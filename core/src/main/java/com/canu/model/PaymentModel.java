package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "payment")
public class PaymentModel {

    public enum Status {
        PENDING,
        TOPPED_UP,
        PAID,
        CANCEL,
        REFUNDED
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.PENDING;

    @Column(name = "total", columnDefinition = "decimal(15, 2)", precision = 15, scale = 2)
    BigDecimal total;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "currency")
    String currency = "usd";

    @Column(name = "transaction_id")
    String transactionId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", updatable = false)
    JobModel job;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    CanUModel owner;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", updatable = false)
    UserCouponModel userCoupon;


}
