package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.*;
import com.ultrader.bot.service.LicenseService;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private StrategyDao strategyDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private SimpMessagingTemplate feedbackNotifier;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ChartDao chartDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private NotificationDao notificationDao;

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
        LicenseMonitor.init(24 * 3600L * 1000, licenseService, settingDao, feedbackNotifier, notificationDao);
        threadPoolTaskExecutor.execute(LicenseMonitor.getInstance());

        //Trading account monitor
        TradingAccountMonitor.init(10000, tradingPlatform.getTradingService(), settingDao, feedbackNotifier, orderDao, chartDao, positionDao, notificationDao);
        threadPoolTaskExecutor.execute(TradingAccountMonitor.getInstance());
        //Start MarketDate Monitor
        long interval = Long.parseLong(RepositoryUtil.getSetting(settingDao, SettingConstant.TRADE_PERIOD_SECOND.getName(), "60")) * 1000;
        MarketDataMonitor.init(interval, tradingPlatform.getTradingService(), tradingPlatform.getMarketDataService(), settingDao, feedbackNotifier, notificationDao);
        threadPoolTaskExecutor.execute(MarketDataMonitor.getInstance());


        //Start Trading strategy monitor
        Thread.sleep(10000);
        TradingStrategyMonitor.init(20000, tradingPlatform.getTradingService(), settingDao, strategyDao, ruleDao, feedbackNotifier);
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
