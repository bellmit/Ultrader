package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.KeyVerificationRequest;
import com.ultrader.bot.model.KeyVerificationResponse;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.LicenseService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * License Monitor
 * @author ytx1991
 */
public class LicenseMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseMonitor.class);
    private static LicenseMonitor singleton_instance = null;
    private static final String BOT_KEY_NAME = "KEY_ULTRADER_KEY";
    private static final String BOT_SECRET_NAME = "KEY_ULTRADER_SECRET";
    private static final String ALPACA_KEY_NAME = "KEY_ALPACA_KEY";
    private static final String TRADING_PLATFORM_NAME = "GLOBAL_TRADING_PLATFORM";

    private boolean validLicense = false;
    private LicenseService licenseService;
    private SettingDao settingDao;

    private LicenseMonitor(long interval, final LicenseService licenseService, final SettingDao settingDao) {
        super(interval);
        Validate.notNull(licenseService, "licenseService is required");
        Validate.notNull(settingDao, "settingDao is required");
        this.licenseService = licenseService;
        this.settingDao = settingDao;
    }

    @Override
    void scan() {
        try{
            String ultraderKey = settingDao.findById(BOT_KEY_NAME).map(Setting::getValue).orElse(null);
            String ultraderSecret = settingDao.findById(BOT_SECRET_NAME).map(Setting::getValue).orElse(null);
            String tradingKey = null;
            String platformName = settingDao.findById(TRADING_PLATFORM_NAME).map(Setting::getValue).orElse("Alpaca");
            switch (platformName) {
                case "Alpaca":
                    tradingKey = settingDao.findById(ALPACA_KEY_NAME).map(Setting::getValue).orElse(null);
                    break;
            }
            if(StringUtils.isEmpty(ultraderKey) || StringUtils.isEmpty(ultraderSecret) || StringUtils.isEmpty(tradingKey)) {
                LOGGER.error("Missing key settings.");
                validLicense = false;
                return;
            }
            validLicense = licenseService.verifyLicense(ultraderKey, ultraderSecret, tradingKey, platformName);
        } catch (Exception e) {
            LOGGER.error("Verify license failed.",e);
        }

    }



    public static void init(long interval, LicenseService licenseService, SettingDao settingDao) {
        singleton_instance = new LicenseMonitor(interval, licenseService, settingDao);
    }

    public static LicenseMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }

    public boolean isValidLicense() {
        return validLicense;
    }
}
