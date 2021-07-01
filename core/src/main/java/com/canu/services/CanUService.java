package com.canu.services;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.ChangePassWordRequest;
import com.canu.dto.requests.ResetPassWordRequest;
import com.canu.dto.responses.Member;
import com.canu.dto.responses.NotificationListResponse;
import com.canu.dto.responses.Token;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.security.config.TokenProvider;
import com.common.dtos.CommonResponse;
import com.common.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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

    final private CanIService canIService;

    final private EntityManager em;

    final private NotificationDetailRepository notiDetailRepo;

    final private NotificationRepository notiRepo;

    final private PropertyRepository propertyRepo;

    final private UserPropertyRepository userproRepo;

    final private AmazonS3Service s3Svc;

    @Value("${app.baseUrl}")
    private String domainLink;

    public ResponseEntity signUp(CanUSignUpRequest request) {

        if (canURepo.findByEmail(request.getEmail()) != null) {
            throw new GlobalValidationException("Email is used");
        }

        String cryptPass = encoder.encode(request.getPassword());
        CanUModel data = new CanUModel();
        data.setEmail(request.getEmail());
        data.setPassword(cryptPass);
        data.setFirstName(request.getFirstName());
        data.setLastName(request.getLastName());
        canURepo.save(data);

        return ResponseEntity.ok(new Token(tokenProvider.createToken(request.getEmail()), 86400L));
    }

    public ResponseEntity getProfile() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", uUser));
    }

    public ResponseEntity updateProfile(CanUModel request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        if (!uUser.getId().equals(request.getId())) {
            throw new GlobalValidationException("do not have privilege for this action");
        }
        updateCanU(request, uUser);
        if(uUser.getCanIModel() != null){
            uUser.getCanIModel().setName(uUser.getName());
            canIService.save(uUser.getCanIModel());
        }
        uUser = canURepo.save(uUser);
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", uUser));
    }

    public void updateCanU(CanUModel source, CanUModel dest) {
        dest.setFirstName(source.getFirstName());
        dest.setLastName(source.getLastName());
        dest.setCity(source.getCity());
        dest.setAddress(source.getAddress());
        dest.setPhone(source.getPhone());
        dest.setNation(source.getNation());
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
            CanUModel uUser;
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
                UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                uUser = canURepo.findByEmail(user.getUsername());
            } else {
                uUser = canURepo.findByEmail("admin@gmail.com");
            }

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
        CanUModel uUser;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            uUser = canURepo.findByEmail(user.getUsername());
        } else {
            uUser = canURepo.findByEmail("admin@gmail.com");
        }
        for (Map.Entry<String, List<MultipartFile>> entry : fileMap.entrySet()) {
            String parentFolder = entry.getKey();
            List<MultipartFile> multipartFiles = entry.getValue();
            List<FileModel> cerModel = uploadImage(multipartFiles, parentFolder, uUser);
            response.put(parentFolder, cerModel);
            if ("canu_avatar".equalsIgnoreCase(parentFolder)) {
                uUser.setAvatar(cerModel.get(0).getUrl());
                if(uUser.getCanIModel() != null) {
                    uUser.getCanIModel().setAvatar(cerModel.get(0).getUrl());
                }
            }
//            else if ("cani_avatar".equalsIgnoreCase(parentFolder) && uUser.isRegisterCanI()) {
//                uUser.getCanIModel().setAvatar(cerModel.get(0).getUrl());
//            }

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
                                     .orElseThrow(() -> new GlobalValidationException("User is not exist or deleted"));
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

    public void resetPassword(ResetPassWordRequest request) {
        CanUModel currUser = canURepo.findByToken(request.getToken())
                                     .orElseThrow(() -> new GlobalValidationException("Token is expired. Please request again"));

        String cryptNewPass = encoder.encode(request.getNewPassword());
        currUser.setPassword(cryptNewPass);
        currUser.setToken(null);
    }

    public ResponseEntity addFavoriteCani(Long userId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        if (userId.equals(uUser.getId())) {
            throw new GlobalValidationException("Cannot add yourself to favorite list");
        }

        CanUModel canIUser = canURepo.findById(userId)
                                     .orElseThrow(() -> new GlobalValidationException("User is not exist or deleted"));

        if (!canIUser.isRegisterCanI()) {
            throw new GlobalValidationException("User is not CanI user");
        }

        Map<String, Boolean> data = new HashMap<>();
        data.put("isFavorite", true);

        if (uUser.getFavoriteCanIs().contains(canIUser.getCanIModel())) {
            uUser.getFavoriteCanIs().remove(canIUser.getCanIModel());
            canURepo.save(uUser);
            data.put("isFavorite", false);
            return ResponseEntity.ok(CommonResponse.buildOkData("Removed", data));
        } else {
            uUser.getFavoriteCanIs().add(canIUser.getCanIModel());
            canURepo.save(uUser);

        }

        return ResponseEntity.ok(CommonResponse.buildOkData("Added", data));
    }

    public Object getFavoriteList() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        canIService.decorateCanI(uUser.getFavoriteCanIs());
        em.detach(uUser);
        return uUser.getFavoriteCanIs();
    }

    public void updateFavoriteFlag(Iterable<Member> members) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        for (Member member : members) {
            if (uUser.getFavoriteCanIs().contains(member.getCani())) {
                member.setIsFavorite(true);
            }
        }
    }

    public Object getNotification(boolean isCanu, Pageable p) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        List<NotificationModel> result = notiRepo.findByOwnerAndIsCanu(uUser, isCanu, p);
        Long unreadCount = notiRepo.countByIsReadFalseAndOwnerAndIsCanu(uUser, isCanu);
        NotificationListResponse response = new NotificationListResponse(result, unreadCount);

        return response;
    }

    public void markNotificationRead(NotificationModel noti){
        notiRepo.markReadNotification(noti.getUserId(), noti.getDetailId());
    }


    public Object getNotificationDetail(Long id) {
        return notiDetailRepo.findById(id).orElseThrow(() -> new GlobalValidationException("Cannot find the notification"));
    }

    @Transactional(readOnly = true)
    public Object getUserRating(String locale) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        Set<PropertyModel> criteriaRatings = propertyRepo.getRatingCriteria(uUser.getId(), locale);
        criteriaRatings.addAll(propertyRepo.findAllByTypeAndLocale(locale));
        criteriaRatings.forEach(r -> r.updateValue(uUser.getId(), locale));

        return criteriaRatings;
    }

    public void updateUserRating(List<PropertyModel> request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        request.forEach(r -> {
            String[] ids = r.getKey().split(":");
            UserPropertyModel model = new UserPropertyModel();
            PropertyModel propertyModel = new PropertyModel();
            propertyModel.setId(Long.parseLong(ids[0]));
            model.setProperty(propertyModel);
            model.setUser(uUser);
            model.setRating(r.getValue());
            if(ids.length > 1){
                model.setId(Long.parseLong(ids[1]));
            }
            userproRepo.save(model);
        });
    }

    public ResponseEntity uploadPublicFileList(MultiValueMap<String, MultipartFile> fileMap) throws IOException {
        Map<String, Object> response = new HashMap<>();

        for (Map.Entry<String, List<MultipartFile>> entry : fileMap.entrySet()) {
            String parentFolder = entry.getKey();
            List<MultipartFile> multipartFiles = entry.getValue();
            List<FileModel> cerModel = updatePublicFile(multipartFiles, parentFolder);
            response.put(parentFolder, cerModel);
        }
        return ResponseEntity.ok(CommonResponse.buildOkData("Upload file", response));
    }


    public List<FileModel> updatePublicFile(List<MultipartFile> multipartFiles, String parentFolder) throws
                                                                                                                 IOException {
        String url = "/images/static/public/" + parentFolder;
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


}
