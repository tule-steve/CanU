package com.canu.controller;

import com.canu.dto.requests.LoginRequest;
import com.canu.dto.responses.Token;
import com.canu.security.config.TokenProvider;
import com.canu.services.CanUService;
import com.canu.services.SocialAuthService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final SocialAuthService authService;

    private final TokenProvider tokenProvider;


//
//    @Qualifier("jwtTokenServices")
//    @Autowired
//    AuthorizationServerTokenServices tokenServices;

    private final AuthenticationManager authenticationManager;

    private final CanUService canuSvc;
    
//    @GetMapping(value = "/api/home")
//    public Object home() {
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpSession session = attr.getRequest().getSession(false);
//        Principal principal;
//        if (session != null) {
//            principal = (Principal)session.getAttribute("Principal");
//            session.removeAttribute("Principal");
//        } else {
//            principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        }
//        UserDto user = authService.extractUserFromAuthInfo(principal);
//        String token = tokenProvider.createToken(user.getEmail());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Location", "/member/uploadImage");
//        return ResponseEntity.ok(new Token(token, 86400L));
//    }

    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if(!canuSvc.isUserActiated(loginRequest.getEmail())){
            return ResponseEntity.ok(Collections.emptyMap());
        }

        String token = tokenProvider.createToken(loginRequest.getEmail());
        return ResponseEntity.ok(new Token(token, 86400L));
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

//    @GetMapping(value = "/api/image/{userId}/{fileName}")
//    public void getImage(@PathVariable("userId") String userId, @PathVariable("fileName") String fileName, HttpServletResponse response) throws
//                                                                                                                                         IOException {
//        String fileUrl = System.getProperty("user.dir") + "/image/" + userId + "/" + fileName;
//        File initialFile = new File(fileUrl);
//        InputStream targetStream = new FileInputStream(initialFile);
//        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//        IOUtils.copy(targetStream, response.getOutputStream());
//    }
}
