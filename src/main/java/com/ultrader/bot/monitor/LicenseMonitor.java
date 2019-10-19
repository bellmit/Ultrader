package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.LicenseService;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.util.NotificationType;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.TradingPlatformConstant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    private NotificationService notifier;

    private LicenseMonitor(long interval,
                           final LicenseService licenseService,
                           final SettingDao settingDao,
                           final NotificationService notifier) {
        super(interval);
        Validate.notNull(licenseService, "licenseService is required");
        Validate.notNull(settingDao, "settingDao is required");
        Validate.notNull(notifier, "notifier is required");

        this.licenseService = licenseService;
        this.settingDao = settingDao;
        this.notifier = notifier;
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
                notifier.sendNotification("License Failure","Cannot find Ultra Trader key in your setting.", NotificationType.ERROR.name());
                validLicense = false;
                return;
            }
            validLicense = licenseService.verifyLicense(ultraderKey, ultraderSecret, tradingKey, platformName);
            if (!validLicense) {
                notifier.sendNotification("License Failure","Cannot verify your Ultra Trader license. Please check if your license is expired.", NotificationType.WARN.name());
            }
        } catch (Exception e) {
            LOGGER.error("Verify license failed.",e);
            notifier.sendNotification("License Failure","Cannot verify your Ultra Trader license. Your trading will be blocked.", NotificationType.ERROR.name());
        }

    }



    public static void init(long interval,
                            final LicenseService licenseService,
                            final SettingDao settingDao,
                            final NotificationService notifier) {
        singleton_instance = new LicenseMonitor(interval, licenseService, settingDao, notifier);
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
