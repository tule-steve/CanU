package com.canu.services;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.responses.Token;
import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.security.config.ExtOAuth2ClientAuthenticationProcessingFilter;
import com.canu.security.config.TokenProvider;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Transactional
@RequiredArgsConstructor
public class CanUService {

    final private BCryptPasswordEncoder encoder;

    final private CanURepository canURepo;

    final private TokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(CanUService.class);

    public ResponseEntity signUp(CanUSignUpRequest request) {

        if (canURepo.findByEmail(request.getEmail()) != null) {
            throw new GlobalValidationException("Email is used.");
        }

        String cryptPass = encoder.encode(request.getPassword());
        CanUModel data = new CanUModel();
        data.setEmail(request.getEmail());
        data.setPassword(cryptPass);
        canURepo.save(data);

        return ResponseEntity.ok(new Token(tokenProvider.createToken(request.getEmail()), 86400L));
    }

    public ResponseEntity changePassword(ChangePassWordRequest request) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            CanUModel uUser = canURepo.findByEmail(user.getUsername());
            if (uUser.getPassword() != null) {
                String cryptOldPass = encoder.encode(request.getOldPassword());
                if (!cryptOldPass.equals(uUser.getPassword())) {
                    throw new GlobalValidationException("old password not correctly");
                }
            }

            String cryptNewPass = encoder.encode(request.getNewPassword());
            uUser.setPassword(cryptNewPass);
            canURepo.save(uUser);
            return ResponseEntity.ok(CommonResponse.buildOkData("changed password"));
        } catch (GlobalValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("error on change password", ex);
            throw new GlobalValidationException("Cannot retries the current user");
        }

    }

    public ResponseEntity uploadImage(MultipartFile multipartFile) throws IOException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        String uploadDir = System.getProperty("user.dir") + "/image/" + uUser.getId().toString();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }

        return ResponseEntity.ok(CommonResponse.buildOkData("updated","/image/" + uUser.getId().toString() + "/" + fileName));
    }
}
