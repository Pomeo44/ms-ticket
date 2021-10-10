package com.flixbus.route.service.impl;

import static java.math.RoundingMode.HALF_UP;

import com.flixbus.route.entity.BusCost;
import com.flixbus.route.entity.Connection;
import com.flixbus.route.entity.DriverCost;
import com.flixbus.route.openapi.model.Route;
import com.flixbus.route.repository.BusCostRepository;
import com.flixbus.route.repository.ConnectionRepository;
import com.flixbus.route.repository.DriverCostRepository;
import com.flixbus.route.service.RouteService;
import com.flixbus.route.service.pathfinder.PathFinder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RouteServiceImpl.
 *
 * @author Aleksandr_Antipin
 */
@Service
@AllArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private PathFinder pathFinder;
    private ConnectionRepository connectionRepository;
    private BusCostRepository busCostRepository;
    private DriverCostRepository driverCostRepository;

    @Override
    public Route findAndCalculateRoute(String city1, String city2) {
        log.debug("findAndCalculateRoute() - start: city1={}, city2={}", city1, city2);
        Objects.requireNonNull(city1);
        Objects.requireNonNull(city2);

        List<PathFinder.Route> routes = pathFinder.findRoutes(city1, city2)
                .orElseThrow(() -> new IllegalArgumentException("Route doesn't exist"));
        log.debug("findAndCalculateRoute() - found paths={}", routes);

        Set<String> connectionIds = routes.stream()
                .map(PathFinder.Route::getPaths)
                .flatMap(Collection::stream)
                .map(path -> Connection.generateId(path.getCityFrom(), path.getCityTo()))
                .collect(Collectors.toSet());
        List<Connection> connections = connectionRepository.findAllById(connectionIds);
        log.debug("findAndCalculateRoute() - for city pairs found connections={}", connections);

        Route route = calculateRoute(connections);
        log.debug("findAndCalculateRoute() - end: route={}", route);
        return route;
    }

    private Route calculateRoute(List<Connection> connections) {
        Set<Long> lineIds = connections.stream()
                .map(Connection::getLineId)
                .collect(Collectors.toSet());
        Map<Long, BigDecimal> busCostMap = busCostRepository.findAllById(lineIds).stream()
                .collect(Collectors.toMap(BusCost::getLineId, BusCost::getBusCostPerKm));
        Map<Long, BigDecimal> driverCostMap = driverCostRepository.findAllById(lineIds).stream()
                .collect(Collectors.toMap(DriverCost::getLineId, DriverCost::getDriverCostPerHr));

        BigDecimal totalBusCost = BigDecimal.ZERO;
        BigDecimal totalDriverCost = BigDecimal.ZERO;

        for (Connection connection : connections) {
            totalBusCost = totalBusCost.add(connection.getDistance().multiply(busCostMap.get(connection.getLineId())));
            totalDriverCost = totalDriverCost.add(connection.getDuration().multiply(driverCostMap.get(connection.getLineId())));
        }

        return new Route()
                .numLines(lineIds.size())
                .totalBusCost(totalBusCost.setScale(2, HALF_UP))
                .totalDriverCost(totalDriverCost.setScale(2, HALF_UP))
                .totalCost(BigDecimal.ZERO.add(totalBusCost).add(totalDriverCost).setScale(2, HALF_UP));
    }

}