package com.flixbus.route.listener;

import com.flixbus.route.entity.Connection;
import com.flixbus.route.service.pathfinder.PathFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * ConnectionHibernateEventListener.
 *
 * @author Aleksandr_Antipin
 */
@Component
@Slf4j
public class ConnectionJpaEventListener {

    private PathFinder pathFinder;

    public ConnectionJpaEventListener() {
    }

    @Autowired
    public ConnectionJpaEventListener(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    public void onEvent(Connection connection) {
        log.info("onEvent() - start: connection={}", connection);
        pathFinder.refresh();
        log.info("onEvent() - end");
    }
}