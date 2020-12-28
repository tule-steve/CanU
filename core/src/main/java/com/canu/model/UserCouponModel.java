package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "user_coupon")
public class UserCouponModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    CanUModel owner;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", updatable = false)
    CouponModel coupon;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CouponModel.Status status = CouponModel.Status.AVAILABLE;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userCoupon")
    List<PaymentModel> payments = new ArrayList<>();

}
