package com.canu.security.config;

import com.canu.exception.GlobalValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
        try {
            request.getSession().setAttribute("Principal", authResult);
        } catch (Exception ex) {
            logger.error("Error on add authResult to session", ex);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        OAuth2AccessToken accessToken = createAccessToken(request);

        if (accessToken == null) {
            BadCredentialsException bad = new BadCredentialsException("access token is require");
            request.getSession().setAttribute("error.message", "access token is require");
            throw bad;
        }
        if (accessToken.isExpired()) {
            BadCredentialsException bad = new BadCredentialsException("access token is expire");
            request.getSession().setAttribute("error.message", "access token is expired");
            throw bad;
        }
        restTemplate.getOAuth2ClientContext().setAccessToken(accessToken);
        return super.attemptAuthentication(request, response);
    }

    private OAuth2AccessToken createAccessToken(HttpServletRequest request) {
        try {
            String tokenValue = request.getParameter("social_access_token");
            String tokenType = request.getParameter(OAuth2AccessToken.TOKEN_TYPE);
            String refreshToken = request.getParameter(OAuth2AccessToken.REFRESH_TOKEN);
            Long expiresAt = Long.valueOf(request.getParameter("expires_at"));
            Set<String> scope = OAuth2Utils.parseParameterList(request.getParameter(OAuth2AccessToken.SCOPE));

            if (tokenValue == null || expiresAt == null) {
                return null;
            }

            DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(tokenValue);
            accessToken.setTokenType(tokenType);
            if (expiresAt != null) {
                accessToken.setExpiration(new Date(expiresAt));
            }
            if (refreshToken != null) {
                accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(refreshToken));
            }

            return accessToken;

        } catch (Exception ex) {
            return null;
        }
    }

}
