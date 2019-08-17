package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EntityScan
@Entity
@Table(name = "asset_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetList {
    @Id
    @Column(name="ID", unique=true, updatable=false, nullable=false)
    private String name;
    @Column(name="symbols", nullable=false)
    private String symbols;
}
