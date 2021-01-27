package com.canu.controller;

import com.canu.dto.requests.LoginRequest;
import com.canu.dto.responses.Token;
import com.canu.exception.GlobalValidationException;
import com.canu.model.User;
import com.canu.security.config.TokenProvider;
import com.canu.services.SocialAuthService;
import com.common.dtos.CommonResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private SocialAuthService authService;

    @Autowired
    private TokenProvider tokenProvider;
//
//    @Qualifier("jwtTokenServices")
//    @Autowired
//    AuthorizationServerTokenServices tokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @GetMapping(value = "/api/home")
    public Object home() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        Principal principal;
        if (session != null) {
            principal = (Principal)session.getAttribute("Principal");
            session.removeAttribute("Principal");
        } else {
            principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        User user = authService.extractUserFromAuthInfo(principal);
        Token tokenResponse = new Token(tokenProvider.createToken(user.getEmail()), 86400L);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(loginRequest.getEmail());
        return ResponseEntity.ok(new Token(tokenProvider.createToken(loginRequest.getEmail()), 86400L));
    }

    @RequestMapping(value = "/api/logout")
    public String logout() {
        return "login";
    }

    @GetMapping("/error")
    public Object error(HttpServletRequest request) {
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return new ResponseEntity(CommonResponse.buildBadRequestData(message), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/api/image/{userId}/{fileName}")
    public void getImage(@PathVariable("userId") String userId, @PathVariable("fileName") String fileName, HttpServletResponse response) throws
                                                                                                                                         IOException {
        String fileUrl = System.getProperty("user.dir") + "/image/" + userId + "/" + fileName;
        File initialFile = new File(fileUrl);
        InputStream targetStream = new FileInputStream(initialFile);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(targetStream, response.getOutputStream());
    }
}
