package com.codegame.security.config;

import com.codegame.security.controller.TwoFactorAuthenticationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stores the oauth authorizationRequest in the session so that it can
 * later be picked by the CustomOAuth2RequestFactory
 * to continue with the authorization flow.
 */
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationFilter.class);

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private OAuth2RequestFactory oAuth2RequestFactory;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    TokenEndpoint tokenEndpoint;

    //These next two are added as a test to avoid the compilation errors that happened when they were not defined.
    public static final String ROLE_TWO_FACTOR_AUTHENTICATED = "ROLE_TWO_FACTOR_AUTHENTICATED";

    public static final String ROLE_TWO_FACTOR_AUTHENTICATION_ENABLED = "ROLE_TWO_FACTOR_AUTHENTICATION_ENABLED";

    @Autowired
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    private boolean twoFactorAuthenticationEnabled(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(
                authority -> ROLE_TWO_FACTOR_AUTHENTICATION_ENABLED.equals(authority.getAuthority())
        );
    }

    private Map<String, String> paramsFromRequest(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            params.put(entry.getKey(), entry.getValue()[0]);
        }
        return params;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if the user hasn't done the two factor authentication.
        if (isAuthenticated() && !hasAuthority(ROLE_TWO_FACTOR_AUTHENTICATED)) {
            Principal principal = request.getUserPrincipal();
            ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(getClientId(principal));

            final Map<String, String> params = paramsFromRequest(request);
            TokenRequest authorizationRequest = oAuth2RequestFactory.createTokenRequest(params, authenticatedClient);
            /* Check if the client's authorities (authorizationRequest.getAuthorities()) or the user's ones
               require two factor authentication. */
            if (twoFactorAuthenticationEnabled(authenticatedClient.getAuthorities()) ||
                twoFactorAuthenticationEnabled(SecurityContextHolder.getContext()
                                                                    .getAuthentication()
                                                                    .getAuthorities())) {


                tokenEndpoint.postAccessToken((Principal) SecurityContextHolder.getContext().getAuthentication(), params);
                // Save the authorizationRequest in the session. This allows the CustomOAuth2RequestFactory
                // to return this saved request to the AuthenticationEndpoint after the user successfully
                // did the two factor authentication.
                request.getSession()
                       .setAttribute(CustomOAuth2RequestFactory.SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME,
                                     authorizationRequest);
                request.getSession().setAttribute("Principal", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

                LOG.debug("doFilterInternal(): redirecting to {}", TwoFactorAuthenticationController.PATH);
                // redirect the the page where the user needs to enter the two factor authentication code
                redirectStrategy.sendRedirect(request, response,
                                              TwoFactorAuthenticationController.PATH + "/" + request.getParameter("username")
                );
                return;
            }
        }

        LOG.debug("doFilterInternal(): without redirect.");

        filterChain.doFilter(request, response);
    }

    /**
     * @param principal the currently authentication principal
     * @return a client id if there is one in the principal
     */
    protected String getClientId(Principal principal) {
        Authentication client = (Authentication) principal;
        if (!client.isAuthenticated()) {
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        }
        String clientId = client.getName();
        if (client instanceof OAuth2Authentication) {
            // Might be a client and user combined authentication
            clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
        }
        return clientId;
    }

    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    private boolean hasAuthority(String checkedAuthority) {

        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(
                authority -> checkedAuthority.equals(authority.getAuthority())
        );
    }

}

