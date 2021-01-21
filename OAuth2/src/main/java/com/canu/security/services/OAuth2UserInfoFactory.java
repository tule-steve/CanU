package com.canu.security.services;

import com.canu.security.DTOs.FacebookOAuth2UserInfo;
import com.canu.security.DTOs.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
            return new FacebookOAuth2UserInfo(attributes);
    }
}
