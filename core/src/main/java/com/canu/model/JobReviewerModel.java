package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "job_reviewer")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobReviewerModel {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", updatable = false, nullable = false)
    JobModel job;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", updatable = false, nullable = false)
    CanUModel reviewer;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", updatable = false, nullable = false)
    CanUModel target;

    @Column(name = "rating")
    Integer rating;

    @Column(name = "content")
    String content;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;


}
