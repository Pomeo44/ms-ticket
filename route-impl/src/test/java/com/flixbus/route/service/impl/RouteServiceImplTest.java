package com.flixbus.route.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.flixbus.route.entity.BusCost;
import com.flixbus.route.entity.Connection;
import com.flixbus.route.entity.DriverCost;
import com.flixbus.route.openapi.model.Route;
import com.flixbus.route.repository.BusCostRepository;
import com.flixbus.route.repository.ConnectionRepository;
import com.flixbus.route.repository.DriverCostRepository;
import com.flixbus.route.service.pathfinder.PathFinder;
import com.flixbus.route.service.pathfinder.PathFinder.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * RouteServiceImplTest.
 *
 * @author Aleksandr_Antipin
 */
@RunWith(SpringRunner.class)
public class RouteServiceImplTest {

    private static final String CITY_FROM = "City1";
    private static final String CITY_BETWEEN = "City2";
    private static final String CITY_TO = "City3";
    private static final BigDecimal DISTANCE12 = BigDecimal.TEN;
    private static final BigDecimal DISTANCE23 = BigDecimal.TEN.add(BigDecimal.TEN);
    private static final BigDecimal DURATION12 = BigDecimal.TEN;
    private static final BigDecimal DURATION23 = BigDecimal.TEN.add(BigDecimal.TEN);
    private static final Long LINE_1_ID = 1L;
    private static final Long LINE_2_ID = 2L;
    private static final BigDecimal LINE_1_BUS = BigDecimal.valueOf(300L);
    private static final BigDecimal LINE_2_BUS = BigDecimal.valueOf(400L);
    private static final BigDecimal LINE_1_DRIVER = BigDecimal.valueOf(500L);
    private static final BigDecimal LINE_2_DRIVER = BigDecimal.valueOf(600L);

    @Mock
    private PathFinder pathFinder;
    @Mock
    private ConnectionRepository connectionRepository;
    @Mock
    private BusCostRepository busCostRepository;
    @Mock
    private DriverCostRepository driverCostRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    @Before
    public void setUp() {
        List<Path> pathList = List.of(
                new Path(CITY_FROM, CITY_BETWEEN),
                new Path(CITY_BETWEEN, CITY_TO));
        when(pathFinder.findRoutes(eq(CITY_FROM), eq(CITY_TO))).thenReturn(Optional.of(Collections.singletonList(new PathFinder.Route(pathList))));
        when(connectionRepository.findAllById(eq(Set.of(CITY_FROM + CITY_BETWEEN, CITY_BETWEEN + CITY_TO))))
                .thenReturn(List.of(
                        new Connection().setId(CITY_FROM + CITY_BETWEEN).setCity1(CITY_FROM).setCity2(CITY_BETWEEN).setDistance(DISTANCE12).setDuration(DURATION12).setLineId(LINE_1_ID),
                        new Connection().setId(CITY_BETWEEN + CITY_TO).setCity1(CITY_BETWEEN).setCity2(CITY_TO).setDistance(DISTANCE23).setDuration(DURATION23).setLineId(LINE_2_ID)
                ));
        when(busCostRepository.findAllById(eq(Set.of(LINE_1_ID, LINE_2_ID)))).thenReturn(List.of(
                new BusCost().setLineId(LINE_1_ID).setBusCostPerKm(LINE_1_BUS),
                new BusCost().setLineId(LINE_2_ID).setBusCostPerKm(LINE_2_BUS)));
        when(driverCostRepository.findAllById(eq(Set.of(LINE_1_ID, LINE_2_ID)))).thenReturn(List.of(
                new DriverCost().setLineId(LINE_1_ID).setDriverCostPerHr(LINE_1_DRIVER),
                new DriverCost().setLineId(LINE_2_ID).setDriverCostPerHr(LINE_2_DRIVER)));
    }

    @Test
    public void successTest() {
        Route route = routeService.findAndCalculateRoute(CITY_FROM, CITY_TO);
        assertNotNull(route);
        assertEquals(Integer.valueOf(2), route.getNumLines());
        //10 * 300 + 20 * 400 = 11000
        assertEquals(BigDecimal.valueOf(11000), route.getTotalBusCost());
        //10 * 500 + 20 * 600 = 17000
        assertEquals(BigDecimal.valueOf(17000), route.getTotalDriverCost());
        //11000 + 17000 = 28000
        assertEquals(BigDecimal.valueOf(28000), route.getTotalCost());

    }

    @Test(expected = NullPointerException.class)
    public void city1NullTest() {
        routeService.findAndCalculateRoute(null, CITY_TO);
    }

    @Test(expected = NullPointerException.class)
    public void city2NullTest() {
        routeService.findAndCalculateRoute(CITY_FROM, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathNotExistTest() {
        routeService.findAndCalculateRoute(CITY_FROM, "Wrong");
    }
}