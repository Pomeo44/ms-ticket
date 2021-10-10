package com.flixbus.route.service.pathfinder.impl;

import com.flixbus.route.entity.Connection;
import com.flixbus.route.repository.ConnectionRepository;
import com.flixbus.route.service.pathfinder.PathFinder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

/**
 * FloidAlgorifmPathFinder.
 *
 * @author Aleksandr_Antipin
 */
@Slf4j
public class FloydWarshallAlgorithmPathFinder implements PathFinder {

    private static final int PATH_NOT_EXIST = -1;
    private static final int ALGORITHM_BIG_NUMBER = 1_000_000_000;

    private final ConnectionRepository connectionRepository;

    private Map<String, Integer> indexCityMap;
    private Map<Integer, String> cityIndexMap;
    private volatile int[][] next;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public FloydWarshallAlgorithmPathFinder(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @PostConstruct
    public void init() {
        log.info("init() - start");
        List<Connection> connectionList = connectionRepository.findAll();
        //create list of unique cities
        List<String> cities = connectionList.stream()
                .map(connection -> List.of(connection.getCity1(), connection.getCity2()))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        log.debug("init() - unique cites={}", cities);
        //prepare maps for fast access
        indexCityMap = cities.stream()
                .collect(Collectors.toMap(Function.identity(), cities::indexOf));
        cityIndexMap = cities.stream()
                .collect(Collectors.toMap(cities::indexOf, Function.identity()));

        //create and initialize matrices
        int[][] routeMatrix = new int[indexCityMap.size()][indexCityMap.size()];
        int[][] futureNext = new int[indexCityMap.size()][indexCityMap.size()];
        for (int i = 0; i < routeMatrix.length; i++) {
            for (int j = 0; j < routeMatrix.length; j++) {
                if (i == j) {
                    routeMatrix[i][j] = 0;
                } else {
                    routeMatrix[i][j] = ALGORITHM_BIG_NUMBER;
                }
                futureNext[i][j] = PATH_NOT_EXIST;
            }
        }

        //fill basic connections. A path from A to B equals B to A
        for (Connection connection : connectionList) {
            int i = indexCityMap.get(connection.getCity1());
            int j = indexCityMap.get(connection.getCity2());
            routeMatrix[i][j] = 1;
            futureNext[i][j] = i;
            //A path from A to B equals B to A
            routeMatrix[j][i] = 1;
            futureNext[j][i] = j;
        }

        log.debug(toStringGrid(routeMatrix, cities));

        calculateMatrix(routeMatrix, futureNext);
        lock.writeLock().lock();
        try {
            next = futureNext;
        } finally {
            lock.writeLock().unlock();
        }
        log.debug(toStringGrid(routeMatrix, cities));
        log.debug(toStringGrid(next, cities));

        log.info("init() - end");
    }

    @Override
    public Optional<List<Route>> findRoutes(String city1, String city2) {
        log.debug("findPaths() - start: city1={}, city2={}", city1, city2);
        Objects.requireNonNull(city1);
        Objects.requireNonNull(city2);

        //check existing city in connections
        if (indexCityMap.get(city1) == null) {
            log.debug("findPaths() - there is no any connection with city1={}", city1);
            return Optional.empty();
        } else if (indexCityMap.get(city2) == null) {
            log.debug("findPaths() - there is no any connection with city2={}", city2);
            return Optional.empty();
        }

        Optional<List<Integer>> pathIndexList = findPathIndexes(indexCityMap.get(city1), indexCityMap.get(city2), next);
        if (pathIndexList.isEmpty()) {
            log.debug("findPaths() - there is no any path between cities: city1={}, city2={}", city1, city2);
            return Optional.empty();
        }
        log.debug("findPaths() - a path includes index cities={}", pathIndexList);
        List<Path> pathList = buildPathsFromIndexes(pathIndexList.get());
        Route route = new Route(pathList);
        log.debug("findPaths() - was prepared the next route={}", route);
        return Optional.of(Collections.singletonList(route));
    }

    @Override
    public void refresh() {
        log.info("refresh() - start");
        init();
        log.info("refresh() - end");
    }

    private void calculateMatrix(int[][] routeMatrix, int[][] next) {
        for (int k = 0; k < routeMatrix.length; ++k) {
            for (int i = 0; i < routeMatrix.length; ++i) {
                for (int j = 0; j < routeMatrix.length; ++j) {
                    if (routeMatrix[i][k] + routeMatrix[k][j] < routeMatrix[i][j]) {
                        routeMatrix[i][j] = routeMatrix[i][k] + routeMatrix[k][j];
                        next[i][j] = next[k][j];
                    }
                }
            }
        }
    }

    private Optional<List<Integer>> findPathIndexes(int indexCity1, int indexCity2, int[][] next) {
        lock.readLock().lock();
        try {
            if (next[indexCity1][indexCity2] == PATH_NOT_EXIST) {
                return Optional.empty();
            }
            int currentIndexCity = indexCity2;
            List<Integer> pathList = new ArrayList<>();
            pathList.add(currentIndexCity);
            while (true) {
                currentIndexCity = next[indexCity1][currentIndexCity];
                if (currentIndexCity == PATH_NOT_EXIST) {
                    break;
                }
                pathList.add(currentIndexCity);
            }
            Collections.reverse(pathList);
            return Optional.of(pathList);
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<Path> buildPathsFromIndexes(List<Integer> pathIndexList) {
        //recovery city pairs
        List<Path> pathList = new ArrayList<>();
        boolean first = true;
        int prevIndexCity = 0;
        for (int indexCity : pathIndexList) {
            if (first) {
                prevIndexCity = indexCity;
                first = false;
                continue;
            }
            String cityFrom = cityIndexMap.get(prevIndexCity);
            String cityTo = cityIndexMap.get(indexCity);
            pathList.add(new Path(cityFrom, cityTo));
            prevIndexCity = indexCity;
        }
        return pathList;
    }

    private String toStringGrid(int[][] routeMatrix, List<String> cities) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        cities.forEach(city -> stringBuilder.append((city + "      ").substring(0, 4) + cities.indexOf(city) + " "));
        stringBuilder.append("\n");
        for (int i = 0; i < routeMatrix.length; i++) {
            for (int j = 0; j < routeMatrix.length; j++) {
                stringBuilder.append(String.format("%5d ", routeMatrix[i][j]));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}