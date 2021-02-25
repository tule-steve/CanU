package com.canu.controller;

import com.canu.model.CanIModel;
import com.canu.services.CanIService;
import com.common.dtos.CommonResponse;
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
