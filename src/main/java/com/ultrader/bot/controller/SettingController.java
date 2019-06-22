package com.ultrader.bot.controller;

import com.ultrader.bot.BotApplication;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.monitor.MonitorManager;
import com.ultrader.bot.service.alpaca.AlpacaMarketDataService;
import com.ultrader.bot.service.alpaca.AlpacaPaperTradingService;
import com.ultrader.bot.service.alpaca.AlpacaTradingService;
import com.ultrader.bot.service.polygon.PolygonMarketDataService;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.web.bind.annotation.*;

/**
 * Setting Controller
 * @author ytx1991
 */

@RequestMapping("/api/setting")
@RestController
public class SettingController {
    private static Logger LOGGER = LoggerFactory.getLogger(SettingController.class);
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private AlpacaTradingService alpacaTradingService;
    @Autowired
    private AlpacaPaperTradingService alpacaPaperTradingService;
    @Autowired
    private AlpacaMarketDataService alpacaMarketDataService;
    @Autowired
    private PolygonMarketDataService polygonMarketDataService;

    @Autowired
    private SettingDao settingDao;

    @RequestMapping(method = RequestMethod.POST, value = "/addSettings")
    @ResponseBody
    public Iterable<Setting> save(@RequestBody Iterable<Setting> settings) {
        try {
            Iterable<Setting> savedSettings = settingDao.saveAll(settings);
            return savedSettings;
        } catch (Exception e) {
            LOGGER.error("Save setting failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getSettings")
    @ResponseBody
    public Iterable<Setting> getSettings() {
        try {
            Iterable<Setting> savedSettings = settingDao.findAll();
            return savedSettings;
        } catch (Exception e) {
            LOGGER.error("Get settings failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/check")
    @ResponseBody
    public boolean check() {
        try {
            if (RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_KEY.getName(), "").isEmpty()
            || RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_SECRET.getName(), "").isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Check settings failed.", e);
            return true;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/restart")
    @ResponseBody
    public void restart() {

        alpacaMarketDataService.restart();
        alpacaPaperTradingService.restart();
        alpacaTradingService.restart();
        polygonMarketDataService.restart();
        monitorManager.restart();
    }
}
