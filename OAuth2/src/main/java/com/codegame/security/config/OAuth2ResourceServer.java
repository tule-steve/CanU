package com.codegame.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@Order(20)
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            //            .anonymous().disable()
            //            .requestMatchers().antMatchers("/api/**", "**/secure/**").and()
            .authorizeRequests()
            .antMatchers("/secure/two_factor_authentication/**", "/api/**").permitAll()
            //            .antMatchers("/api/**").access("hasAnyRole('ADMIN','USER')")
            .anyRequest().authenticated()

            //            .and()
            //            .oauth2Login()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
            .and()
            .logout()
            .logoutSuccessUrl("/login")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll();
        ;
    }
}
