package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.KeyVerificationRequest;
import com.ultrader.bot.model.KeyVerificationResponse;
import com.ultrader.bot.model.Setting;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
@Scope("prototype")
public class LicenseMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseMonitor.class);
    private static final String BOT_KEY_NAME = "KEY_ULTRADER_KEY";
    private static final String BOT_SECRET_NAME = "KEY_ULTRADER_SECRET";
    private static final String ALPACA_KEY_NAME = "KEY_ALPACA_KEY";
    private static final String TRADING_PLATFORM_NAME = "Alpaca";
    private static final String LICENSE_CLIENT_URI = "http://license.ultraderbot.com:8080/api/key";
    private static boolean validLicense = false;

    private RestTemplateBuilder restTemplateBuilder;
    private SettingDao settingDao;

    private RestTemplate client;

    public LicenseMonitor(long interval,final RestTemplateBuilder restTemplateBuilder,final SettingDao settingDao) {
        super(interval);
        Validate.notNull(restTemplateBuilder, "restTemplateBuilder is required");
        Validate.notNull(settingDao, "settingDao is required");
        this.restTemplateBuilder = restTemplateBuilder;
        this.settingDao = settingDao;
        client = restTemplateBuilder.rootUri(LICENSE_CLIENT_URI).build();
    }

    @Override
    void scan() {
        try{
            String ultraderKey = settingDao.findById(BOT_KEY_NAME).map(Setting::getValue).orElse(null);
            String ultraderSecret = settingDao.findById(BOT_SECRET_NAME).map(Setting::getValue).orElse(null);
            String alpacaKey = settingDao.findById(ALPACA_KEY_NAME).map(Setting::getValue).orElse(null);
            String platformName = settingDao.findById(TRADING_PLATFORM_NAME).map(Setting::getValue).orElse("Alpaca");
            if(StringUtils.isEmpty(ultraderKey) || StringUtils.isEmpty(ultraderSecret) || StringUtils.isEmpty(alpacaKey)) {
                LOGGER.error("Missing key settings.");
                validLicense = false;
                return;
            }
            //Verify License
            KeyVerificationRequest request = new KeyVerificationRequest();
            request.setPlatformKey(alpacaKey);
            request.setPlatformName(platformName);
            request.setUltraderKey(ultraderKey);
            request.setTimestamp(new Date().getTime());
            request.setToken(encrypt(ultraderKey + ultraderSecret + request.getTimestamp().toString()));
            KeyVerificationResponse response = client.postForObject("/verify", request, KeyVerificationResponse.class);
            LOGGER.info(response.toString());
            if(response.getToken() == null) {
                LOGGER.error(response.getMessage());
                validLicense = false;
                return;
            } else if(response.getToken().equals(encrypt(ultraderKey + ultraderSecret + alpacaKey + response.getTimestamp().toString()))) {
                LOGGER.info("Verify license succeeded.");
                validLicense = true;
                return;
            } else {
                LOGGER.error("Invalid token, hacking detected");
                //TODO upload user info to the license host
                validLicense = false;
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Verify license failed.",e);
        }

    }

    private String encrypt(String raw) {
        return new String(DigestUtils.md5Digest(raw.getBytes()));
    }

    public static boolean isIsValidLicense() {
        return validLicense;
    }
}
