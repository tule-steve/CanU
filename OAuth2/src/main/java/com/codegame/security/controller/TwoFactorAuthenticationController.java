package com.codegame.security.controller;

import com.codegame.exception.GlobalValidationException;
import com.codegame.security.DTOs.LoginResponse;
import com.codegame.security.config.CustomOAuth2RequestFactory;
import com.codegame.security.config.TwoFactorAuthenticationFilter;
import com.codegame.security.services.OTPService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping(TwoFactorAuthenticationController.PATH)
@RequiredArgsConstructor
public class TwoFactorAuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(TwoFactorAuthenticationController.class);
    
    public static final String PATH = "/secure/two_factor_authentication";


    final private OTPService otpSvc;


    @RequestMapping(path="/{email}", method = RequestMethod.GET)
    public ResponseEntity auth(@PathVariable("email") String email, HttpSession session) {
        if (isAuthenticatedWithAuthority(TwoFactorAuthenticationFilter.ROLE_TWO_FACTOR_AUTHENTICATED)) {
            LOG.debug("User {} already has {} authority - no need to enter code again", TwoFactorAuthenticationFilter.ROLE_TWO_FACTOR_AUTHENTICATED);
            //throw ....;
        }
        else if (session.getAttribute(CustomOAuth2RequestFactory.SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME) == null) {
            LOG.debug("Error while entering 2FA code - attribute {} not found in session.", CustomOAuth2RequestFactory.SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME);
            //throw ....;
        }

        LOG.debug("auth() HTML.Get");
        try {
            otpSvc.sendEmail(email);
        } catch (Exception ex){
            LOG.error("Error on sending the OTP email", ex);
            throw new GlobalValidationException("error on sending OTP email");
        }
//        return "loginSecret"; // Show the form to enter the 2FA secret
//        return ;
        return ResponseEntity.ok(new LoginResponse(HttpStatus.OK, email, "sent the OTP to email"));
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object auth(@ModelAttribute(value="secret") String secret, @ModelAttribute(value="email") String email, BindingResult result, Model model, HttpServletResponse response) {
    	LOG.debug("auth() HTML.Post");
        
    	if (userEnteredCorrect2FASecret(secret, email)) {
            addAuthority(TwoFactorAuthenticationFilter.ROLE_TWO_FACTOR_AUTHENTICATED);
            return "forward:/oauth/token"; // Continue with the OAuth flow
        }

        return ResponseEntity.badRequest().body(new LoginResponse(HttpStatus.BAD_REQUEST, email, "OTP is not matching")); // Show the form to enter the 2FA secret again
    }
    
    private boolean isAuthenticatedWithAuthority(String checkedAuthority){
  
    	return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(
                authority -> checkedAuthority.equals(authority.getAuthority())
    			);
    }
    
    private boolean addAuthority(String authority){
    	
    	Collection<SimpleGrantedAuthority> oldAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    	SimpleGrantedAuthority newAuthority = new SimpleGrantedAuthority(authority);
    	List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
    	updatedAuthorities.add(newAuthority);
    	updatedAuthorities.addAll(oldAuthorities);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        User principal;
        if (session != null) {
            principal = (User)session.getAttribute("Principal");
        } else {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        session.getAttribute("Authorization");

    	SecurityContextHolder.getContext().setAuthentication(
    	        new UsernamePasswordAuthenticationToken(
                        principal,
                        SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                        updatedAuthorities)
    	);
    		
    	return true;
    }
    
    private boolean userEnteredCorrect2FASecret(String secret, String email){
    	/* later on, we need to pass a temporary secret for each user and control it here */
    	/* this is just a temporary way to check things are working */
    	
    	String otp = String.valueOf(otpSvc.getOtp(email));
    	if(otp.length() > 2 && otp.equals(secret)){
    	    return true;
        }else {
    	    return false;
        }
    }
}
