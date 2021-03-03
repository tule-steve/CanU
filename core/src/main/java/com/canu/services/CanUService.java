package com.canu.services;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.ResetPassWordRequest;
import com.canu.dto.responses.Token;
import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.model.FileModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.FileRepository;
import com.canu.security.config.TokenProvider;
import com.common.dtos.CommonResponse;
import com.common.mail.MailService;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class CanUService {

    final private BCryptPasswordEncoder encoder;

    final private CanURepository canURepo;

    final private TokenProvider tokenProvider;

    final private MailService mailSvc;

    final private FileRepository fileRepo;

    private static final Logger logger = LoggerFactory.getLogger(CanUService.class);

    final private EntityManager em;

    @Value("${app.baseUrl}")
    private String domainLink;

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

    public ResponseEntity getProfile() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", uUser));
    }

    public ResponseEntity changePassword(ChangePassWordRequest request) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            CanUModel uUser = canURepo.findByEmail(user.getUsername());
            if (uUser.getPassword() != null) {
                if (!encoder.matches(request.getOldPassword(), uUser.getPassword())) {
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

    //    public ResponseEntity uploadCanIFile(MultipartFile[] gpdkkdFile, MultipartFile[] cerFile) throws IOException{
    //        Map<String, Object> response = new HashMap<>();
    //        if(gpdkkdFile.length > 0){
    //            List<FileModel> gpdkkdModel = uploadImage(gpdkkdFile, "/gpdkkd");
    //            response.put("gpdkkd", gpdkkdModel);
    //        }
    //
    //        if(cerFile.length > 0){
    //            List<FileModel> cerModel = uploadImage(cerFile, "/certificate");
    //            response.put("certificate", cerModel);
    //        }
    //
    //        return ResponseEntity.ok(CommonResponse.buildOkData("Upload file", response));
    //    }

    public ResponseEntity updateFileData(StandardMultipartHttpServletRequest request) throws IOException {
        if (request.getParameterMap().get("deleted") != null) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CanUModel uUser = canURepo.findByEmail(user.getUsername());
            String[] deletedData = request.getParameterMap().get("deleted")[0].split(",");
            List<Long> ids = new ArrayList<>();
            for (String id : deletedData) {
                ids.add(Long.parseLong(id.trim()));
            }
            fileRepo.deleteFilesWithIdsAndUser(ids, uUser.getId());
        }

        return uploadFile(request.getMultiFileMap());
    }

    public ResponseEntity uploadFile(MultiValueMap<String, MultipartFile> fileMap) throws IOException {
        Map<String, Object> response = new HashMap<>();
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        for (Map.Entry<String, List<MultipartFile>> entry : fileMap.entrySet()) {
            String parentFolder = entry.getKey();
            List<MultipartFile> multipartFiles = entry.getValue();
            List<FileModel> cerModel = uploadImage(multipartFiles, parentFolder, uUser);
            response.put(parentFolder, cerModel);
            if ("canu_avatar".equalsIgnoreCase(parentFolder)) {
                uUser.setAvatar(cerModel.get(0).getUrl());
            } else if ("cani_avatar".equalsIgnoreCase(parentFolder) && uUser.getCanIModel() != null){
                uUser.getCanIModel().setAvatar(cerModel.get(0).getUrl());
            }

        }

        return ResponseEntity.ok(CommonResponse.buildOkData("Upload file", response));
    }

    public List<FileModel> uploadImage(List<MultipartFile> multipartFiles, String parentFolder, CanUModel uUser) throws
                                                                                                                 IOException {
        String url = "/images/static/" + uUser.getId().toString() + "/" + parentFolder;
        String uploadDir = System.getProperty("user.dir") + url;
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        List<FileModel> fileList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            FileModel file = new FileModel();
            file.setDescription(parentFolder);
            file.setFileName(fileName);
            file.setOwner(uUser);
            file.setUrl(domainLink + url + "/" + file.getFileName());
            fileList.add(fileRepo.save(file));

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName).normalize();
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }

        return fileList;
    }

    //    public List<FileModel> uploadImage(MultipartFile[] multipartFiles, String parentFolder) throws IOException {
    //        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //        CanUModel uUser = canURepo.findByEmail(user.getUsername());
    //        String url = "/image/static/" + uUser.getId().toString() + parentFolder;
    //        String uploadDir = System.getProperty("user.dir") + url;
    //        Path uploadPath = Paths.get(uploadDir);
    //        if (!Files.exists(uploadPath)) {
    //            Files.createDirectories(uploadPath);
    //        }
    //
    //        List<FileModel> fileList = new ArrayList<>();
    //
    //        for (MultipartFile multipartFile : multipartFiles) {
    //            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    //
    //            FileModel file = new FileModel();
    //            file.setDescription("test");
    //            file.setFileName(fileName);
    //            file.setOwner(uUser);
    //            file.setUrl(url + "/" + file.getFileName());
    //            fileList.add(fileRepo.save(file));
    //
    //            try (InputStream inputStream = multipartFile.getInputStream()) {
    //                Path filePath = uploadPath.resolve(fileName);
    //                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    //            } catch (IOException ioe) {
    //                throw new IOException("Could not save image file: " + fileName, ioe);
    //            }
    //        }
    //
    //        return fileList;
    //    }

    public void sendVerificationEmail(String email) {
        CanUModel currUser = canURepo.findByEmail(email);
        if (currUser == null) {
            throw new GlobalValidationException("User is not exist or deleted");
        }
        String token = currUser.getToken();
        if (token == null) {
            token = UUID.randomUUID().toString();
            currUser.setToken(token);
        }
        canURepo.save(currUser);
        mailSvc.sendEmailVerification(email, token);
    }

    public void confirmEmail(String token) {
        CanUModel currUser = canURepo.findByToken(token)
                                     .orElseThrow(() -> new GlobalValidationException("User is not existed"));
        currUser.setToken(null);
        currUser.setActivated(true);
        canURepo.save(currUser);
    }

    public void sendForgetPassword(String email) {
        CanUModel currUser = canURepo.findByEmail(email);
        if (currUser == null) {
            throw new GlobalValidationException("User is not exist or deleted");
        }
        String token = currUser.getToken();
        if (token == null) {
            token = UUID.randomUUID().toString();
            currUser.setToken(token);
        }

        canURepo.save(currUser);
        mailSvc.sendResetPasswordMail(email,
                                      token,
                                      Optional.ofNullable(currUser.getFirstName()).orElse("") +
                                      Optional.ofNullable(currUser.getLastName()).orElse(""));
    }
    public void resetPassword(ResetPassWordRequest request){
        CanUModel currUser = canURepo.findByToken(request.getToken())
                                     .orElseThrow(() -> new GlobalValidationException("Token invalid"));

        String cryptNewPass = encoder.encode(request.getNewPassword());
        currUser.setPassword(cryptNewPass);
        currUser.setToken(null);
    }
}
