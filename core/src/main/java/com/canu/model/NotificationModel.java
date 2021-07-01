package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "canu_notification")
public class NotificationModel {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDetailModel.class);

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @JsonIgnore
    @Column(name = "data")
    String extData;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    NotificationDetailModel.Type typeNoti;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "is_canu")
    private Boolean isCanu = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    CanUModel owner;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", updatable = false, nullable = false)
    NotificationDetailModel detail;

    public void setOwner(CanUModel canu) {
        this.owner = canu;
        if (owner != null) {
            this.userId = owner.getId();
        }
    }

    public void setDetail(NotificationDetailModel detail) {
        this.detail = detail;
        if (detail != null) {
            this.detailId = detail.getId();
        }
    }

    @Transient
    String type = "notification";

    @Transient
    Long detailId;

    @PostLoad
    @PostUpdate
    void setUpdateEntity() {
        this.detailId = detail.getId();
        if (owner != null) {
            this.userId = owner.getId();
        }

        if (!StringUtils.isEmpty(extData)) {
            try {
                data = NotificationDetailModel.mapper.readValue(extData, Map.class);
            } catch (JsonProcessingException ex) {
                logger.error("error on saving Notification", ex);
            }
        }
    }

    //    public Long getDetailId() {
    //        return detail.getId();
    //    }

    @Transient
    Long userId;

    //    public Long getUserId() {
    //        return owner.getId();
    //    }

    @Transient
    Object data;

    public void setData(Object extData){
        data = extData;
        try {
            setExtData(NotificationDetailModel.mapper.writeValueAsString(extData));
        } catch (Exception ex){
            logger.error("error on parse ext data for admin notification", ex);
        }
    }

}
