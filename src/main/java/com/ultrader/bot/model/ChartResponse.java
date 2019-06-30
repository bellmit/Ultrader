package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Front-End Chart response
 * @author ytx1991
 */
public class ChartResponse {
    private String name;
    private List<String> labels;
    private List<List<Double>> series;
}
