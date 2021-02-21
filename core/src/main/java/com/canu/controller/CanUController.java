package com.canu.controller;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.SocialSignUpRequest;
import com.canu.model.CanUModel;
import com.canu.security.config.TokenProvider;
import com.canu.services.CanUService;
import com.common.dtos.CommonResponse;
import com.common.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/canu")
@RequiredArgsConstructor
public class CanUController {

    final private CanUService canUService;
    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanUSignUpRequest request) {
        return canUService.signUp(request);
    }


    @PostMapping(value = "/change-password")
    public ResponseEntity changePassword(@Validated @RequestBody ChangePassWordRequest request) {
        return canUService.changePassword(request);
    }


    @PostMapping(value = "/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        return canUService.uploadImage(multipartFile);
    }

    @GetMapping(value = "/verify-email")
    public ResponseEntity requestJob() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        canUService.sendVerificationEmail(user.getUsername());
        return ResponseEntity.ok(CommonResponse.buildOkData("Sent verification email"));
    }

    @GetMapping(value = "/email-confirmation")
    public ResponseEntity requestJob(@RequestParam("token")  String token) {
        canUService.confirmEmail(token);
        return ResponseEntity.ok(CommonResponse.buildOkData("activate the account"));
    }


}
