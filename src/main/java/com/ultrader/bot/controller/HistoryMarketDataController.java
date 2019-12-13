package com.ultrader.bot.controller;

import com.ultrader.bot.dao.AssetListDao;
import com.ultrader.bot.dao.HistoryMarketDataDao;
import com.ultrader.bot.dao.RuleDao;
import com.ultrader.bot.model.AssetList;
import com.ultrader.bot.model.HistoryMarketData;
import com.ultrader.bot.model.ProgressMessage;
import com.ultrader.bot.model.Rule;
import com.ultrader.bot.monitor.MarketDataMonitor;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.HistoryMarketDataUtil;
import com.ultrader.bot.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * History MarketData Controller
 * @author ytx1991
 */

@RequestMapping("/api/historymarketdata")
@RestController("HistoryMarketDataController")
public class HistoryMarketDataController {
    private static Logger LOGGER = LoggerFactory.getLogger(HistoryMarketDataController.class);
    private static int DOWNLOAD_BATCH_SIZE = 20;
    private static String DOWNLOAD_TOPIC = "/topic/progress/download";
    @Autowired
    private HistoryMarketDataDao historyMarketDataDao;
    @Autowired
    private AssetListDao assetListDao;
    @Autowired
    private TradingPlatform tradingPlatform;
    @Autowired
    private NotificationService notifier;

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public ResponseEntity<HistoryMarketData> saveHistoryMarketData(@RequestBody HistoryMarketData historyMarketData) {
        try {
            historyMarketData.setIsDownloaded(false);
            historyMarketData.setSize(0);
            historyMarketData.setAssetCount(0);
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

    @RequestMapping(method = RequestMethod.GET, value = "/getCurrentMarketData/{stock}")
    public ResponseEntity<String> getCurrentMarketData(@PathVariable String stock) {
        try {
            if (MarketDataMonitor.timeSeriesMap.containsKey(stock)) {
                StringBuilder sb = new StringBuilder();
                MarketDataMonitor.timeSeriesMap.get(stock).getBarData().stream().forEach(b -> sb.append(
                        String.format("EndTime:%s, Open:%.2f, Close:%.2f, High:%.2f, Low:%.2f \n",
                                b.getEndTime().toString(),
                                b.getOpenPrice().doubleValue(),
                                b.getClosePrice().doubleValue(),
                                b.getMaxPrice().doubleValue(),
                                b.getMinPrice().doubleValue())));
                return ResponseEntity.ok(sb.toString());
            } else {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            LOGGER.error("Get current market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<HistoryMarketData> deleteHistoryMarketData(@PathVariable Long id) {
        try {

            Optional<HistoryMarketData> historyMarketData = historyMarketDataDao.findById(id);
            if (historyMarketData.isPresent()) {
                deleteMarketData(historyMarketData.get());
                historyMarketDataDao.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                LOGGER.error("Market data not exist.");
                return ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            LOGGER.error("Delete history market data failed.", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Download market data and save into the files
     * @param id
     * @return
     */
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

    /**
     * Delete the downloaded files
     * @param id
     * @return
     */
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

    /**
     * Download market data and save into the files
     * @param historyMarketData
     */
    private void downloadMarketData(HistoryMarketData historyMarketData) {
        //Load asset list
        Optional<AssetList> assetList = assetListDao.findById(historyMarketData.getAssetListName());
        if (!assetList.isPresent()) {
            LOGGER.error("Cannot download market data {}, since asset list {} is not exist", historyMarketData.getName(), historyMarketData.getAssetListName());
            notifier.sendNotification("Download Market Data Failed", "Cannot download market data " + historyMarketData.getName() + ", since asset list is not exist.", NotificationType.ERROR);
            return;
        }
        //Download market data
        String[] assets = assetList.get().getSymbols().split(",");
        List<TimeSeries> timeSeriesList = new ArrayList<>();
        int count = 0;
        int validCount = 0;
        int total = assets.length;
        if (total == 0) {
            LOGGER.error("No asset defined in the list {}", historyMarketData.getAssetListName());
            notifier.sendNotification("Download Market Data Failed", "No asset defined in the list " + historyMarketData.getName(), NotificationType.ERROR);
            return;
        }
        for (String symbol : assets) {
            TimeSeries timeSeries = new BaseTimeSeries(symbol);
            timeSeriesList.add(timeSeries);
            if (timeSeriesList.size() == DOWNLOAD_BATCH_SIZE) {
                notifier.getNotifier().convertAndSend(DOWNLOAD_TOPIC, new ProgressMessage("InProgress", "Downloading " + timeSeriesList.stream().map(s -> s.getName()).collect(Collectors.toList()),100 * count / total));
                try {
                    //Load market data
                    tradingPlatform.getMarketDataService().getTimeSeries(timeSeriesList, historyMarketData.getPeriod() * 1000, historyMarketData.getStartDate(), historyMarketData.getEndDate(), null, null);
                    //Save to file
                    validCount += HistoryMarketDataUtil.timeSeriesToFile(timeSeriesList, historyMarketData.getId());

                } catch (Exception e) {
                    LOGGER.error("Load back test data failed.", e);
                }
                count += DOWNLOAD_BATCH_SIZE;
                timeSeriesList.clear();
            }
        }
        if (timeSeriesList.size() > 0) {
            notifier.getNotifier().convertAndSend(DOWNLOAD_TOPIC, new ProgressMessage("InProgress", "Downloading " + timeSeriesList.stream().map(s -> s.getName()).collect(Collectors.toList()),100 * count / total));
            try {
                //Load market data
                tradingPlatform.getMarketDataService().getTimeSeries(timeSeriesList, historyMarketData.getPeriod() * 1000, historyMarketData.getStartDate(), historyMarketData.getEndDate(), null, null);
                //Save to file
                validCount += HistoryMarketDataUtil.timeSeriesToFile(timeSeriesList, historyMarketData.getId());
            } catch (Exception e) {
                LOGGER.error("Load back test data failed.", e);
            }
        }
        notifier.getNotifier().convertAndSend(DOWNLOAD_TOPIC, new ProgressMessage("Completed", "Download Completed",100));
        //Update history market data
        historyMarketData.setIsDownloaded(true);
        historyMarketData.setAssetCount(validCount);
        historyMarketData.setSize(HistoryMarketDataUtil.getFolderSize(historyMarketData.getId()));
        historyMarketDataDao.save(historyMarketData);
    }

    /**
     * Delete the downloaded files
     * @param historyMarketData
     */
    private void deleteMarketData(HistoryMarketData historyMarketData) {
        historyMarketData.setIsDownloaded(false);
        historyMarketData.setAssetCount(0);
        historyMarketData.setSize(0);
        historyMarketDataDao.save(historyMarketData);
        if(!HistoryMarketDataUtil.deleteDataFolder(historyMarketData.getId())){
            LOGGER.error("Cannot delete market data");
        }
    }
}
