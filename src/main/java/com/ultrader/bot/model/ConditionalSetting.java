package com.ultrader.bot.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

/**
 * setting model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "conditional_settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionalSetting {
    @Id
    @Column(name="id", unique=true, updatable=false, nullable=false)
    @GeneratedValue
    private Long id;

    @Column(name="market_trend", nullable=false)
    private String marketTrend;

    @Column(name="setting_name", nullable=false)
    private String settingName;

    @Column(name="setting_value", nullable=false)
    private String settingValue;
}
