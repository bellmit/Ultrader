package com.ultrader.bot.model.polygon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Polygon Aggregation API Entity
 * @author ytx1991
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggResponse {
    private String ticker;
    private String status;
    private boolean adjusted;
    private int queryCount;
    private int resultsCount;
    private List<Aggv2> results;
}
