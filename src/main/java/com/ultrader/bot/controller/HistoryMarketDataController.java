package com.ultrader.bot.controller;

import com.ultrader.bot.dao.HistoryMarketDataDao;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.model.HistoryMarketData;
import com.ultrader.bot.model.Rule;
import com.ultrader.bot.service.TradingPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * History MarketData Controller
 * @author ytx1991
 */

@RequestMapping("/api/historymarketdata")
@RestController("HistoryMarketDataController")
public class HistoryMarketDataController {
    private static Logger LOGGER = LoggerFactory.getLogger(HistoryMarketDataController.class);

    @Autowired
    private HistoryMarketDataDao historyMarketDataDao;
    @Autowired
    private TradingPlatform tradingPlatform;

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public ResponseEntity<HistoryMarketData> saveHistoryMarketData(@RequestBody HistoryMarketData historyMarketData) {
        try {
            HistoryMarketData saved = historyMarketDataDao.save(historyMarketData);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            LOGGER.error("Save history market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get/{id}")
    public ResponseEntity<HistoryMarketData> getHistoryMarketData(@PathVariable Long id) {
        try {
            Optional<HistoryMarketData> historyMarketData = historyMarketDataDao.findById(id);
            return ResponseEntity.of(historyMarketData);
        } catch (Exception e) {
            LOGGER.error("Get history market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity<Iterable<HistoryMarketData>> getHistoryMarketDataList() {
        try {
            Iterable<HistoryMarketData> data = historyMarketDataDao.findAll();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            LOGGER.error("Get history market data list failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<HistoryMarketData> deleteHistoryMarketData(@PathVariable Long id) {
        try {
            historyMarketDataDao.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error("Delete history market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/download/{id}")
    public ResponseEntity<HistoryMarketData> downloadHistoryMarketData(@PathVariable Long id) {
        try {
            Optional<HistoryMarketData> historyMarketData = historyMarketDataDao.findById(id);
            if (historyMarketData.isPresent()) {
                downloadMarketData(historyMarketData.get());
            } else {
                LOGGER.error("Cannot not find history market data record for id {}", id);
            }
            return ResponseEntity.of(historyMarketData);
        } catch (Exception e) {
            LOGGER.error("Download history market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/download/{id}")
    public ResponseEntity<HistoryMarketData> removeHistoryMarketData(@PathVariable Long id) {
        try {
            Optional<HistoryMarketData> historyMarketData = historyMarketDataDao.findById(id);
            if (historyMarketData.isPresent()) {
                deleteMarketData(historyMarketData.get());
            } else {
                LOGGER.error("Cannot not find history market data record for id {}", id);
            }
            return ResponseEntity.of(historyMarketData);
        } catch (Exception e) {
            LOGGER.error("Delete history market data download failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private void downloadMarketData(HistoryMarketData historyMarketData) {

    }

    private void deleteMarketData(HistoryMarketData historyMarketData) {

    }
}
