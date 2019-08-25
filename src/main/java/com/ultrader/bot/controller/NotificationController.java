package com.ultrader.bot.controller;

import com.ultrader.bot.dao.ChartDao;
import com.ultrader.bot.dao.NotificationDao;
import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.Notification;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.util.NotificationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification Controller
 * @author ytx1991
 */
@RequestMapping("/api/notification")
@RestController("NotificationController")
public class NotificationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ChartDao chartDao;
    @Autowired
    private SimpMessagingTemplate notifier;
    @Autowired
    private NotificationDao notificationDao;

    @RequestMapping(method = RequestMethod.GET, value = "/dashboard")
    @ResponseBody
    public DashboardDataMessage dashboardNotification() {
        //Populate Dashboard Message
        try {
            DashboardDataMessage message = new DashboardDataMessage();
            message.setData(new HashMap<>());
            message.getData().putAll(NotificationUtil.generateAccountNotification(TradingAccountMonitor.getAccount(), chartDao).getData());
            message.getData().putAll(NotificationUtil.generateTradesNotification(orderDao).getData());
            message.getData().putAll(NotificationUtil.generateProfitNotification(orderDao).getData());
            message.getData().putAll(NotificationUtil.generatePositionNotification().getData());
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
