package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.*;
import com.ultrader.bot.service.LicenseService;
import com.ultrader.bot.service.NotificationService;
import com.ultrader.bot.service.TradingNotificationService;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.DatabaseUtil;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


/**
 * Manager of all long running tasks.
 * It will in charge of all automatic functions.
 * @author ytx1991
 */
@Component("MonitorManager")
public class MonitorManager implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorManager.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private TradingPlatform tradingPlatform;
    @Autowired
    private SettingDao  settingDao;
    @Autowired
    private ConditionalSettingDao conditionalSettingDao;
    @Autowired
    private StrategyDao strategyDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private NotificationService notifier;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ChartDao chartDao;
    @Autowired
    private AssetListDao assetListDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TradingNotificationService sns;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() throws Exception {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.initialize();
        //Start License Monitor
        LicenseMonitor.init(24 * 3600L * 1000, licenseService, settingDao, notifier);
        threadPoolTaskExecutor.execute(LicenseMonitor.getInstance());
        //Start MarketDate Monitor
        long interval = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PERIOD_SECOND.getName(), "60")) * 1000;
        MarketDataMonitor.init(interval, tradingPlatform, settingDao, assetListDao, notifier, jdbcTemplate, orderDao);
        threadPoolTaskExecutor.execute(MarketDataMonitor.getInstance());
        //Trading account monitor
        TradingAccountMonitor.init(10000, tradingPlatform.getTradingService(), settingDao, notifier, orderDao, chartDao, positionDao);
        threadPoolTaskExecutor.execute(TradingAccountMonitor.getInstance());



        //Start Trading strategy monitor
        Thread.sleep(5000);
        TradingStrategyMonitor.init(15000, tradingPlatform, settingDao, conditionalSettingDao, strategyDao, ruleDao, notifier);
        threadPoolTaskExecutor.execute(TradingStrategyMonitor.getInstance());
    }

    public void restart() {
        try {
            threadPoolTaskExecutor.shutdown();
            init();
            LOGGER.info("Restarted monitor manager.");
        } catch (Exception e) {
            LOGGER.error("Restart monitor manager failed.", e);
        }
    }

}
