package com.ultrader.bot.model;


import com.ultrader.bot.util.SettingConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private static Set<String> SUPPORT_SETTINGS = new HashSet<>();
    static  {
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_BUY_STRATEGY.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_SELL_STRATEGY.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_PRICE_LIMIT_MAX.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_PRICE_LIMIT_MIN.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_VOLUME_LIMIT_MAX.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_VOLUME_LIMIT_MIN.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_BUY_MAX_LIMIT.getName());
        SUPPORT_SETTINGS.add(SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName());
        SUPPORT_SETTINGS = Collections.unmodifiableSet(SUPPORT_SETTINGS);
    }

    public static Set<String> getSupportSettings() {
        return SUPPORT_SETTINGS;
    }
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
