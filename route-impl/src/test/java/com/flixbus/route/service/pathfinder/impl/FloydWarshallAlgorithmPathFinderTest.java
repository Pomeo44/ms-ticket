package com.flixbus.route.service.pathfinder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.flixbus.route.AbstractIntegrationTest;
import com.flixbus.route.service.pathfinder.PathFinder;
import com.flixbus.route.service.pathfinder.PathFinder.Path;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * FloydWarshallAlgorithmPathFinderTest.
 *
 * @author Aleksandr_Antipin
 */
public class FloydWarshallAlgorithmPathFinderTest extends AbstractIntegrationTest {

    @Autowired
    private FloydWarshallAlgorithmPathFinder pathFinder;

    @Test
    public void onePathTest() {
        Optional<List<PathFinder.Route>> routes = pathFinder.findRoutes("Munich", "Stuttgart");
        assertTrue(routes.isPresent());
        assertEquals(1, routes.get().size());
        assertEquals("Munich", routes.get().get(0).getPaths().get(0).getCityFrom());
        assertEquals("Stuttgart", routes.get().get(0).getPaths().get(0).getCityTo());
    }

    @Test
    public void manyPathsTest() {
        Optional<List<PathFinder.Route>> routeListOptional = pathFinder.findRoutes("Warsaw", "Amsterdam");
        assertTrue(routeListOptional.isPresent());
        List<Path> pathList = routeListOptional.get().get(0).getPaths();
        assertEquals(8, pathList.size());
        assertEquals("Warsaw", pathList.get(0).getCityFrom());
        assertEquals("Berlin", pathList.get(0).getCityTo());
        assertEquals("Berlin", pathList.get(1).getCityFrom());
        assertEquals("Munich", pathList.get(1).getCityTo());
        assertEquals("Munich", pathList.get(2).getCityFrom());
        assertEquals("Stuttgart", pathList.get(2).getCityTo());
        assertEquals("Stuttgart", pathList.get(3).getCityFrom());
        assertEquals("Freiburg", pathList.get(3).getCityTo());
        assertEquals("Freiburg", pathList.get(4).getCityFrom());
        assertEquals("Strasbourg", pathList.get(4).getCityTo());
        assertEquals("Strasbourg", pathList.get(5).getCityFrom());
        assertEquals("Brussels", pathList.get(5).getCityTo());
        assertEquals("Brussels", pathList.get(6).getCityFrom());
        assertEquals("Cologne", pathList.get(6).getCityTo());
        assertEquals("Cologne", pathList.get(7).getCityFrom());
        assertEquals("Amsterdam", pathList.get(7).getCityTo());
    }

    @Test
    public void pathNotExistTest() {
        Optional<List<PathFinder.Route>> routes = pathFinder.findRoutes("Munich", "Madrid");
        assertTrue(routes.isEmpty());
    }


}