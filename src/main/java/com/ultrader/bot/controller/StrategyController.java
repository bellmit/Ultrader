package com.ultrader.bot.controller;

import com.ultrader.bot.dao.StrategyDao;
import com.ultrader.bot.model.Strategy;
import com.ultrader.bot.monitor.TradingStrategyMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Strategy Controller
 * @author ytx1991
 */

@RequestMapping("/strategy")
@RestController
public class StrategyController {
    private static Logger LOGGER = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    private StrategyDao strategyDao;

    @RequestMapping(method = RequestMethod.POST, value = "/addStrategy")
    @ResponseBody
    public Strategy saveStrategy(@RequestBody Strategy strategy) {
        try {
            Strategy savedStrategy = strategyDao.save(strategy);
            return savedStrategy;
        } catch (Exception e) {
            LOGGER.error("Save strategy failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getStrategy")
    @ResponseBody
    public Strategy getStrategy(@PathVariable Long id) {
        try {
            Optional<Strategy> strategy = strategyDao.findById(id);
            return strategy.get();
        } catch (Exception e) {
            LOGGER.error("Get strategy failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reload")
    @ResponseBody
    public boolean reload() {
        try {
            synchronized (TradingStrategyMonitor.lock) {
                TradingStrategyMonitor.strategies.clear();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Reload strategy failed.", e);
            return false;
        }
    }
}
