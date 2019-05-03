package com.ultrader.bot.controller;

import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.User;
import com.ultrader.bot.util.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * @author ytx1991
 */

@RequestMapping("/api/user")
@RestController
public class UserController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @RequestMapping(method = RequestMethod.POST, value = "/addUser")
    @ResponseBody
    public User save(@RequestBody User user) {
        try {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
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
            User savedUser = userDao.save(user);
            return savedUser;
        } catch (Exception e) {
            LOGGER.error("Save user failed.", e);
            return null;
        }
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

    @RequestMapping(method = RequestMethod.GET, value = "/getUserType")
    @ResponseBody
    public UserType[] getRuleType() {
        return UserType.values();
    }
}
