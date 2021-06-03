package com.canu.services;

import com.canu.dto.SocialProvider;
import com.canu.dto.UserDto;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SocialAuthService {
    private static final String GOOGLE_ID_FIELD_NAME = "sub";

    private static final String FB_DETAILS_NAME_PARAM = "name";

    private static final String FB_DETAILS_EMAIL_PARAM = "email";

    private static final String FB_FIRST_NAME_PARAM = "first_name";

    private static final String FB_LAST_NAME_PARAM = "last_name";

    private static final String GG_FIRST_NAME_PARAM = "given_name";

    private static final String GG_LAST_NAME_PARAM = "family_name";

    private static final String GG_AVATAR_PARAM = "picture";

    private static final Logger logger = LoggerFactory.getLogger(SocialAuthService.class);

    public UserDto extractUserFromAuthInfo(@NonNull OAuth2Authentication principal) {
        return extractExternalUser(principal);
    }

    private UserDto extractExternalUser(@NonNull OAuth2Authentication oAuth) {
        Map<String, String> details = (Map<String, String>) oAuth.getUserAuthentication().getDetails();

        SocialProvider socialProvider;
        String extIdStr;
        String firstName;
        String lastName;
        String avatar = null;
        if (isGoogle(details)) {
            socialProvider = SocialProvider.GOOGLE;
            extIdStr = details.get(GOOGLE_ID_FIELD_NAME);
            firstName = details.get(GG_FIRST_NAME_PARAM);
            lastName = details.get(GG_LAST_NAME_PARAM);
            avatar = details.get(GG_AVATAR_PARAM);
        } else {
            socialProvider = SocialProvider.FACEBOOK;
            extIdStr = (String) oAuth.getUserAuthentication().getPrincipal();
            firstName = details.get(FB_FIRST_NAME_PARAM);
            lastName = details.get(FB_LAST_NAME_PARAM);

            try {
                avatar = ((Map<String, LinkedHashMap<String, LinkedHashMap>>) oAuth.getUserAuthentication()
                                                                                   .getDetails()).get(
                        "picture").get("data").get("url").toString();
            } catch (Exception ex) {

            }
        }
        return UserDto.builder()
                      .id(extIdStr)
                      .name(details.get(FB_DETAILS_NAME_PARAM))
                      .email(details.get(FB_DETAILS_EMAIL_PARAM))
                      .provider(socialProvider)
                      .firstName(firstName)
                      .lastName(lastName)
                      .avatar(avatar)
                      .build();
    }

    private boolean isGoogle(@NonNull Map<String, String> details) {
        return details.containsKey(GOOGLE_ID_FIELD_NAME);
    }
}
