package com.flixbus.route.entity;

import com.flixbus.route.listener.ConnectionJpaEventListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Connection.
 *
 * @author Aleksandr_Antipin
 */
@Entity
@Data
@Accessors(chain = true)
@EntityListeners(ConnectionJpaEventListener.class)
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private Long lineId;

    @Column
    private String city1;

    @Column
    private String city2;

    @Column
    private BigDecimal distance;

    @Column
    private BigDecimal duration;

    public Connection generateId() {
        this.id = Stream.of(this.city1, this.city2)
                .sorted()
                .collect(Collectors.joining());
        return this;
    }

    public static String generateId(String city1, String city2) {
        return Stream.of(city1, city2)
                .sorted()
                .collect(Collectors.joining());
    }

}