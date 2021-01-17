package com.canu.controller.cani;

import com.canu.dto.CanUSignUpRequest;
import com.canu.model.CanIModel;
import com.canu.model.CanUModel;
import com.canu.services.CanIService;
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
@RequestMapping(value = "/api/v1/cani")
@RequiredArgsConstructor
public class CanIController {

    final private CanIService canIService;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanIModel request) {
        canIService.signUp(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create account"));
    }

}
