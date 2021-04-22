package com.canu.model;

import com.common.dtos.validation.ExtendedEmailValidator;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "support_request")
public class SupportRequestModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "name is required")
    @Column(name = "name")
    String name;

    @NotBlank(message = "title is required")
    @Column(name = "title")
    String title;

    @NotBlank(message = "content is required")
    @Column(name = "content")
    String content;

    @ExtendedEmailValidator(message = "email's format is not correct")
    @Column(name = "email")
    String email;

    @NotBlank(message = "phone is required")
    @Column(name = "phone")
    String phone;

    @Column(name = "note")
    String note;

    @Column(name = "status")
    JobModel.JobStatus status;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;
}
