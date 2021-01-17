package com.canu.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Order(20)
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

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
            .addFilterAt(ssoFilter(), BasicAuthenticationFilter.class)
            .logout()
            .logoutSuccessUrl("/login")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll();
        ;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResource() {
        return new ResourceServerProperties();
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(createSsoFilter("/facebook/login", facebook(), facebookResource()));
        filters.add(createSsoFilter("/google/login", google(), googleResource()));

        filter.setFilters(filters);

        return filter;
    }

    private OAuth2ClientAuthenticationProcessingFilter createSsoFilter(String filterUrl, OAuth2ProtectedResourceDetails oAuthResourceDetails, ResourceServerProperties resourceServerProperties) {
        OAuth2ClientAuthenticationProcessingFilter oAuthFilter = new OAuth2ClientAuthenticationProcessingFilter(filterUrl);
        OAuth2RestTemplate oAuthTemplate = new OAuth2RestTemplate(oAuthResourceDetails, oauth2ClientContext);
        oAuthFilter.setRestTemplate(oAuthTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceServerProperties.getUserInfoUri(), oAuthResourceDetails.getClientId());
        tokenServices.setRestTemplate(oAuthTemplate);
        oAuthFilter.setTokenServices(tokenServices);

        return oAuthFilter;
    }
}
