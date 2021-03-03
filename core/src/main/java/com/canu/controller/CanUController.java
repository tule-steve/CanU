package com.canu.controller;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.ResetPassWordRequest;
import com.canu.services.CanUService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/canu")
@RequiredArgsConstructor
public class CanUController {

    final private CanUService canUService;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanUSignUpRequest request) {
        return canUService.signUp(request);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity getProfile() {
        return canUService.getProfile();
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity changePassword(@Validated @RequestBody ChangePassWordRequest request) {
        return canUService.changePassword(request);
    }

//    @PostMapping(value = "/uploadFile")
//    public ResponseEntity uploadFile(@RequestParam("gpdkkd") MultipartFile[] gpdkkdFile,
//                                     @RequestParam("certificate") MultipartFile[] cerFiles) throws IOException {
//        return canUService.uploadFile(gpdkkdFile, cerFiles);
//    }

    @PostMapping(value = "/uploadFile")
    public ResponseEntity uploadFile(StandardMultipartHttpServletRequest request) throws IOException {
//        request

        return canUService.updateFileData(request);
    }

    @GetMapping(value = "/verify-email")
    public ResponseEntity verifyEmail() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        canUService.sendVerificationEmail(user.getUsername());
        return ResponseEntity.ok(CommonResponse.buildOkData("Sent verification email"));
    }

    @GetMapping(value = "/email-confirmation")
    public ResponseEntity activeEmail(@RequestParam("token") String token) {
        canUService.confirmEmail(token);
        return ResponseEntity.ok(CommonResponse.buildOkData("activate the account"));
    }

    @GetMapping(value = "/forgot-password")
    public ResponseEntity forgotPassword(@RequestParam("email") String email ) {
        canUService.sendForgetPassword(email);
        return ResponseEntity.ok(CommonResponse.buildOkData("sent password reset mail for user"));
    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity forgotPassword(@Validated @RequestBody ResetPassWordRequest request) {
        canUService.resetPassword(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Change password"));
    }

}
