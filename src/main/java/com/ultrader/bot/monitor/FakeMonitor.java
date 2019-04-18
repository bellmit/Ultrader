package com.ultrader.bot.monitor;

import com.ultrader.bot.controller.websocket.GreetingController;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.model.websocket.Greeting;
import com.ultrader.bot.model.websocket.HelloMessage;
import com.ultrader.bot.service.LicenseService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.HtmlUtils;

public class FakeMonitor extends Monitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeMonitor.class);
    private static FakeMonitor singleton_instance = null;

    private SimpMessagingTemplate template;
    private static int count = 0;

    public void greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        this.template.convertAndSend("/topic/greetings", new Greeting("Fire" + count));
    }

    private FakeMonitor(long interval, SimpMessagingTemplate template) {
        super(interval);
        Validate.notNull(template, "template is required");
        this.template = template;

    }

    @Override
    void scan() {


    }



    public static void init(long interval, SimpMessagingTemplate template) {
        singleton_instance = new FakeMonitor(interval, template);
    }

    public static FakeMonitor getInstance() throws IllegalAccessException {
        if(singleton_instance == null) {
            LOGGER.error("Instance is not initialized.");
            throw new IllegalAccessException("Instance is not initialized.");
        }
        return singleton_instance;
    }
}
