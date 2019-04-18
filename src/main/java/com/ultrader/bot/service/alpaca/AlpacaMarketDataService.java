package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.alpaca.Bar;
import com.ultrader.bot.service.MarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
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

import org.ta4j.core.TimeSeries;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Alpaca Market data API
 * @author ytx1991
 */
@Service
public class AlpacaMarketDataService implements MarketDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaMarketDataService.class);
    private static final int MAX_STOCK = 200;
    private static final String TIME_ZONE = "America/New_York";
    private String alpacaKey;
    private String alpacaSecret;
    private RestTemplate client;

    private ParameterizedTypeReference<HashMap<String, ArrayList<Bar>>> barResponseType;
    @Autowired
    public AlpacaMarketDataService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_KEY_NAME.getName(), "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_SECRET_NAME.getName(), "");
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
            updateStockTimeSeries(newStocks, interval, true, maxLength);
        }
        if(oldStocks.size() > 0) {
            updateStockTimeSeries(oldStocks, interval, false, maxLength);
        }

        return stocks;
    }

    private void updateStockTimeSeries(List<TimeSeries> stocks, Long interval, boolean isNewStock, int maxLength) {
        int count = 0;
        StringBuilder symbols = new StringBuilder();
        Map<String, TimeSeries> batchTimeSeries = new HashMap<>();
        for(TimeSeries timeSeries : stocks) {
            if(count == 0) {
                symbols.append(timeSeries.getName());
            } else {
                symbols.append("," + timeSeries.getName());
            }
            count ++;

            batchTimeSeries.put(timeSeries.getName(), timeSeries);
            if(count < MAX_STOCK) {
                continue;
            }
            //Do a batch call when reach the max stocks per call limit
            updateBars(symbols.toString(), interval, (isNewStock ? maxLength : 1), batchTimeSeries);

            batchTimeSeries.clear();
            symbols = new StringBuilder();
            count = 0;
        }
        if(count != 0) {
            updateBars(symbols.toString(), interval, (isNewStock ? maxLength : 1), batchTimeSeries);
        }

    }

    private void updateBars(String symbols, Long interval, int limit, Map<String, TimeSeries> batchTimeSeries) {
        HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
        StringBuilder parameter = new StringBuilder("?");

        parameter.append("symbols=" + symbols);
        parameter.append("&limit=" + limit);

        //Get new bars
        ResponseEntity<HashMap<String, ArrayList<Bar>>> responseEntity = client.exchange("/bars/" + convertIntervalToTimeframe(interval) + parameter.toString(), HttpMethod.GET, entity, barResponseType);
        if (responseEntity.getStatusCode().is4xxClientError()) {
            LOGGER.error("Invalid Alpaca key, please check you key and secret");
            return;
        }


        //Update time series
        for(String stock : responseEntity.getBody().keySet()) {
            LOGGER.debug(String.format("Update stocks %s, %d bars", symbols, responseEntity.getBody().get(stock).size()));
            for(Bar bar : responseEntity.getBody().get(stock)) {
                Instant i = Instant.ofEpochSecond(bar.getT() + interval/1000);
                ZonedDateTime endDate = ZonedDateTime.ofInstant(i, ZoneId.of(TIME_ZONE));
                if( batchTimeSeries.get(stock).getBarCount() == 0 || !batchTimeSeries.get(stock).getLastBar().getEndTime().equals(endDate)) {
                    batchTimeSeries.get(stock).addBar(endDate, bar.getO(), bar.getH(), bar.getL(), bar.getC(), bar.getV());
                }
            }
        }
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
