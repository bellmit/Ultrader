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
@Table(name = "settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    @Id
    @Column(name="setting_name", unique=true, updatable=false, nullable=false)
    private String name;

    @Column(name="setting_value", nullable=false)
    private String value;
}
