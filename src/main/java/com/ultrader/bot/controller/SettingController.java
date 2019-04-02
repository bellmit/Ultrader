package com.ultrader.bot.controller;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.model.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:9191")
@RequestMapping("/setting")
@RestController
public class SettingController {
    private static Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

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

}
