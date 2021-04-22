package com.canu.controller;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.RatingUserRequest;
import com.canu.dto.requests.ResetPassWordRequest;
import com.canu.dto.responses.JobDto;
import com.canu.dto.responses.Member;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.PropertyModel;
import com.canu.services.*;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/canu")
@RequiredArgsConstructor
public class CanUController {

    final private CanUService canUService;

    final private JobService jobSvc;

    final ChatService chatSvc;

    final private AdminService adminSvc;

    final private SmsService smsSvc;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanUSignUpRequest request) {
        return canUService.signUp(request);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity getProfile() {
        return canUService.getProfile();
    }

    @PostMapping(value = "/update-profile")
    public ResponseEntity getProfile(@Validated @RequestBody CanUModel request) {
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
    public ResponseEntity forgotPassword(@RequestParam("email") String email) {
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
        JobDto job = jobSvc.postJob(request);
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

    @PostMapping(value = "/delete-chat")
    public ResponseEntity deleteConservation(@RequestBody Map<String, Object> request) {
        Long participantId = Long.parseLong(request.get("participantId").toString());
        Long lastedMessageId = Long.parseLong(request.get("messageId").toString());
        chatSvc.deleteConservation(participantId, lastedMessageId);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @GetMapping(value = "/view-profile/{userId}")
    public ResponseEntity viewProfile(@PathVariable Long userId, Pageable p) {
        Page<Member> result = adminSvc.getMembers(p, userId);
        canUService.updateFavoriteFlag(result);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", result));
    }

    @GetMapping(value = "/participant-list")
    public ResponseEntity getChatParticipant(Pageable p) {

        return ResponseEntity.ok(CommonResponse.buildOkData("OK", chatSvc.getParticipant()));
    }

    @GetMapping(value = "/add-favorite/{canIUserId}")
    public ResponseEntity viewProfile(@PathVariable Long canIUserId) {
        return canUService.addFavoriteCani(canIUserId);

    }

    @GetMapping(value = "/get-favorite")
    public ResponseEntity getFavorite() {
        return ResponseEntity.ok(CommonResponse.buildOkData("Ok", canUService.getFavoriteList()));
    }

    @GetMapping(value = "/get-notification")
    public ResponseEntity getNotification(Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("Ok", canUService.getNotification(p)));
    }

    @GetMapping(value = "/get-notification-detail/{notificationId}")
    public ResponseEntity getNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok(CommonResponse.buildOkData("Ok", canUService.getNotificationDetail(notificationId)));
    }

    @PostMapping(value = "/phone-verification")
    public ResponseEntity verifyPhone(@RequestBody Map<String, Object> request) {
        smsSvc.sendSms(request.get("phoneNumber").toString());
        return ResponseEntity.ok(CommonResponse.buildOkData("sending SMS"));
    }

    @PostMapping(value = "/check-mobile-code")
    public ResponseEntity verifyCode(@RequestBody Map<String, Object> request) {
        smsSvc.verifyCode(request.get("code").toString());
        return ResponseEntity.ok(CommonResponse.buildOkData("activate the account"));
    }

    @GetMapping(value = "/get-user-rating")
    public ResponseEntity getPlatformRating(@RequestParam(defaultValue = "en") String locale) {

        return ResponseEntity.ok(CommonResponse.buildOkData("OK", canUService.getUserRating(locale)));
    }

    @PostMapping(value = "/update-user-rating")
    public ResponseEntity updatePlateFormRating(@RequestBody List<PropertyModel> request) {
        canUService.updateUserRating(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @PostMapping(value = "/rating-user")
    public ResponseEntity ratPlatform(@RequestBody RatingUserRequest request) {
        jobSvc.ratingUser(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("add review"));
    }

}
