package com.ultrader.bot.config;

import com.ultrader.bot.dao.SettingDao;
import com.ultrader.bot.util.RepositoryUtil;
import com.ultrader.bot.util.SettingConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SetupCompleteCheckFilter extends OncePerRequestFilter {

    @Autowired
    private SettingDao settingDao;

    private static final Logger logger = LoggerFactory.getLogger(SetupCompleteCheckFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //if the user is logged in
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    //if the user has the keys
                    (RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_KEY.getName(), "").isEmpty()
                    || RepositoryUtil.getSetting(settingDao, SettingConstant.BOT_SECRET.getName(), "").isEmpty())) {
                response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "The keys are not set, the user needs to be redirected to a setup page.");
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/h2-console") ||
                path.startsWith("/api/metadata/") ||
                path.startsWith("/api/setting/addSettings") ||
                path.startsWith("/api/strategy/import") ||
                path.startsWith("/api/auth");

    }
}
