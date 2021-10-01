package com.flixbus.route.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flixbus.route.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * RouteControllerImplTest.
 *
 * @author Aleksandr_Antipin
 */
public class RouteControllerImplTest extends AbstractIntegrationTest {


    @Autowired
    private RouteControllerImpl routeController;

    @Test
    public void successTest() throws Exception {
        //Stuttgart,Zurich,500,5,4
        //Munich,Stuttgart,800,8,3
        //Bus cost
        //3,1.414111432
        //4,5.659771674
        //Driver cost
        //3,5.582751583
        //4,10.15811534
        //500 * 5.659771674 + 800 * 1.414111432 = 2829,885837 + 1131,2891456 = 3961,1749826
        //5 * 10.15811534 + 8 * 5.582751583 = 50,7905767 + 44,662012664 = 95,452589364
        //3961,1749826 + 95,452589364 = 4056,627571964
        mockMvc.perform(get("/route?cityFrom=Zurich&cityTo=Munich"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.num_lines").value(2))
                .andExpect(jsonPath("$.total_bus_cost").value(BigDecimal.valueOf(3961.17)))
                .andExpect(jsonPath("$.total_driver_cost").value(BigDecimal.valueOf(95.45)))
                .andExpect(jsonPath("$.total_cost").value(BigDecimal.valueOf(4056.63)))
                .andReturn();
    }

    @Test
    public void cityToNullTest() throws Exception {
        mockMvc.perform(get("/route?cityFrom=Zurich"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Bad request. cityTo is null"))
                .andReturn();
    }

    @Test
    public void pathNotExistTest() throws Exception {
        mockMvc.perform(get("/route?cityFrom=Lisbon&cityTo=Munich"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Route doesn't exist"))
                .andReturn();
    }

}