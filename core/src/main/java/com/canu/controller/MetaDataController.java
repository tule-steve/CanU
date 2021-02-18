package com.canu.controller;

import com.canu.model.MetadataPropertyModel;
import com.canu.model.SkillSetModel;
import com.canu.repositories.PropertyRepository;
import com.canu.repositories.SkillSetRepository;
import com.canu.specifications.SkillSetFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/data")
@RequiredArgsConstructor
public class MetaDataController {

    private final SkillSetRepository skillSetRepo;

    private final PropertyRepository propertyRepo;

    @GetMapping(value = "/services")
    public Object getServices(SkillSetFilter filter) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", skillSetRepo.findAll(filter)));

    }

    @PostMapping(value = "/init-skillset")
    public Object initSkillSet(@RequestBody List<SkillSetModel> list) {
        skillSetRepo.saveAll(list);
        return ResponseEntity.ok(CommonResponse.buildOkData("initialize data"));
    }

    @PostMapping(value = "/add-properties")
    public Object addOptions(@RequestBody List<MetadataPropertyModel> list) {
        propertyRepo.saveAll(list);
        return ResponseEntity.ok(CommonResponse.buildOkData("added " + list.size() + " option"));
    }

    @PostMapping(value = "/get-properties")
    public Object getOptions(@RequestBody List<String> list) {

        return ResponseEntity.ok(CommonResponse.buildOkData("OK", propertyRepo.findDistinctByKeyIn(list)));
    }
}
