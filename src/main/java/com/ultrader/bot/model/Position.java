package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Internal position model
 */
@EntityScan
@Entity
@Table(name = "positions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {
    @Id
    @Column(name="symbol", unique=true, updatable=false, nullable=false)
    private String symbol;
    @Column(name="quantity", nullable=false)
    private int quantity;
    @Column(name="average_cost", nullable=false)
    private double averageCost;
    @Column(name="buy_date", nullable=false)
    private Date buyDate;
    @Column(name="current_price", nullable=false)
    private double currentPrice;
    @Column(name="exchange")
    private String exchange;
    @Column(name="market_value")
    private double marketValue;
    @Column(name="change_today")
    private double changeToday;
}
