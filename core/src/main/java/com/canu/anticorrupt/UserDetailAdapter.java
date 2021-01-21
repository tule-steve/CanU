package com.canu.anticorrupt;

import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailAdapter implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailAdapter.class);

    final private CanURepository canUSvc;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CanUModel user = canUSvc.findByEmail(username);
        if (user == null) {
            logger.error("User {} not found", username);
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CANU");
        grantList.add(authority);

        if(user.getCanIModel() != null){
            authority = new SimpleGrantedAuthority("ROLE_CANI");
            grantList.add(authority);
        }

        UserDetails userDetails = new User(user.getEmail(),
                                           user.getPassword(),
                                           grantList);

        return userDetails;
    }
}
