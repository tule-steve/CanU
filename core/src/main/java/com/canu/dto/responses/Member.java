package com.canu.dto.responses;

import com.canu.model.CanIModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
public class Member {

    final Long userId;
//    String name;
    final String firstName;
    final String lastName;
    final String email;
    final LocalDateTime createdAt;

    final Integer createdJob;
    final Integer finishedJob;
    final Integer processingJob;
    @JsonIgnore
    final Long caniId;

    CanIModel cani;

}
