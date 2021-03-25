package com.canu.services;

import com.canu.dto.SocialProvider;
import com.canu.dto.UserDto;
import lombok.NonNull;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SocialAuthService {
    private static final String GOOGLE_ID_FIELD_NAME = "sub";

    private static final String AUTH_DETAILS_NAME_PARAM = "name";

    private static final String AUTH_DETAILS_EMAIL_PARAM = "email";

    public UserDto extractUserFromAuthInfo(@NonNull OAuth2Authentication principal) {
            return extractExternalUser(principal);
    }

    private UserDto extractExternalUser(@NonNull OAuth2Authentication oAuth) {
        Map<String, String> details = (Map<String, String>) oAuth.getUserAuthentication().getDetails();

        SocialProvider socialProvider;
        String extIdStr;
        if (isGoogle(details)) {
            socialProvider = SocialProvider.GOOGLE;
            extIdStr = details.get(GOOGLE_ID_FIELD_NAME);
        } else {
            socialProvider = SocialProvider.FACEBOOK;
            extIdStr = (String) oAuth.getUserAuthentication().getPrincipal();
        }
        return new UserDto(extIdStr,
                           details.get(AUTH_DETAILS_NAME_PARAM),
                           details.get(AUTH_DETAILS_EMAIL_PARAM),
                           socialProvider);
    }

    private boolean isGoogle(@NonNull Map<String, String> details) {
        return details.containsKey(GOOGLE_ID_FIELD_NAME);
    }
}
