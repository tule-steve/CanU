package com.canu.controller;

import com.canu.model.CanIModel;
import com.canu.services.CanIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/cani")
@RequiredArgsConstructor
public class CanIController {

    final private CanIService canIService;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@Validated @RequestBody CanIModel request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return canIService.signUp(request, user.getUsername());
    }

    @GetMapping(value = "/detail")
    public ResponseEntity getDetail() {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return canIService.getDetail(user.getUsername());
    }

}
