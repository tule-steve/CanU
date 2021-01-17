package com.canu.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExtOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(ExtOAuth2ClientAuthenticationProcessingFilter.class);
    public ExtOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException,
                                                                                                 ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        try{
            request.getSession().setAttribute("Principal", authResult);
        } catch (Exception ex){
            logger.error("Error on add authResult to session", ex);
        }
    }

}
