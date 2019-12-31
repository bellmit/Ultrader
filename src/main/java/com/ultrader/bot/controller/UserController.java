package com.ultrader.bot.controller;

import com.ultrader.bot.dao.AssetListDao;
import com.ultrader.bot.dao.ConditionalSettingDao;
import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.AssetList;
import com.ultrader.bot.model.ConditionalSetting;
import com.ultrader.bot.model.User;
import com.ultrader.bot.util.DefaultSetting;
import com.ultrader.bot.util.MarketTrend;
import com.ultrader.bot.util.SettingConstant;
import com.ultrader.bot.util.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * @author ytx1991
 */

@RequestMapping("/api/user")
@RestController("UserController")
public class UserController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private AssetListDao assetListDao;
    @Autowired
    private ConditionalSettingDao conditionalSettingDao;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @RequestMapping(method = RequestMethod.POST, value = "/addUser")
    @ResponseBody
    public User save(@RequestBody User user) {
        try {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

            Integer roleId = null;
            try {
                roleId = Integer.parseInt(user.getRoleId());
            } catch (Exception e) {
            }
            if(roleId != null && UserType.findById(roleId)!= null){
                user.setRoleId(user.getRoleId());
            }else{
                user.setRoleId(UserType.READ_ONLY_USER.getId().toString());
            }

            User savedUser = userDao.save(user);
            return savedUser;
        } catch (Exception e) {
            LOGGER.error("Save user failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addRootUser")
    @ResponseBody
    public User addRootUser(@RequestBody User user) {
        try {
            if(userDao.count() > 0) {
                return null;
            }
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            user.setRoleId(UserType.ADMIN.getId().toString());
            User savedUser = userDao.save(user);
            initDatabase();
            return savedUser;
        } catch (Exception e) {
            LOGGER.error("Save user failed.", e);
            return null;
        }
    }

    private void initDatabase() {
        assetListDao.save(DefaultSetting.NASDAQ100);
        assetListDao.save(DefaultSetting.SP500);
        assetListDao.save(DefaultSetting.ULTRADER600);
        List<ConditionalSetting> conditionalSettings = new ArrayList<>();
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BULL.name(), SettingConstant.TRADE_BUY_STRATEGY.getName(), "21"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BULL.name(), SettingConstant.TRADE_SELL_STRATEGY.getName(), "29"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BULL.name(), SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "5"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BULL.name(), SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "20%"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BULL.name(), SettingConstant.TRADE_BUY_STRATEGY.getName(), "21"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BULL.name(), SettingConstant.TRADE_SELL_STRATEGY.getName(), "29"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BULL.name(), SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "5"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BULL.name(), SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "20%"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.NORMAL.name(), SettingConstant.TRADE_BUY_STRATEGY.getName(), "20"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.NORMAL.name(), SettingConstant.TRADE_SELL_STRATEGY.getName(), "30"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.NORMAL.name(), SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "10"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.NORMAL.name(), SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "10%"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BEAR.name(), SettingConstant.TRADE_BUY_STRATEGY.getName(), "22"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BEAR.name(), SettingConstant.TRADE_SELL_STRATEGY.getName(), "31"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BEAR.name(), SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "5"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.BEAR.name(), SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "10%"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BEAR.name(), SettingConstant.TRADE_BUY_STRATEGY.getName(), "22"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BEAR.name(), SettingConstant.TRADE_SELL_STRATEGY.getName(), "31"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BEAR.name(), SettingConstant.TRADE_BUY_HOLDING_LIMIT.getName(), "5"));
        conditionalSettings.add(new ConditionalSetting(null, MarketTrend.SUPER_BEAR.name(), SettingConstant.TRADE_BUY_MAX_LIMIT.getName(), "10%"));

    }
    @RequestMapping(method = RequestMethod.GET, value = "/getUsers")
    @ResponseBody
    public Iterable<User> getUsers() {
        try {
            Iterable<User> savedUsers = userDao.findAll();
            return savedUsers;
        } catch (Exception e) {
            LOGGER.error("Get users failed.", e);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hasUsers")
    @ResponseBody
    public boolean hasUsers() {
        try {
            return userDao.count() > 0;
        } catch (Exception e) {
            LOGGER.error("Has users failed.", e);
            return false;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getUser/{id}")
    @ResponseBody
    public User getUser(@PathVariable Long id) {
        try {
            return userDao.findById(id).get();
        } catch (Exception e) {
            LOGGER.error("Get users failed.", e);
            return null;
        }
    }

}
