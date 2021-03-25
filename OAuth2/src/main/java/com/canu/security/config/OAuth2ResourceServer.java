package com.canu.security.config;

import com.canu.security.out.SocialLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Order(20)
@EnableConfigurationProperties
@EnableWebSecurity
public class OAuth2ResourceServer extends WebSecurityConfigurerAdapter {

    @Value("${google.resource.userInfoUri}")
    private String googleResUri;


    @Value("${facebook.resource.userInfoUri}")
    private String facebookResUri;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private SocialLogin socialLogin;


    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http.cors().and()
//            //            .anonymous().disable()
//            //            .requestMatchers().antMatchers("/api/**", "**/secure/**").and()
//            .authorizeRequests()
//            .antMatchers("/secure/two_factor_authentication/**", "/google/**", "/facebook/**", "/api/**").permitAll()
//            //            .antMatchers("/api/**").access("hasAnyRole('ADMIN','USER')")
//            .anyRequest().authenticated()
//
//            //            .and()
//            //            .oauth2Login()
//            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
//            .and()
//            .addFilterAt(ssoFilter(), BasicAuthenticationFilter.class)
//            .logout()
//            .logoutSuccessUrl("/login")
//            .deleteCookies("JSESSIONID")
//            .invalidateHttpSession(true)
//            .permitAll();

        http
                .cors()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .csrf()
                    .disable()
                .formLogin()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
            .antMatcher("/**").authorizeRequests()
            .antMatchers("/login/**", "/logout/**", "/", "/error", "/api/**", "/login", "/api/v1/data/**", "/image/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterAt(ssoFilter(), BasicAuthenticationFilter.class)
            .logout()
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll();
        // Add our custom Token based authentication filter
//        http.addFilterBefore(tokenAuthenticationFilter(), BasicAuthenticationFilter.class);
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

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(createSsoFilter("/api/facebook/login", facebook(), facebookResUri));
        filters.add(createSsoFilter("/api/google/login", google(), googleResUri));

        filter.setFilters(filters);

        return filter;
    }

    private OAuth2ClientAuthenticationProcessingFilter createSsoFilter(String filterUrl, OAuth2ProtectedResourceDetails oAuthResourceDetails, String resourceServerProperties) {
        OAuth2ClientAuthenticationProcessingFilter oAuthFilter = new ExtOAuth2ClientAuthenticationProcessingFilter(filterUrl, socialLogin);
        OAuth2RestTemplate oAuthTemplate = new OAuth2RestTemplate(oAuthResourceDetails, oauth2ClientContext);
        oAuthFilter.setRestTemplate(oAuthTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceServerProperties, oAuthResourceDetails.getClientId());
        tokenServices.setRestTemplate(oAuthTemplate);
        oAuthFilter.setTokenServices(tokenServices);

        return oAuthFilter;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
}
