package com.flixbus.route.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * BusCost.
 *
 * @author Aleksandr_Antipin
 */
@Entity
@Data
@Accessors(chain = true)
public class BusCost {

    @Id
    private Long lineId;

    @Column
    private BigDecimal busCostPerKm;
}