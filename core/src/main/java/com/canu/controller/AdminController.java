package com.canu.controller;

import com.canu.model.TemplateModel;
import com.canu.services.AdminService;
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

    @GetMapping(value = "/member")
    public Object getDetail(Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.getMembers(p, null)));
    }

    @PostMapping(value = "/setup-template")
    public Object setupTemplate(@RequestBody TemplateModel template) {
        adminSvc.setupTemplate(template);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }
}
