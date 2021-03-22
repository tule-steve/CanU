package com.canu.dto;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "chat_message")
public class MessageBean {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "message")
    private String message;

    @NotNull
    @Column(name = "from_user")
    private Long fromUser;

    @NotNull
    @Column(name = "to_user")
    private Long toUser;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

}
