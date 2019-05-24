package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.alpaca.Bar;
import com.ultrader.bot.service.MarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.ta4j.core.BaseBar;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Alpaca Market data API
 * @author ytx1991
 */
@Service
public class AlpacaMarketDataService implements MarketDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaMarketDataService.class);
    private static final int MAX_STOCK = 150;
    private String alpacaKey;
    private String alpacaSecret;
    private RestTemplate client;

    private ParameterizedTypeReference<HashMap<String, ArrayList<Bar>>> barResponseType;
    @Autowired
    public AlpacaMarketDataService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY.getName(), "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_SECRET.getName(), "");
        if(alpacaKey.equals("") || alpacaSecret.equals("")) {
            //It can be the first time setup
            LOGGER.warn("Cannot find Alpaca API key, please check our config");
        }
        client = restTemplateBuilder.rootUri("https://data.alpaca.markets/v1/").build();
        barResponseType = new ParameterizedTypeReference<HashMap<String, ArrayList<Bar>>>() {};
    }

    @Override
    public List<TimeSeries> updateTimeSeries(List<TimeSeries> stocks, Long interval) {
        List<TimeSeries> newStocks = new ArrayList<>();
        List<TimeSeries> oldStocks = new ArrayList<>();
        List<TimeSeries> result = new ArrayList<>();
        int maxLength = 0;
        for(TimeSeries timeSeries : stocks) {
            if(timeSeries.getBarCount() == 0) {
                newStocks.add(timeSeries);
            } else {
                oldStocks.add(timeSeries);
            }
            if(timeSeries.getMaximumBarCount() > maxLength) {
                maxLength = timeSeries.getMaximumBarCount();
            }
        }
        if(newStocks.size() > 0) {
           result.addAll(updateStockTimeSeries(newStocks, interval, true, maxLength));
        }
        if(oldStocks.size() > 0) {
            result.addAll(updateStockTimeSeries(oldStocks, interval, false, maxLength));
        }

        return result;
    }

    private List<TimeSeries> updateStockTimeSeries(List<TimeSeries> stocks, Long interval, boolean isNewStock, int maxLength) {
        Map<String, TimeSeries> result = new HashMap<>();
        stocks.stream().forEach(s -> result.put(s.getName(), s));
        int count = 0;
        StringBuilder symbols = new StringBuilder();
        for(TimeSeries timeSeries : stocks) {
            if(count == 0) {
                symbols.append(timeSeries.getName());
            } else {
                symbols.append("," + timeSeries.getName());
            }
            count ++;

            if(count < MAX_STOCK) {
                continue;
            }
            //Do a batch call when reach the max stocks per call limit
            updateBars(symbols.toString(), interval, (isNewStock ? maxLength : 3), result);

            symbols = new StringBuilder();
            count = 0;
        }
        if(count != 0) {
            updateBars(symbols.toString(), interval, (isNewStock ? maxLength : 3), result);
        }
        LOGGER.info("Batch updated {} stocks.", result.size());
        return new ArrayList<>(result.values());
    }

    private void updateBars(String symbols, Long interval, int limit, Map<String, TimeSeries> batchTimeSeries) {
        HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
        StringBuilder parameter = new StringBuilder("?");

        parameter.append("symbols=" + symbols);
        parameter.append("&limit=" + limit);
        //parameter.append("&start=" + (new Date().getTime() - interval * (limit + 1)));
        //Get new bars
        ResponseEntity<HashMap<String, ArrayList<Bar>>> responseEntity = client.exchange("/bars/" + convertIntervalToTimeframe(interval) + parameter.toString(), HttpMethod.GET, entity, barResponseType);
        if (responseEntity.getStatusCode().is4xxClientError()) {
            LOGGER.error("Invalid Alpaca key, please check you key and secret");
            return;
        }
        LOGGER.debug("Download {} stocks market data", responseEntity.getBody().size());
        int filterCount = 0;
        //Update time series
        for(String stock : responseEntity.getBody().keySet()) {
            LOGGER.debug(String.format("Update stocks %s, %d bars", stock, responseEntity.getBody().get(stock).size()));
            for(Bar bar : responseEntity.getBody().get(stock)) {
                int barSize = batchTimeSeries.get(stock).getBarCount();
                if( barSize == 0 || batchTimeSeries.get(stock).getLastBar().getBeginTime().toEpochSecond() <= bar.getT()) {
                    LOGGER.debug("Last bar begin at {}, current bar begin at {}, stock {}, bars {}",
                            barSize == 0 ? "null" : batchTimeSeries.get(stock).getLastBar().getBeginTime().toEpochSecond(),
                            bar.getT(),
                            stock,
                            barSize);
                    Instant i = Instant.ofEpochSecond(bar.getT() + interval/1000);
                    ZonedDateTime endDate = ZonedDateTime.ofInstant(i, ZoneId.of(TradingUtil.TIME_ZONE));
                    org.ta4j.core.Bar newBar = new BaseBar(Duration.ofMillis(interval),endDate, PrecisionNum.valueOf(bar.getO()), PrecisionNum.valueOf(bar.getH()), PrecisionNum.valueOf(bar.getL()), PrecisionNum.valueOf(bar.getC()), PrecisionNum.valueOf(bar.getV()), PrecisionNum.valueOf(bar.getV() * bar.getC()));
                    if(barSize > 0 && batchTimeSeries.get(stock).getLastBar().getEndTime().toEpochSecond() == endDate.toEpochSecond()) {
                        //Replace last bar
                        batchTimeSeries.get(stock).addBar(newBar, true);
                        LOGGER.debug("Replaced {} last bar {}", stock, newBar);
                    } else {
                        if(barSize == 0 || endDate.getDayOfYear() != batchTimeSeries.get(stock).getLastBar().getEndTime().getDayOfYear() || (endDate.toEpochSecond() - batchTimeSeries.get(stock).getLastBar().getEndTime().toEpochSecond()) <= 3600) {
                            //Time series must be continuous
                            batchTimeSeries.get(stock).addBar(newBar);
                        } else {
                            LOGGER.debug("Filter out {}, {}, {}", stock, endDate.toEpochSecond(), batchTimeSeries.get(stock).getLastBar().getEndTime().toEpochSecond());
                            //Remove the time series
                            batchTimeSeries.remove(stock);

                            filterCount++;
                            break;
                        }

                    }

                }
            }
        }
        LOGGER.debug("Filtered out {} stocks when downloading.", filterCount);
    }
    private HttpHeaders generateHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("APCA-API-KEY-ID", alpacaKey);
        httpHeaders.set("APCA-API-SECRET-KEY", alpacaSecret);
        return httpHeaders;
    }
    private String convertIntervalToTimeframe(long interval) {
        if(interval == 60 * 1000L) {
            return "1Min";
        } else if(interval == 300 * 1000L) {
            return "5Min";
        } else if(interval == 900 * 1000L) {
            return "15Min";
        } else if(interval == 24 * 3600 * 1000L) {
            return "day";
        } else {
            return "5Min";
        }
    }
}
