package com.ultrader.bot.controller;

import com.ultrader.bot.dao.OrderDao;
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

/**
 * Notification Controller
 * @author ytx1991
 */
@RequestMapping("/api/notification")
@RestController
public class NotificationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SimpMessagingTemplate notifier;

    @RequestMapping(method = RequestMethod.GET, value = "/dashboard")
    @ResponseBody
    public void dashboardNotification() {
        //Populate Dashboard Message
        notifier.convertAndSend("/topic/dashboard/account", NotificationUtil.generateAccountNotification(TradingAccountMonitor.getAccount()));
        notifier.convertAndSend("/topic/dashboard/trades", NotificationUtil.generateTradesNotification(orderDao));
        notifier.convertAndSend("/topic/dashboard/profit", NotificationUtil.generateProfitNotification(orderDao));

    }
}
