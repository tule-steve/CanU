package com.canu.controller;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.ResetPassWordRequest;
import com.canu.dto.responses.JobDto;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.services.CanUService;
import com.canu.services.ChatService;
import com.canu.services.JobService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/canu")
@RequiredArgsConstructor
public class CanUController {

    final private CanUService canUService;

    final private JobService jobSvc;

    final ChatService chatSvc;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanUSignUpRequest request) {
        return canUService.signUp(request);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity getProfile() {
        return canUService.getProfile();
    }


    @PostMapping(value = "/update-profile")
    public ResponseEntity getProfile(@Validated @RequestBody  CanUModel request) {
        return canUService.updateProfile(request);
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

    @PostMapping(value = "/post-job")
    public ResponseEntity postJob(@Validated @RequestBody JobModel request) {
        JobModel jobEntity = jobSvc.postJob(request);
        JobDto job = jobSvc.getJobDetail(jobEntity.getId());
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", job));
    }

    @GetMapping(value = "/job-detail/{jobId}")
    public ResponseEntity postJob(@PathVariable Long jobId) {
        JobDto job = jobSvc.getJobDetail(jobId);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", job));
    }

    @GetMapping(value = "/show-chat/{partnerId}")
    public ResponseEntity showChatHistory(@PathVariable Long partnerId, Pageable p) {

        return ResponseEntity.ok(CommonResponse.buildOkData("OK", chatSvc.getChatHistory(partnerId, p)));
    }

//    @GetMapping(value = "/participant-list")
//    public ResponseEntity getChatParticipant() {
//        chatSvc.
//    }
}
