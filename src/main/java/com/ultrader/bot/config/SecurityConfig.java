package com.ultrader.bot.config;

import com.ultrader.bot.service.UserService;
import com.ultrader.bot.util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * REST security config
 * @author ytx1991
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationSuccessHandler mySuccessHandler;

    @Autowired
    private UserService userService;

    private SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider
                = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        return authProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/rule/**").hasAnyRole(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())//1 for Admin, 2 for User
                .antMatchers("/user/addRootUser").permitAll()
                .antMatchers("/user/getUserType").permitAll()
                .antMatchers("/strategy/**").hasAnyRole(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                .antMatchers("/setting/**").hasAnyRole(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                .antMatchers("/user/**").hasRole(UserType.ADMIN.getId().toString())
                .and()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .successHandler(mySuccessHandler)
                .failureHandler(failureHandler)
                .and()
                .httpBasic()
                .and()
                .logout();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
