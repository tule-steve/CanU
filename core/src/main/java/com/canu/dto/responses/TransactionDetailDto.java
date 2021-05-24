package com.canu.dto.responses;

import com.canu.model.CouponModel;
import com.canu.model.JobModel;
import com.canu.model.PaymentModel;
import com.canu.model.PropertyModel;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

@Data
public class TransactionDetailDto {

    public TransactionDetailDto(PaymentModel payment, JobModel job, EntityManager em, PropertyModel pointExRate) {
        this.payment = payment;
        if (payment.getUserCoupon() != null && pointExRate != null) {
            couponDetail = payment.getUserCoupon().getCoupon();

            couponDetail.setValue(couponDetail.getValue()
                                              .multiply(new BigDecimal(pointExRate.getProperty())));
            payment.setTotal(payment.getTotal().subtract(couponDetail.getValue()));
            em.detach(couponDetail);
        }
        this.fullTotal = BigDecimal.valueOf(job.getTotal());
        this.job = job;

        this.cpointUsed = payment.getCpointUsed();

        if (cpointUsed != null && pointExRate != null) {
            cpointInCurrency = BigDecimal.valueOf(cpointUsed).multiply(new BigDecimal(pointExRate.getProperty()));
            payment.setTotal(payment.getTotal().subtract(cpointInCurrency));
        }

    }

    @JsonUnwrapped
    PaymentModel payment;

    CouponModel couponDetail;

    BigDecimal fullTotal;

    JobModel job;

    Integer cpointUsed;

    BigDecimal cpointInCurrency;

    //    @JsonAnyGetter
    //    public PaymentModel getPayment(){
    //        return payment;
    //    }
}
