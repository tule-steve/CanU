package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "job")
@SQLDelete(sql = "UPDATE job SET status = 'CANCEL' WHERE id = ?")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@Where(clause = "status <> 'CANCEL'")
public class JobModel {

    public enum JobStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCEL,
        REQUEST_CANCEL
    }

    public enum CancelStatus {
        REQUESTED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    JobStatus status = JobStatus.PENDING;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creation_user", updatable = false, nullable = false)
    CanUModel creationUser;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_user")
    CanUModel requestedUser;

    @Column(name = "title")
    String title;

    @Column(name = "content")
    String content;

    @Column(name = "nation")
    String nation;

    @Column(name = "city")
    String city;

    @Column(name = "total")
    Long total;

    @Column(name = "currency")
    String currency;

    @Column(name = "image")
    String image;

    @Column(name = "cancel_reason")
    String reason;

    @Column(name = "admin_reason")
    String adminReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_status")
    CancelStatus cancelStatus;

    @Column(name = "rating")
    Integer rating = 0;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "job_skillset",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_set_id"))
    private List<SkillSetModel> skillSets = new ArrayList<>();

    @Transient
    private Set<Long> service;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "job_tag",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<TagModel> tags = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "job_canu",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "canu_id"))
    private List<CanUModel> canus = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job")
    List<JobReviewerModel> reviewers = new ArrayList<>();

    @JsonIgnore
    @Where(clause = "status <> 'CANCEL'")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job")
    List<PaymentModel> payments = new ArrayList<>();

    @Transient
    private Set<String> keyword;

    @Transient
    private Set<Long> pickupCanI;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

}
