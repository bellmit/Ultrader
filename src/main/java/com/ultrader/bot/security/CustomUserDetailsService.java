package com.ultrader.bot.security;

import com.ultrader.bot.config.BotUser;
import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userDao.findByUsername(username);
        if(user == null){
            throw  new UsernameNotFoundException("User not found with username: " + username);
        }


        return new BotUser(user);
    }


    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userDao.findById(id).get();
        if(user == null){
            throw  new UsernameNotFoundException("User not found with id: " + id);
        }
        return new BotUser(user);
    }
}