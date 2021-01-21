package com.canu.controller;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.SocialSignUpRequest;
import com.canu.services.CanUService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/canu")
@RequiredArgsConstructor
public class CanUController {

    final private CanUService canUService;
    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanUSignUpRequest request) {
        canUService.signUp(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create account"));
    }

    @PostMapping(value = "/social")
    public ResponseEntity signUpBySocial(@Validated @RequestBody SocialSignUpRequest request) {
        canUService.signUpBySocial(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create account"));
    }


}
