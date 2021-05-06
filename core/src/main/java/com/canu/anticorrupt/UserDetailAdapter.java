package com.canu.anticorrupt;

import com.canu.dto.UserDto;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.security.config.TokenProvider;
import com.canu.security.out.SocialLogin;
import com.canu.services.SocialAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailAdapter implements UserDetailsService, SocialLogin {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailAdapter.class);

    final private CanURepository canURepo;

    @Autowired
    private SocialAuthService authService;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CanUModel user = canURepo.findByEmail(username);
        if (user == null) {
            logger.error("User {} not found", username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CANU");
        grantList.add(authority);

        if (user.isRegisterCanI()) {
            authority = new SimpleGrantedAuthority("ROLE_CANI");
            grantList.add(authority);
        }

        UserDetails userDetails = new User(user.getEmail(),
                                           user.getPassword() == null ? "" : user.getPassword(),
                                           grantList);

        return userDetails;
    }

    @Override
    public String processOAuth2User(String provider, OAuth2Authentication authResult) {
        UserDto user = authService.extractUserFromAuthInfo(authResult);
        String token = tokenProvider.createToken(user.getEmail());

        CanUModel currUser = canURepo.findByEmail(user.getEmail());

        if (currUser == null) {
            CanUModel data = new CanUModel();
            data.setEmail(user.getEmail());
            data.setProviderType(user.getProvider().toString());
            canURepo.save(data);
        }

        return token;
    }

}
