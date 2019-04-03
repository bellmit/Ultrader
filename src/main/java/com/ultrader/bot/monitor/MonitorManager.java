package com.ultrader.bot.monitor;

import com.ultrader.bot.dao.SettingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


/**
 * Manager of all long running tasks.
 * It will in charge of all automatic functions.
 */
@Component
public class MonitorManager implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorManager.class);

    private ThreadPoolTaskExecutor threadPoolTaskExecutor =new ThreadPoolTaskExecutor();
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @Autowired
    SettingDao  settingDao;

    @Override
    public void run(String... args) throws Exception {
        threadPoolTaskExecutor.setCorePoolSize(3);
        threadPoolTaskExecutor.setMaxPoolSize(3);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        threadPoolTaskExecutor.initialize();
        LicenseMonitor licenseMonitor = new LicenseMonitor(24 * 3600L * 1000, restTemplateBuilder, settingDao);
        threadPoolTaskExecutor.execute(licenseMonitor);

    }


}
