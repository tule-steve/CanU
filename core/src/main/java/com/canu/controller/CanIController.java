package com.canu.controller;

import com.canu.model.CanIModel;
import com.canu.services.CanIService;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        canIService.signUp(request);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create account"));
    }

    @PostMapping(value = "/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String uploadDir = System.getProperty("user.dir") + "/image";
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

        return ResponseEntity.ok(CommonResponse.buildOkData("update load file"));
    }

}
