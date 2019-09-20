package com.ultrader.bot.util;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OptimizationType {
    AVG_PROFIT,
    COMPOUND_PROFIT
}
