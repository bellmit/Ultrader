package com.ultrader.bot.controller;

import com.ultrader.bot.model.Order;
import com.ultrader.bot.model.Position;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Position Controller
 * @author ytx1991
 */

@RequestMapping("/api/position")
@RestController("PositionController")
public class PositionController {
    private static Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/getPositions")
    @ResponseBody
    public Iterable<Position> getPositions() {
        try {
            Iterable<Position> positions = TradingAccountMonitor.getPositions().values();
            return ((Collection<Position>) positions).stream().filter(p -> !TradingStrategyMonitor.getOpenOrders().containsKey(p.getSymbol())).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Get positions failed.", e);
            return null;
        }
    }

}
