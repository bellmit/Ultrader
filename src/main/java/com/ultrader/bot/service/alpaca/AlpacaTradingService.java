package com.ultrader.bot.service.alpaca;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.alpaca.Clock;
import com.ultrader.bot.service.TradingService;
import com.ultrader.bot.util.RepositoryUtil;
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

/**
 * Alpaca Trading API
 * @author ytx1991
 */
@Service
public class AlpacaTradingService implements TradingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaTradingService.class);
    private static final String ALPACA_KEY_NAME = "KEY_ALPACA_KEY";
    private static final String ALPACA_SECRET_NAME = "KEY_ALPACA_SECRET";
    private String alpacaKey;
    private String alpacaSecret;
    private RestTemplate client;

    @Autowired
    public AlpacaTradingService(SettingDao settingDao, RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");

        this.alpacaKey = RepositoryUtil.getSetting(settingDao, ALPACA_KEY_NAME, "");
        this.alpacaSecret = RepositoryUtil.getSetting(settingDao, ALPACA_SECRET_NAME, "");
        if(alpacaKey.equals("") || alpacaSecret.equals("")) {
            //It can be the first time setup
            LOGGER.warn("Cannot find Alpaca API key, please check our config");
        }
        client = restTemplateBuilder.rootUri("https://api.alpaca.markets/v1/").build();
    }

    private HttpHeaders generateHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("APCA-API-KEY-ID", alpacaKey);
        httpHeaders.set("APCA-API-SECRET-KEY", alpacaSecret);
        return httpHeaders;
    }

    public boolean isMarketOpen() {
        try {
            HttpEntity<String> entity = new HttpEntity<>("", generateHeader());
            ResponseEntity<Clock> clock = client.exchange("/clock", HttpMethod.GET, entity, Clock.class);
            if (clock.getStatusCode().is4xxClientError()) {
                LOGGER.error("Invalid Alpaca key, please check you key and secret");
                return false;
            }
            return clock.getBody().getIs_open();
        } catch (Exception e) {
            LOGGER.error("Failed to call /clock api.", e);
            return false;
        }
    }
}
