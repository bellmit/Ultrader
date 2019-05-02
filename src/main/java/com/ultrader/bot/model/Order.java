package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

/**
 * Order record model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "tradings")
@Data
@AllArgsConstructor
public class Order {
    @Id
    @Column(name="ID", unique=true, updatable=false, nullable=false)
    private String id;
    @Column(name="symbol", updatable = false, nullable=false)
    private String symbol;
    @Column(name="side", updatable = false, nullable=false)
    private String side;
    @Column(name="type", updatable = false, nullable=false)
    private String type;
    @Column(name="quantity", updatable = false, nullable=false)
    private int quantity;
    @Column(name="average_price", nullable=false)
    private double averagePrice;
    @Column(name="status", nullable=false)
    private String status;

}
