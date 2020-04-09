package com.ultrader.bot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.TradingNotification;
import com.ultrader.bot.model.TradingNotificationRequest;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Publishing trading notification to AWS SNS
 */
@Service("TradingNotificationService")
public class TradingNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingNotificationService.class);
    private static final String TRADING_NOTIFICATION_URL = "http://www.ultraderbot.com/api/tradingNotification";
    private RestTemplate client;
    private ObjectMapper mapper;
    private SettingDao settingDao;

    @Autowired
    public TradingNotificationService(final RestTemplateBuilder restTemplateBuilder, final SettingDao settingDao) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");
        client = restTemplateBuilder.rootUri(TRADING_NOTIFICATION_URL).build();
        this.settingDao = settingDao;
        mapper = new ObjectMapper();
    }

    public void sendNotification(TradingNotification notification) {
        String token = RepositoryUtil.getSetting(settingDao, SettingConstant.ULTRADER_PUBLISH_TOKEN.getName(), "");
        if(token.equals("") || notification.getStrategyId().equals("")) {
            //Missing settings, skip publishing
            return;
        }
        final String msg;
        try {
            Validate.notEmpty(notification.getStrategyId(), "StrategyId is required");
            Validate.notEmpty(notification.getAmount(), "Amount is required");
            Validate.notEmpty(notification.getSymbol(), "Symbol is required");
            Validate.notEmpty(notification.getSide(), "Side is required");
            Validate.notNull(notification.getPrice(), "Price is required");
            Validate.notEmpty(notification.getType(), "Type is required");
            Validate.notNull(notification.getCurrentPositions(), "CurrentPositions is required");
            msg = mapper.writeValueAsString(notification);
            Long timestamp = new Date().getTime();
            TradingNotificationRequest request = new TradingNotificationRequest();
            request.setOrder(msg);
            request.setStrategyId(notification.getStrategyId());
            request.setTimestamp(timestamp);
            request.setToken(new String(DigestUtils.md5Digest((token + timestamp).getBytes(Charset.forName("UTF-8"))),  Charset.forName("UTF-8")));
            ResponseEntity<String> responseEntity = client.exchange("/publish", HttpMethod.POST, new HttpEntity<>(request), String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Published trading notification to Ultrader. {}", notification);
            } else {
                LOGGER.error("Publish trading notification failed. {}", responseEntity.getBody());
            }

        } catch (Exception e) {
            LOGGER.error("Publish trading notification failed.", e);
        }

    }
}
