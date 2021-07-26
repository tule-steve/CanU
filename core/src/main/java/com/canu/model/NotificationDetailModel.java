package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "notification")
public class NotificationDetailModel {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDetailModel.class);

    public static final ObjectMapper mapper = new ObjectMapper();

    public enum Type {
        VERIFIED_EMAIL,
        CREATE_JOB,
        POST_JOB,
        PICK_JOB,
        PAYMENT_REMINDER,
        CANU_TOPPED_UP,
        TOPPED_UP,
        RECEPTION,
        PREPARATION,
        PACKAGED,
        DELIVERY,
        CANI_COMPLETE_JOB,
        JOB_COMPLETED,
        CANU_PAID_FOR_CANI,
        PAID_FOR_CANI,
        INVALID_PAYPAL_ACCOUNT,
        CONFIRM_PAYMENT,
        ASSIGNED_JOB,
        REQUESTED_CANI,
        CANCEL_JOB_BY_CANU,
        CANCEL_JOB_FEEDBACK_YES,
        CANCEL_JOB_FEEDBACK_NO,
        ADMIN_CANU_TOPPED_UP,
        ADMIN_JOB_COMPLETED,
        ADMIN_SUPPORT_REQUEST,
        CUSTOM;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toTitleString() {
            return super.toString() + "_title";
        }

        public String toEmailString() {
            return super.toString() + "_email";
        }
    }
//    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "title")
    private String title;

    @Column(name = "data")
    private String data;

    @Column(name = "description")
    String description;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "detail")
    List<NotificationModel> notifications = new ArrayList<>();

    @Transient
    String receivedName;

    @Transient
    Long userId;



}
