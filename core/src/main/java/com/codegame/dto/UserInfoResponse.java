package com.codegame.dto;

import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Value
public class UserInfoResponse {

    String email;
    Collection<String> roles;
}
