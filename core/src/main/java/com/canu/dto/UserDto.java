package com.canu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private final String id;
    private final String name;
    private final String email;
    private final SocialProvider provider;
    private final String firstName;
    private final String lastName;
    private final String avatar;

}
