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
@Configuration("SecurityConfig")
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

    @Bean
    public SetupCompleteCheckFilter setupCompleteCheckFilter() {
        return new SetupCompleteCheckFilter();
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
                // websockets
                .antMatchers(
                        "/ws/**/**",
                        "/ws/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                //Auth Controller
                .antMatchers("/api/auth/**").permitAll()
                //Metadata Controller
                .antMatchers("/api/metadata/**").permitAll()
                //Rule Controller
                .antMatchers("/api/rule/getRule/**").permitAll()
                .antMatchers("/api/rule/getRules/**").permitAll()
                .antMatchers("/api/rule/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Asset Controller
                .antMatchers("/api/asset/getAssetLists/**").permitAll()
                .antMatchers("/api/asset/getAllAssets/**").permitAll()
                .antMatchers("/api/asset/getAssetList/**").permitAll()
                .antMatchers("/api/asset/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Order Controller
                .antMatchers("/api/order/getOpenOrders/**").permitAll()
                .antMatchers("/api/order/getClosedOrders/**").permitAll()
                .antMatchers("/api/order/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Chart Controller
                .antMatchers("/api/chart/**").permitAll()
                //Position Controller
                .antMatchers("/api/position/**").permitAll()
                //Notification Controller
                .antMatchers("/api/notification/**").permitAll()
                //User Controller
                .antMatchers("/api/user/addRootUser/**").permitAll()
                .antMatchers("/api/user/getUserType/**").permitAll()
                .antMatchers("/api/user/**").hasAuthority(UserType.ADMIN.getId().toString())
                //Strategy Controller
                .antMatchers("/api/strategy/getStrategy/**").permitAll()
                .antMatchers("/api/strategy/getStrategies/**").permitAll()
                .antMatchers("/api/strategy/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Setting Controller
                .antMatchers("/api/setting/check/**").permitAll()
                .antMatchers("/api/setting/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())

                .antMatchers("/admin/**").hasAuthority(UserType.ADMIN.getId().toString())
                // database console
                .antMatchers("/h2-console/**").permitAll()
        ;


        http.headers().frameOptions().sameOrigin();
        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // Add filter for checking keys
        http.addFilterBefore(setupCompleteCheckFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
