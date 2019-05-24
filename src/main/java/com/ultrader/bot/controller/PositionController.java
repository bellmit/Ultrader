package com.ultrader.bot.controller;

import com.ultrader.bot.model.Position;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Position Controller
 * @author ytx1991
 */

@RequestMapping("/api/position")
@RestController
public class PositionController {
    private static Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/getPositions")
    @ResponseBody
    public Iterable<Position> getPositions() {
        try {
            Iterable<Position> positions = TradingAccountMonitor.getPositions().values();
            return positions;
        } catch (Exception e) {
            LOGGER.error("Get positions failed.", e);
            return null;
        }
    }

}
