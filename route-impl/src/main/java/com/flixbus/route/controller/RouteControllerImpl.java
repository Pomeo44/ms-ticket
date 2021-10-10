package com.flixbus.route.controller;

import com.flixbus.route.api.RouteController;
import com.flixbus.route.openapi.model.Route;
import com.flixbus.route.openapi.model.Routes;
import com.flixbus.route.service.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * RouteControllerImpl.
 *
 * @author Aleksandr_Antipin
 */
@RestController
@AllArgsConstructor
public class RouteControllerImpl implements RouteController {

    private RouteService routeService;

    @Override
    public ResponseEntity allroutesGet(String cityFrom, String cityTo) {
        return null;
    }

    @Override
    public ResponseEntity bestrouteGet(String cityFrom, String cityTo) {
        try {
            validation(cityFrom, cityTo);
            Route route = routeService.findAndCalculateRoute(cityFrom, cityTo);
            return new ResponseEntity<>(route, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("route", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validation(String cityFrom, String cityTo) {
        if (cityFrom == null) {
            throw new IllegalArgumentException("Bad request. cityFrom is null");
        }
        if (cityTo == null) {
            throw new IllegalArgumentException("Bad request. cityTo is null");
        }
    }
}