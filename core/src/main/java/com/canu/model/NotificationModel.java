package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "canu_notification")
public class NotificationModel {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    NotificationDetailModel.Type typeNoti;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    CanUModel owner;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", updatable = false, nullable = false)
    NotificationDetailModel detail;

    public void setOwner(CanUModel canu){
        this.owner = canu;
        if(owner != null){
            this.userId = owner.getId();
        }
    }

    public void setDetail(NotificationDetailModel detail){
        this.detail = detail;
        if(detail != null){
            this.detailId = detail.getId();
        }
    }

    @Transient
    String type = "notification";

    @Transient
    Long detailId;

    @PostLoad
    @PostUpdate
    void setUpdateEntity(){
        this.detailId = detail.getId();
        this.userId = owner.getId();
    }

//    public Long getDetailId() {
//        return detail.getId();
//    }

    @Transient
    Long userId;

//    public Long getUserId() {
//        return owner.getId();
//    }


}
