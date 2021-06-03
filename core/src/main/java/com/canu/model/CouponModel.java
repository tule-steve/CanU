package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "coupon")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CouponModel {

    public enum Type {
        AMOUNT
    }

    public enum Status {
        PENDING,
        AVAILABLE,
        REDEEMED,
        CANCEL
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "code")
    String code;

    @Column(name = "coupon_type")
    @Enumerated(EnumType.STRING)
    Type coupon_type;

    @Column(name = "value")
    BigDecimal value;

    @Column(name = "remaining_amount")
    BigDecimal amount;

    @Column(name = "from_date")
    LocalDateTime fromDate;

    @Column(name = "to_date")
    LocalDateTime toDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status = Status.AVAILABLE;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "coupon")
    List<UserCouponModel> userCoupons = new ArrayList<>();

    @JsonIgnore
    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;
}
