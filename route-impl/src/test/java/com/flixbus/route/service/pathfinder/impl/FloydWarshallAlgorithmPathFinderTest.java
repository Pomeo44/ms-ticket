package com.flixbus.route.service.pathfinder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.flixbus.route.AbstractIntegrationTest;
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
        Optional<List<Path>> pathList = pathFinder.findPaths("Munich", "Stuttgart");
        assertTrue(pathList.isPresent());
        assertEquals(1, pathList.get().size());
        assertEquals("Munich", pathList.get().get(0).getCityFrom());
        assertEquals("Stuttgart", pathList.get().get(0).getCityTo());
    }

    @Test
    public void manyPathsTest() {
        Optional<List<Path>> pathListOptional = pathFinder.findPaths("Warsaw", "Amsterdam");
        assertTrue(pathListOptional.isPresent());
        List<Path> pathList = pathListOptional.get();
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
        Optional<List<Path>> pathList = pathFinder.findPaths("Munich", "Madrid");
        assertTrue(pathList.isEmpty());
    }


}