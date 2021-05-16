package com.canu.controller;

import com.canu.dto.requests.AdminJobCancelRequest;
import com.canu.dto.requests.TemplateRequest;
import com.canu.model.PropertyModel;
import com.canu.services.AdminService;
import com.canu.services.JobService;
import com.canu.specifications.JobFilter;
import com.canu.specifications.TemplateFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    final private AdminService adminSvc;

    final private JobService jobSvc;


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

//    @GetMapping
//    public Object getRevenue(){
//        adminSvc
//    }
}
