package com.canu.dto.responses;

import com.canu.model.AuthProviderModel;
import com.canu.model.CanIModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;


@Data
@AllArgsConstructor
public class Member {

    Long userId;
    String name;
    String email;
    LocalDateTime createdAt;

    Integer createdJob;
    Integer finishedJob;
    Integer processingJob;

}
