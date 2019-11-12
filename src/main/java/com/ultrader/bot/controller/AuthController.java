package com.ultrader.bot.controller;

import com.ultrader.bot.config.BotUser;
import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.dao.UserDao;
import com.ultrader.bot.model.User;
import com.ultrader.bot.payload.model.JwtAuthenticationResponse;
import com.ultrader.bot.payload.model.LoginRequest;
import com.ultrader.bot.security.JwtTokenProvider;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("AuthController")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @RequestMapping(method = RequestMethod.POST, value = "/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        BotUser userPrincipal = (BotUser) authentication.getPrincipal();

        User user = userDao.findById(userPrincipal.getId()).get();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        boolean isSetup = true;
        if (RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_KEY.getName(), "").isEmpty()
                || RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_SECRET.getName(), "").isEmpty()) {
            isSetup = false;
        }
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt,"Bearer", isSetup, user.getRoleId()));
    }

}
