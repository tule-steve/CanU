package com.canu.controller;

import com.canu.dto.requests.UpdateJobRequest;
import com.canu.repositories.CanURepository;
import com.canu.services.JobService;
import com.canu.specifications.JobFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/cancel-job")
    public Object cancelJob(@RequestBody Map<String, Object> request) {
        jobSvc.cancelJob(Long.parseLong(request.get("id").toString()));
        return ResponseEntity.ok(CommonResponse.buildOkData("cancel the job"));
    }

    @PostMapping(value = "/update-job")
    public Object updateJob(@RequestBody UpdateJobRequest request) {
        jobSvc.updateJob(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated job"));
    }


}