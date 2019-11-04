package com.ultrader.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.model.Notification;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Notification Controller
 * @author ytx1991
 */
@RequestMapping("/api/notification")
@RestController("NotificationController")
public class NotificationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    @Autowired
    private NotificationService notifier;
    @Autowired
    private NotificationDao notificationDao;

    @RequestMapping(method = RequestMethod.GET, value = "/dashboard")
    @ResponseBody
    public DashboardDataMessage dashboardNotification() {
        //Populate Dashboard Message
        try {
            ObjectMapper mapper = new ObjectMapper();


            DashboardDataMessage message = new DashboardDataMessage();
            message.setData(new HashMap<>());
            message.getData().putAll(notifier.sendAccountNotification(TradingAccountMonitor.getAccount()).getData());
            message.getData().put("1", mapper.writeValueAsString(notifier.sendProfitNotification(1, false).getData()));
            message.getData().put("7", mapper.writeValueAsString(notifier.sendProfitNotification(7, false).getData()));
            message.getData().put("30", mapper.writeValueAsString(notifier.sendProfitNotification(30, false).getData()));
            message.getData().put("365", mapper.writeValueAsString(notifier.sendProfitNotification(365, false).getData()));
            message.getData().putAll(notifier.sendPositionNotification().getData());
            return message;
        } catch (Exception e) {
            LOGGER.error("Populate notification failed.", e);
            return  null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getNotifications")
    @ResponseBody
    public Iterable<Notification> getNotifications(@RequestParam int length) {
        //Populate Dashboard Message
        try {
            return notificationDao.getLatestNotification(length);
        } catch (Exception e) {
            LOGGER.error("Get notification failed.", e);
            return  null;
        }
    }
}
