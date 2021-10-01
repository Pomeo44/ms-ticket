package com.flixbus.route.service;

import com.flixbus.route.openapi.model.Route;

/**
 * RouteServece.
 *
 * @author Aleksandr_Antipin
 */
public interface RouteService {

    Route findAndCalculateRoute(String city1, String city2);
}