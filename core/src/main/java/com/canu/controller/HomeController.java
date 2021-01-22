package com.canu.controller;

import com.canu.model.User;
import com.canu.services.SocialAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@RestController
public class HomeController {

    @Autowired
    private SocialAuthService authService;
    
    @GetMapping(value = "/")
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


        
        return user;
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/logout")
    public String logout() {
        return "login";
    }
}
