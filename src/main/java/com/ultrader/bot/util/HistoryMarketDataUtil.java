package com.ultrader.bot.util;

import com.ultrader.bot.model.HistoryMarketData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.PrecisionNum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Util for History Market Data
 *
 * @author ytx1991
 */
public class HistoryMarketDataUtil {
    private static String DEFAULT_PATH = "data/marketdata/";
    private static Logger LOGGER = LoggerFactory.getLogger(HistoryMarketDataUtil.class);

    /**
     * Persist TimeSeries to file
     *
     * @param timeSeriesList
     * @param mid
     * @return
     * @throws IOException
     */
    public static int timeSeriesToFile(List<TimeSeries> timeSeriesList, long mid) throws IOException {
        int validCount = 0;
        String folder = DEFAULT_PATH + mid;
        File file = new File(folder);
        file.mkdirs();
        for (TimeSeries timeSeries : timeSeriesList) {
            if (timeSeries.getBarCount() == 0) {
                continue;
            }
            validCount++;
            FileWriter fileWriter = new FileWriter(folder + "/" + timeSeries.getName());
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for (Bar bar : timeSeries.getBarData()) {
                printWriter.printf("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        bar.getEndTime().toEpochSecond(),
                        bar.getOpenPrice().doubleValue(),
                        bar.getMaxPrice().doubleValue(),
                        bar.getMinPrice().doubleValue(),
                        bar.getClosePrice().doubleValue(),
                        bar.getVolume().doubleValue(),
                        bar.getAmount().doubleValue());
            }
            printWriter.close();
        }
        return validCount;
    }

    /**
     * Delete a specific history market data folder
     *
     * @param mid
     * @return
     */
    public static boolean deleteDataFolder(long mid) {
        return FileSystemUtils.deleteRecursively(new File(DEFAULT_PATH + mid));
    }

    /**
     * Read market data from file to TimeSeries
     *
     * @param historyMarketData
     * @param symbol
     * @return
     */
    public static TimeSeries fileToTimeSeries(HistoryMarketData historyMarketData, String symbol) {
        String file = DEFAULT_PATH + historyMarketData.getId() + "/" + symbol;
        BufferedReader reader = null;
        TimeSeries timeSeries = new BaseTimeSeries(symbol);
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader != null) {
                String currentLine = reader.readLine();
                if (currentLine == null) {
                    break;
                }
                String[] token = currentLine.split(",");
                Instant i = Instant.ofEpochSecond(Long.parseLong(token[0]));
                ZonedDateTime endDate = ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE));
                timeSeries.addBar(
                        new BaseBar(
                                Duration.ofSeconds(historyMarketData.getPeriod()),
                                endDate,
                                PrecisionNum.valueOf(token[1]),
                                PrecisionNum.valueOf(token[2]),
                                PrecisionNum.valueOf(token[3]),
                                PrecisionNum.valueOf(token[4]),
                                PrecisionNum.valueOf(token[5]),
                                PrecisionNum.valueOf(token[6])));
            }
            reader.close();
            return timeSeries;
        } catch (Exception e) {
            LOGGER.error("Cannot read market data {} / {}", historyMarketData.getName(), symbol, e);
            return null;
        }
    }

    /**
     * Get size of the market data
     *
     * @param mid
     * @return
     */
    public static long getFolderSize(long mid) {
        String folderName = DEFAULT_PATH + mid;
        Path folder = Paths.get(folderName);
        long size = 0;
        try {
            size = Files.walk(folder)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            LOGGER.error("Cannot get market data size.");
        }
        return size / 1024;
    }
}
