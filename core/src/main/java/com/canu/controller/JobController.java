package com.canu.controller;

import com.canu.dto.requests.UpdateJobRequest;
import com.canu.dto.requests.UpdateJobStatusRequest;
import com.canu.dto.requests.UpdateSubStatusRequest;
import com.canu.model.PaymentModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.PaymentRepository;
import com.canu.repositories.ReportRepository;
import com.canu.services.AmazonS3Service;
import com.canu.services.JobService;
import com.canu.services.PaymentService;
import com.canu.specifications.JobFilter;
import com.canu.specifications.JobRatingFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/job")
@RequiredArgsConstructor
public class JobController {

    final private JobService jobSvc;

    final private CanURepository canURepo;

    @GetMapping(value = "/list")
    public Object getDetail(JobFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", jobSvc.showJobList(filter, p)));
    }

    @PostMapping(value = "/pick-up")
    public Object pickupJob(@RequestBody Map<String, Object> request) {
        jobSvc.pickUpJob(Long.parseLong(request.get("id").toString()));
        return ResponseEntity.ok(CommonResponse.buildOkData("picked up job"));
    }

    @PostMapping(value = "/sub-status")
    public Object updateSubStatus(@RequestBody @Validated UpdateSubStatusRequest request) {

        return ResponseEntity.ok(CommonResponse.buildOkData("updated sub status", jobSvc.updateSubStatus(request)));
    }

    @PostMapping(value = "/cancel-job")
    public Object cancelJob(@RequestBody Map<String, Object> request) {
        jobSvc.cancelJob(Long.parseLong(request.get("id").toString()), String.valueOf(request.get("reason")));
        return ResponseEntity.ok(CommonResponse.buildOkData("cancel the job"));
    }

    @PostMapping(value = "/update-job")
    public Object updateJob(@RequestBody UpdateJobRequest request) {
        jobSvc.updateJob(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated job"));
    }

    @PostMapping(value = "/start-job")
    public Object startJob(@RequestBody @Validated UpdateJobStatusRequest request) {
        jobSvc.startJob(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @PostMapping(value = "/cani-complete")
    public Object completeJobByCanI(@RequestBody Map<String, Object> request) {
        Long jobId = Long.parseLong(request.get("jobId").toString());
        jobSvc.completeJobByCanI(jobId);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @PostMapping(value = "/canu-complete")
    public Object completeJobByCanU(@RequestBody Map<String, Object> request) {
        Long jobId = Long.parseLong(request.get("jobId").toString());
        jobSvc.completeJobByCanU(jobId);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @GetMapping(value = "/get-rating/list")
    public Object getRatingList(JobRatingFilter filter, Pageable p) {

        return ResponseEntity.ok(CommonResponse.buildOkData("OK", jobSvc.getRatingList(filter, p)));
    }

    @GetMapping(value = "/get-review/list")
    public Object getReviewList(JobRatingFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", jobSvc.getReviewList(filter, p)));
    }

    @GetMapping(value = "/unpaid-list")
    public Object getUnpaidList(JobFilter filter) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", jobSvc.getUnpaidJobList(filter)));
    }

    private final ReportRepository reportRepo;
    private final AmazonS3Service s3Svc;
    private final PaymentService paymentSvc;
    private final PaymentRepository paymentRepo;
    @Transactional
    @GetMapping(value = "/test/{id}")
    public Object test(@PathVariable Long id) {
//        s3Svc.listOut();

//        LocalDate date = LocalDate.now().minusDays(1);
//        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.MIDNIGHT);
//        LocalDateTime enđDate = LocalDateTime.of(date, LocalTime.MAX);
//        reportRepo.updateAvenue(startDate, enđDate);
        PaymentModel model = paymentRepo.findById(id).get();
        paymentSvc.refund(model.getTransactionId(), model);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }


}
