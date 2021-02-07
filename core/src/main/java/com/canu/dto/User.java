package com.canu.dto;

import lombok.Data;

public @Data
class User {
    private final String id;
    private final String name;
    private final String email;
    private final SocialProvider provider;
}
