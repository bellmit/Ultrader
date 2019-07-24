package com.ultrader.bot.model;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;

/**
 * Chart model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "charts")
@Data
public class Chart {
    @Id
    @Column(name="ID", unique=true, updatable=false, nullable=false)
    private long id;


    @Column(name="serialName")
    private String serialName;

    @Column(name="date")
    private Date date;

    @Column(name="value", nullable=false)
    private Double value;
}
