package com.canu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Order(30)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .antMatchers("/", "/secure/two_factor_authentication").permitAll()
//            .antMatchers("/oauth/token").permitAll()
//            .anyRequest().authenticated()
//            .and().httpBasic();
        http.cors().and().csrf().disable();
        //2fa
        //
        //            .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
        //            .antMatchers("/api/user/**").access("#oauth2.hasScope('ADMIN')")
        //            .anyRequest()
        //            .authenticated()
        //            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //            .and()
        //            .formLogin().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("stephenle1412@gmail.com")
                .password(passwordEncoder().encode("123456"))
                .roles("ADMIN");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RequestLogginFilter requestLoggingFilter() {
        RequestLogginFilter filter = new RequestLogginFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        // truncate payloads
        filter.setMaxPayloadLength(1000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("Request received: ");
        return filter;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
