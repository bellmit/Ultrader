package com.ultrader.bot.service;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.KeyVerificationRequest;
import com.ultrader.bot.model.KeyVerificationResponse;
import com.ultrader.bot.util.RepositoryUtil;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * License Service
 * @author ytx1991
 */
@Service("ls")
public class LicenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseService.class);

    private static final String LICENSE_CLIENT_URI = "http://license.ultraderbot.com/api/key";

    private RestTemplate client;

    @Autowired
    public LicenseService(RestTemplateBuilder restTemplateBuilder) {
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");

        client = restTemplateBuilder.rootUri(LICENSE_CLIENT_URI).build();
    }

    public boolean verifyLicense(String ultraderKey, String ultraderSecret, String tradingKey, String tradingPlatform) {
        //Verify License
        KeyVerificationRequest request = new KeyVerificationRequest();
        request.setPlatformKey(tradingKey);
        request.setPlatformName(tradingPlatform);
        request.setUltraderKey(ultraderKey);
        request.setTimestamp(new Date().getTime());
        request.setToken(encrypt(ultraderKey + ultraderSecret + request.getTimestamp().toString()));
        KeyVerificationResponse response = client.postForObject("/verify", request, KeyVerificationResponse.class);
        if(response.getToken() == null) {
            LOGGER.error(response.getMessage());
            return false;
        } else if(response.getToken().equals(encrypt(ultraderKey + ultraderSecret + tradingKey + response.getTimestamp().toString()))) {
            LOGGER.info("Verify license succeeded.");
            return true;
        } else {
            LOGGER.error("Invalid token, hacking detected");
            //TODO upload user info to the license host
            return false;
        }
    }

    private String encrypt(String raw) {
        return new String(DigestUtils.md5Digest(raw.getBytes(Charset.forName("UTF-8"))),  Charset.forName("UTF-8"));
    }
}
