package com.ultrader.bot.service;

import com.ultrader.bot.config.BotUser;
import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

/**
 * User service for API login
 * @author ytx1991
 */

@Service("UserService")
public class UserService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private WebApplicationContext applicationContext;
    private UserDao userDao;

    public UserService() {
        super();
    }

    @PostConstruct
    public void completeSetup() {
        userDao = applicationContext.getBean(UserDao.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username);
        LOGGER.debug("find user: " + user.toString());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new BotUser(user);
    }

}
