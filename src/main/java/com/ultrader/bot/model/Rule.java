package com.ultrader.bot.model;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;

/**
 * Order rule model
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "rules")
@Data
public class Rule {
    @Id
    @Column(name="ID", unique=true, updatable=false, nullable=false)
    @GeneratedValue
    private long id;


    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="type", nullable=false)
    private String type;

    @Column(name="formula", nullable=false)
    private String formula;
}
