package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.KeyVerificationRequest;
import com.ultrader.bot.model.KeyVerificationResponse;
import com.ultrader.bot.model.Notification;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.LicenseService;
import com.ultrader.bot.util.NotificationUtil;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingPlatformConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;

/**
 * License Monitor
 * @author ytx1991
 */
public class LicenseMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseMonitor.class);
    private static LicenseMonitor singleton_instance = null;

    private boolean validLicense = false;
    private LicenseService licenseService;
    private SettingDao settingDao;
    private SimpMessagingTemplate notifier;
    private NotificationDao notificationDao;

    private LicenseMonitor(long interval,
                           final LicenseService licenseService,
                           final SettingDao settingDao,
                           final SimpMessagingTemplate notifier,
                           final NotificationDao notificationDao) {
        super(interval);
        Validate.notNull(licenseService, "licenseService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");
        Validate.notNull(notificationDao, "notificationDao is required");

        this.licenseService = licenseService;
        this.settingDao = settingDao;
        this.notifier = notifier;
        this.notificationDao = notificationDao;
    }

    @Override
    void scan() {
        try{
            String ultraderKey = settingDao.findById(SettingConstant.BOT_KEY.getName()).map(Setting::getValue).orElse(null);
            String ultraderSecret = settingDao.findById(SettingConstant.BOT_SECRET.getName()).map(Setting::getValue).orElse(null);
            String tradingKey = null;
            String platformName = settingDao.findById(SettingConstant.TRADING_PLATFORM.getName()).map(Setting::getValue).orElse(TradingPlatformConstant.ALPACA_PAPER);
            switch (platformName) {
                case "Alpaca":
                    tradingKey = settingDao.findById(SettingConstant.ALPACA_KEY.getName()).map(Setting::getValue).orElse(null);
                    break;
                case "AlpacaPaper":
                    tradingKey = settingDao.findById(SettingConstant.ALPACA_PAPER_KEY.getName()).map(Setting::getValue).orElse(null);
                    break;
            }
            if(StringUtils.isEmpty(ultraderKey) || StringUtils.isEmpty(ultraderSecret) || StringUtils.isEmpty(tradingKey)) {
                LOGGER.error("Missing key settings.");
                NotificationUtil.sendNotification(notifier, notificationDao, new Notification(
                        UUID.randomUUID().toString(),
                        "ERROR",
                        "Cannot find Ultra Trader key in your setting.",
                        "License Failure",
                        new Date()));
                validLicense = false;
                return;
            }
            validLicense = licenseService.verifyLicense(ultraderKey, ultraderSecret, tradingKey, platformName);
            if (!validLicense) {
                NotificationUtil.sendNotification(notifier, notificationDao, new Notification(
                        UUID.randomUUID().toString(),
                        "WARN",
                        "Cannot verify your Ultra Trader license. Please check if your license is expired.",
                        "License Failure",
                        new Date()));
            }
        } catch (Exception e) {
            LOGGER.error("Verify license failed.",e);
            NotificationUtil.sendNotification(notifier, notificationDao, new Notification(
                    UUID.randomUUID().toString(),
                    "ERROR",
                    "Cannot verify your Ultra Trader license. Your trading will be blocked.",
                    "License Failure",
                    new Date()));
        }

    }



    public static void init(long interval,
                            final LicenseService licenseService,
                            final SettingDao settingDao,
                            final SimpMessagingTemplate notifier,
                            final NotificationDao notificationDao) {
        singleton_instance = new LicenseMonitor(interval, licenseService, settingDao, notifier, notificationDao);
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
