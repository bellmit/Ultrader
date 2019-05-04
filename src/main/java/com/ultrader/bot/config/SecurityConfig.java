package com.ultrader.bot.config;

import com.ultrader.bot.security.CustomUserDetailsService;
import com.ultrader.bot.security.JwtAuthenticationEntryPoint;
import com.ultrader.bot.security.JwtAuthenticationFilter;
import com.ultrader.bot.service.UserService;
import com.ultrader.bot.util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.sql.DataSource;

/**
 * REST security config
 * @author ytx1991
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(encoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .and()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils:: isPreFlightRequest).permitAll()
                .antMatchers("/api/auth").permitAll()
                .antMatchers("/rule/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())//1 for Admin, 2 for User
                .antMatchers("/user/addRootUser").permitAll()
                .antMatchers("/user/getUserType").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/strategy/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                .antMatchers("/setting/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                .antMatchers("/user/**").hasAuthority(UserType.ADMIN.getId().toString());


        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}