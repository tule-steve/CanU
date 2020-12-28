package com.canu.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "announcement")
public class AnnouncementModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "title is required")
    @Column(name = "title")
    String title;

    @NotBlank(message = "content is required")
    @Column(name = "content")
    String content;

    @NotNull(message = "locale is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "locale")
    CountryModel.Locale locale;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;
}
