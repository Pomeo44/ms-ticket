package com.flixbus.route.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.flixbus.route.AbstractIntegrationTest;
import com.flixbus.route.entity.Connection;
import com.flixbus.route.service.pathfinder.PathFinder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * ConnectionRepositoryTest.
 *
 * @author Aleksandr_Antipin
 */
public class ConnectionRepositoryTest  extends AbstractIntegrationTest {

    @MockBean
    private PathFinder pathFinder;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    public void cityPairIdTest() {
        Connection connection = new Connection()
                .setCity1("ATest1")
                .setCity2("BTest1")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        Connection saved = connectionRepository.save(connection);

        assertEquals("ATest1BTest1", saved.getId());

        connection = new Connection()
                .setCity1("BTest2")
                .setCity2("ATest2")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        saved = connectionRepository.save(connection);

        assertEquals("ATest2BTest2", saved.getId());
    }

    @Test
    public void cityPairABEqualsBATest() {
        Connection connection = new Connection()
                .setCity1("ATest3")
                .setCity2("BTest3")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        Connection saved = connectionRepository.save(connection);

        Optional<Connection> found = connectionRepository.findById(Connection.generateId("BTest3", "ATest3"));
        assertTrue(found.isPresent());
        assertEquals(found.get().getId(), saved.getId());

        connection = new Connection()
                .setCity1("ATest4")
                .setCity2("BTest4")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        Connection savedAB = connectionRepository.save(connection);

        assertEquals("ATest4BTest4", savedAB.getId());

        connection = new Connection()
                .setCity1("BTest4")
                .setCity2("ATest4")
                .generateId()
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        Connection savedBA = connectionRepository.save(connection);
        assertEquals("ATest4BTest4", savedBA.getId());
        assertEquals(savedAB.getId(), savedBA.getId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void cityPairABRejectBATest() {
        Connection connection = new Connection()
                .setCity1("ATest5")
                .setCity2("BTest5")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        connectionRepository.save(connection);

        connection = new Connection()
                .setCity1("BTest5")
                .setCity2("ATest5")
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        connectionRepository.save(connection);
    }

    @Test
    public void listenerTest() {
        Connection connection = new Connection()
                .setCity1("ATest6")
                .setCity2("BTest6")
                .generateId()
                .setDuration(BigDecimal.TEN)
                .setDistance(BigDecimal.TEN)
                .setLineId(1L);
        connectionRepository.save(connection);
        verify(pathFinder).refresh();
    }
}
