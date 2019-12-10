package com.ultrader.bot.model;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * History market data
 * @author ytx1991
 */
@EntityScan
@Entity
@Table(name = "history_market_data")
@Data
public class HistoryMarketData {
    @Id
    @Column(name="id", unique=true, updatable=false, nullable=false)
    @GeneratedValue
    private long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="asset_list_id", nullable=false)
    private String assetListName;

    @Column(name="data_start_date", nullable=false)
    private LocalDateTime startDate;

    @Column(name="data_end_date", nullable=false)
    private LocalDateTime endDate;

    @Column(name="is_downloaded", nullable=false)
    private Boolean isDownloaded;

    /**
     * Size of the data, unit KB
     */
    @Column(name="data_size", nullable=false)
    private long size;

    @Column(name="period_seconds", nullable=false)
    private long period;

    @Column(name="asset_count", nullable=false)
    private long assetCount;
}
