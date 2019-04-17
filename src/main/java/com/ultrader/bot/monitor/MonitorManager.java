package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.service.LicenseService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.util.RepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Manager of all long running tasks.
 * It will in charge of all automatic functions.
 * @author ytx1991
 */
@Component
public class MonitorManager implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorManager.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor =new ThreadPoolTaskExecutor();
    @Autowired
    LicenseService licenseService;
    @Autowired
    AlpacaTradingService alpacaTradingService;
    @Autowired
    SettingDao  settingDao;

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void run(String... args) throws Exception {
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(4);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.initialize();
        //Start License Monitor
        LicenseMonitor.init(24 * 3600L * 1000, licenseService, settingDao);
        threadPoolTaskExecutor.execute(LicenseMonitor.getInstance());
        //Start MarketDate Monitor
        long interval = Long.parseLong(RepositoryUtil.getSetting(settingDao,"TRADE_INTERVAL_SECOND", "60000"));
        MarketDataMonitor.init(interval, alpacaTradingService, settingDao);
        threadPoolTaskExecutor.execute(MarketDataMonitor.getInstance());


        FakeMonitor.init(1000, template);
        threadPoolTaskExecutor.execute(FakeMonitor.getInstance());
    }


}
