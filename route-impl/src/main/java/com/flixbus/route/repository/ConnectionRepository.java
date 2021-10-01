package com.flixbus.route.repository;

import com.flixbus.route.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ConnectionRepository.
 *
 * @author Aleksandr_Antipin
 */
public interface ConnectionRepository extends JpaRepository<Connection, String> {
}