package com.canu.controller;

import com.canu.dto.requests.AdminJobCancelRequest;
import com.canu.dto.requests.TemplateRequest;
import com.canu.model.CouponModel;
import com.canu.model.PropertyModel;
import com.canu.services.*;
import com.canu.specifications.*;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    final private AdminService adminSvc;

    final private JobService jobSvc;

    final private PaymentService paymentSvc;

    final private CouponService couponSvc;

    final private SystemRatingService SystemRatingSvc;


    @GetMapping(value = "/member")
    public Object getDetail(Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.getMembers(p, null)));
    }

    @PostMapping(value = "/setup-template")
    public Object setupTemplate(@RequestBody TemplateRequest template) {
        adminSvc.setupTemplate(template);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @GetMapping(value = "/get-template")
    public Object getTemplate(TemplateFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.getTemplate(filter, p)));
    }

    @PostMapping(value = "/rating-criteria/initial")
    public Object initialRatingCriteria(@RequestBody PropertyModel propertyModel) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.initialRatingCriteria(propertyModel)));
    }

    @GetMapping(value = "/get-rating/list")
    public Object getReviewList(JobFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", jobSvc.getAdminRatingList(filter, p)));
    }

    @PostMapping(value = "/cancel_job")
    public Object cancelJob(AdminJobCancelRequest request){
        jobSvc.cancelJobByAdmin(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @PostMapping(value = "/exchange-rate/upsert")
    public Object upsertExchangeRate(@RequestBody List<PropertyModel> propertyModel) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.upsertExchangeRate(propertyModel)));
    }

    @GetMapping(value = "/exchange-rate/list")
    public Object getExchangeRate(PropertyFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.getExchangeRate(filter, p)));
    }

    @GetMapping(value = "/payment/history")
    public ResponseEntity getPaymentHistory(PaymentFilter filter, Pageable p){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", paymentSvc.getPaymentList(filter, p, true)));
    }

    @GetMapping(value = "/coupon/list")
    public Object getCouponList(CouponFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", couponSvc.getCouponForAdmin(filter, p)));
    }

    @PostMapping(value = "/coupon/upsert")
    public ResponseEntity getPaymentHistory(@RequestBody List<CouponModel> propertyModel){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", couponSvc.upsertCoupon(propertyModel)));
    }

    @GetMapping(value = "/review-app/list")
    public Object getAppRevie(SystemReviewFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", SystemRatingSvc.getSystemReview(filter, p)));
    }

//    @GetMapping
//    public Object getRevenue(){
//        adminSvc
//    }
}
