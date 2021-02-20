package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "job")
public class JobModel {

    public enum JobStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCEL;
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    JobStatus status;

    @Column(name = "creationUser")
    Long creationUser;

    @Column(name = "requested_user")
    Long requestedUser;

    @Column(name = "description")
    Long description;








}
