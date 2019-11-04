package com.ultrader.bot.controller;

import com.ultrader.bot.dao.ConditionalSettingDao;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.ConditionalSetting;
import com.ultrader.bot.model.Setting;
import com.ultrader.bot.monitor.MonitorManager;
import com.ultrader.bot.service.TradingPlatform;
import com.ultrader.bot.util.DatabaseUtil;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Setting Controller
 * @author ytx1991
 */

@RequestMapping("/api/setting")
@RestController("SettingController")
public class SettingController {
    private static Logger LOGGER = LoggerFactory.getLogger(SettingController.class);
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private TradingPlatform tradingPlatform;
    @Autowired
    private SettingDao settingDao;
    @Autowired
    private ConditionalSettingDao conditionalSettingDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(method = RequestMethod.POST, value = "/addSettings")
    @ResponseBody
    public Iterable<Setting> saveSettings(@RequestBody List<Setting> settings) {
        try {
            Iterable<Setting> savedSettings = settingDao.saveAll(settings);
            tradingPlatform.getTradingService().setAccountConfiguration(settings);
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
            settingDao.saveAll(tradingPlatform.getTradingService().getAccountConfiguration());
            Iterable<Setting> savedSettings = settingDao.findAll();
            return savedSettings;
        } catch (Exception e) {
            LOGGER.error("Get settings failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addConditionalSettings")
    @ResponseBody
    public Iterable<ConditionalSetting> saveConditionalSettings(@RequestBody List<ConditionalSetting> settings) {
        try {
            Iterable<ConditionalSetting> savedSettings = conditionalSettingDao.saveAll(settings);
            return savedSettings;
        } catch (Exception e) {
            LOGGER.error("Save setting failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getConditionalSettings")
    @ResponseBody
    public Iterable<ConditionalSetting> getConditionalSettings() {
        try {
            Iterable<ConditionalSetting> savedSettings = conditionalSettingDao.findAll();
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
        LOGGER.info("Backup database.");
        DatabaseUtil.backup(jdbcTemplate);
        LOGGER.info("Restart Ultrader ...");
        tradingPlatform.restart();
        monitorManager.restart();
    }
}
