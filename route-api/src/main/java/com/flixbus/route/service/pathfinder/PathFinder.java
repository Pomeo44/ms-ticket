package com.flixbus.route.service.pathfinder;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * PathFinder.
 *
 * @author Aleksandr_Antipin
 */
public interface PathFinder {

    Optional<List<Route>> findRoutes(String city1, String city2);

    void refresh();

    @Data
    @AllArgsConstructor
    class Route {
        private List<Path> paths;
    }

    @Data
    @AllArgsConstructor
    class Path {
        private String cityFrom;
        private String cityTo;
    }
}