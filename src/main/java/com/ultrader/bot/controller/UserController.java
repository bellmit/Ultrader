package com.ultrader.bot.controller;

import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RequestMapping("/user")
@RestController
public class UserController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.POST, value = "/addUser")
    @ResponseBody
    public User save(@RequestBody User user) {
        try {
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

}
