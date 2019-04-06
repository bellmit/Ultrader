package com.ultrader.bot.model;


import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

/**
 * setting model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "settings")
@Data
public class Setting {
    @Id
    @Column(name="setting_name", unique=true, updatable=false, nullable=false)
    private String name;

    @Column(name="setting_value", nullable=false)
    private String value;
}
