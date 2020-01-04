package com.ultrader.bot.config;

import static java.util.Collections.singletonList;

import com.ultrader.bot.security.CustomUserDetailsService;
import com.ultrader.bot.security.JwtAuthenticationEntryPoint;
import com.ultrader.bot.security.JwtAuthenticationFilter;
import com.ultrader.bot.util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = singletonList("*");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(singletonList("*"));
        configuration.setAllowedMethods(singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
                .antMatchers("/ws/**").permitAll()
                //Auth Controller
                .antMatchers("/api/auth/**").permitAll()
                //Metadata Controller
                .antMatchers("/api/metadata/**").permitAll()
                //Rule Controller
                .antMatchers("/api/rule/getRule/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/rule/getRules/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/rule/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Asset Controller
                .antMatchers("/api/asset/getAssetLists/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/asset/getAllAssets/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/asset/getAssetList/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/asset/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Order Controller
                .antMatchers("/api/order/getOpenOrders/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/order/getClosedOrders/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/order/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Chart Controller
                .antMatchers("/api/chart/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                //Position Controller
                .antMatchers("/api/position/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                //Notification Controller
                .antMatchers("/api/notification/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                //User Controller
                .antMatchers("/api/user/addRootUser/**").permitAll()
                .antMatchers("/api/user/getUserType/**").permitAll()
                .antMatchers("/api/user/hasUsers/**").permitAll()
                .antMatchers("/api/user/**").hasAuthority(UserType.ADMIN.getId().toString())
                //News  Controller
                .antMatchers("/api/news/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                //Strategy Controller
                .antMatchers("/api/strategy/getStrategy/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/strategy/getStrategies/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString(), UserType.READ_ONLY_USER.getId().toString())
                .antMatchers("/api/strategy/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())
                //Setting Controller
                .antMatchers("/api/setting/check/**").permitAll()
                .antMatchers("/api/setting/**").hasAnyAuthority(UserType.ADMIN.getId().toString(), UserType.OPERATOR.getId().toString())

                .antMatchers("/admin/**").hasAuthority(UserType.ADMIN.getId().toString())
                // database console
                .antMatchers("/h2-console/**").permitAll();


        http.headers().frameOptions().sameOrigin();

        http.cors();
        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // Add filter for checking keys
        http.addFilterBefore(setupCompleteCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(customCorsFilter(), SessionManagementFilter.class);

    }

    @Bean
    CORSFilter customCorsFilter() {
        return new CORSFilter();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
