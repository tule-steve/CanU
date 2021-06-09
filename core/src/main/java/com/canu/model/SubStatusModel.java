package com.canu.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "sub_status")
public class SubStatusModel {

    public enum Status {
        RECEPTION,
        PREPARATION,
        PACKAGED,
        DELIVERY
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;


}
