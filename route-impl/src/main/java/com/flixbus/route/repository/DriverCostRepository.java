package com.flixbus.route.repository;

import com.flixbus.route.entity.DriverCost;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ConnectionRepository.
 *
 * @author Aleksandr_Antipin
 */
public interface DriverCostRepository extends JpaRepository<DriverCost, Long> {
}