package com.ultrader.bot.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component("CustomAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException ex) throws IOException, ServletException {
        LOGGER.info("AccessDenied.");
        response.getOutputStream().print("Error Message Goes Here");
        response.setStatus(403);
    }

}