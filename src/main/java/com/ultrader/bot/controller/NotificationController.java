package com.ultrader.bot.controller;

import com.ultrader.bot.dao.OrderDao;
import com.ultrader.bot.model.websocket.DashboardDataMessage;
import com.ultrader.bot.monitor.TradingAccountMonitor;
import com.ultrader.bot.util.NotificationUtil;
import com.ultrader.bot.util.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    private SimpMessagingTemplate notifier;

    @RequestMapping(method = RequestMethod.GET, value = "/dashboard")
    @ResponseBody
    public DashboardDataMessage dashboardNotification() {
        //Populate Dashboard Message
        DashboardDataMessage message = new DashboardDataMessage();
        message.setData(new HashMap<>());
        message.getData().putAll(NotificationUtil.generateAccountNotification(TradingAccountMonitor.getAccount()).getData());
        message.getData().putAll(NotificationUtil.generateTradesNotification(orderDao).getData());
        message.getData().putAll( NotificationUtil.generateProfitNotification(orderDao).getData());
        return message;
    }
}
