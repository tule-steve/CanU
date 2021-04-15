package com.canu.security.out;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface SocialLogin {

    String processOAuth2User(String provider, OAuth2Authentication authResult);


}
