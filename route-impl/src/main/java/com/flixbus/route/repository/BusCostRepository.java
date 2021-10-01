package com.flixbus.route.repository;

import com.flixbus.route.entity.BusCost;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ConnectionRepository.
 *
 * @author Aleksandr_Antipin
 */
public interface BusCostRepository extends JpaRepository<BusCost, Long> {
}