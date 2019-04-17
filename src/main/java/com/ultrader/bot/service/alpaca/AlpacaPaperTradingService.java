package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.alpaca.Asset;
import com.ultrader.bot.model.alpaca.Clock;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Alpaca Trading API
 * @author ytx1991
 */
@Service
public class AlpacaPaperTradingService implements TradingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaPaperTradingService.class);
    private String alpacaKey;
    private String alpacaSecret;
    private RestTemplate client;

    @Autowired
    public AlpacaPaperTradingService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.alpacaKey = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_KEY_NAME.getName(), "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, SettingConstant.ALPACA_PAPER_SECRET_NAME.getName(), "");
        if(alpacaKey.equals("") || alpacaSecret.equals("")) {
            //It can be the first time setup
            LOGGER.warn("Cannot find Alpaca API key, please check our config");
        }
        client = restTemplateBuilder.rootUri("https://paper-api.alpaca.markets/v1/").build();
    }

    private HttpHeaders generateHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("APCA-API-KEY-ID", alpacaKey);
        httpHeaders.set("APCA-API-SECRET-KEY", alpacaSecret);
        return httpHeaders;
    }

    public boolean isMarketOpen() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Clock> clock = client.exchange("/clock", HttpMethod.GET, entity, Clock.class);
            if (clock.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return false;
            }
            LOGGER.info(clock.getBody().toString());
            return clock.getBody().getIs_open();
        } catch (Exception e) {
            LOGGER.error("Failed to call /clock api.", e);
            return false;
        }
    }

    public Map<String, Set<String>> getAvailableStocks() {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(generateHeader());
            ResponseEntity<Asset[]> stocks = client.exchange("/assets?status=active", HttpMethod.GET, entity, Asset[].class);
            if (stocks.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return new HashMap<>();
            }
            Map<String, Set<String>> exchangeStockMap = new HashMap<>();
            for(Asset asset : stocks.getBody()) {
                if(!exchangeStockMap.containsKey(asset.getExchange())) {
                    exchangeStockMap.put(asset.getExchange(), new HashSet<String>());
                }
                if(asset.getTradable()) {
                    exchangeStockMap.get(asset.getExchange()).add(asset.getSymbol());
                }
            }
            return exchangeStockMap;
        } catch (Exception e) {
            LOGGER.error("Failed to call /assets api.", e);
            return new HashMap<>();
        }
    }
}
