package com.canu.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipantDto {
    Long id;

    String name;

    String avatar;

    String lastMessage;

    LocalDateTime createdAt;


}
