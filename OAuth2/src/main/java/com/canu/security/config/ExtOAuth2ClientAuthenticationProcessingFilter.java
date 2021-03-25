package com.canu.security.config;

import com.canu.security.out.SocialLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

public class ExtOAuth2ClientAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(ExtOAuth2ClientAuthenticationProcessingFilter.class);

    final SocialLogin socialLogin;

    public ExtOAuth2ClientAuthenticationProcessingFilter(String defaultFilterProcessesUrl, SocialLogin socialLogin) {
        super(defaultFilterProcessesUrl);
        this.socialLogin = socialLogin;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException,
                                                                                                 ServletException {
//        ((AbstractAuthenticationTargetUrlRequestHandler) super.getSuccessHandler()).setDefaultTargetUrl("/api/home");
//        super.successfulAuthentication(request, response, chain, authResult);
        try {
            OAuth2Authentication auth = (OAuth2Authentication)authResult;
//            request.getSession().setAttribute("Principal", authResult);
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            String token = socialLogin.processOAuth2User("facebook", auth);
            body.put("token", token);
            body.put("expiredIn", 86400);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(body.toString());
            out.flush();
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
