package com.ultrader.bot.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.model.TradingNotification;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Publishing trading notification to AWS SNS
 */
@Service("TradingNotificationService")
public class TradingNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradingNotificationService.class);
    @Value("${app.accessKey}")
    private String key;
    @Value("${app.accessSecret}")
    private String secret;
    @Value("${app.snsARN}")
    private String arn;
    @Value("${app.awsRegion}")
    private String region;
    @Value("${app.strategyId}")
    private String strategyId;

    private AmazonSNS client;
    private ObjectMapper mapper;

    @Autowired
    public TradingNotificationService() {

       mapper = new ObjectMapper();
    }

    public void sendNotification(TradingNotification notification) {
        if(StringUtils.isEmpty(arn) || StringUtils.isEmpty(strategyId) || StringUtils.isEmpty(secret) || StringUtils.isEmpty(key) || StringUtils.isEmpty(region)) {
            LOGGER.error("Missing SNS configs.");
            return;
        }
        if (client == null) {
            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
            client = AmazonSNSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                    .withRegion(region).build();
        }
        final String msg;
        try {
            notification.setStrategyId(strategyId);
            msg = mapper.writeValueAsString(notification);
            final PublishRequest publishRequest = new PublishRequest(arn, msg);
            client.publish(publishRequest);
        } catch (Exception e) {
            LOGGER.error("Publish trading notification failed.", e);
        }

    }
}
