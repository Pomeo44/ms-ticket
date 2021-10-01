package com.flixbus.route;

import com.flixbus.route.repository.BusCostRepository;
import com.flixbus.route.repository.ConnectionRepository;
import com.flixbus.route.repository.DriverCostRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AbstractIntegrationTest.
 *
 * @author Aleksandr_Antipin
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private BusCostRepository busCostRepository;
    @Autowired
    private DriverCostRepository driverCostRepository;

}