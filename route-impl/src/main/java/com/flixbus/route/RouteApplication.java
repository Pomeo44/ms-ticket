package com.flixbus.route;

import com.flixbus.route.repository.ConnectionRepository;
import com.flixbus.route.service.pathfinder.PathFinder;
import com.flixbus.route.service.pathfinder.impl.FloydWarshallAlgorithmPathFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EnableDiscoveryClient
public class RouteApplication {

    public static void main(String[] args) {
        log.info("Version: {}", RouteApplication.class.getPackage().getImplementationVersion());
        SpringApplication.run(RouteApplication.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public PathFinder pathFinder(ConnectionRepository connectionRepository) {
        return new FloydWarshallAlgorithmPathFinder(connectionRepository);
    }
}
