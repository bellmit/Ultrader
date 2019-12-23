package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;

/**
 * Order record model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "tradings")
@Data
@NoArgsConstructor
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
    @Column(name="average_price")
    private Double averagePrice;
    @Column(name="status", nullable=false)
    private String status;
    @Column(name="close_date")
    private Date closeDate;
    @Column(name="reason")
    private String reason;
}
