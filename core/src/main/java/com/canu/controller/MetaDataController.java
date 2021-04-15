package com.canu.controller;

import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.services.AnnouncementService;
import com.canu.services.CanIService;
import com.canu.services.FAQService;
import com.canu.services.GuidelineService;
import com.canu.specifications.*;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/data")
@RequiredArgsConstructor
public class MetaDataController {

    private final SkillSetRepository skillSetRepo;

    private final PropertyRepository propertyRepo;

    private final CityRepository cityRepo;

    private final CountryRepository countryRepo;

    final private TagRepository tagRepo;

    final private CanIService canIService;

    final private FAQService faqSvc;

    final private AnnouncementService announceSvc;

    final private GuidelineService guideSvc;

    @GetMapping(value = "/services")
    public Object getServices(SkillSetFilter filter) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", skillSetRepo.findAll(filter)));

    }

    @PostMapping(value = "/update-services")
    public Object updateServices(@RequestBody List<SkillSetModel> list) {
        List<SkillSetModel> result = skillSetRepo.saveAll(list);
        return ResponseEntity.ok(CommonResponse.buildOkData("updated data", result));
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

    @GetMapping(value = "/get-country")
    public Object getNational() {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", countryRepo.findAll()));
    }

    @GetMapping(value = "/get-city")
    public Object getCity(@RequestParam("country") String country) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", cityRepo.findByCountryId(country)));
    }

    @GetMapping(value = "/get-keyword")
    public Object getKeyword() {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", tagRepo.findAll()));
    }

    @GetMapping(value = "/detail-list")
    public ResponseEntity getDetail(CanIFilter filter, Pageable p) {
        return canIService.GetCaniList(filter, p);
    }

    @GetMapping(value = "/FAQ/list")
    public ResponseEntity getFAQ(FAQFilter filter) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", faqSvc.getListFAQs(filter)));
    }

    @PostMapping(value = "/FAQ/initial")
    public ResponseEntity update(@RequestBody @Validated FAQModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", faqSvc.initialFAQ(request)));
    }

    @GetMapping(value = "/announce/list")
    public ResponseEntity getAnnounce(AnnouncementFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", announceSvc.getListAnnounces(filter, p)));
    }

    @PostMapping(value = "/announce/initial")
    public ResponseEntity update(@RequestBody @Validated AnnouncementModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", announceSvc.initialAnnounce(request)));
    }

    @GetMapping(value = "/guide/list")
    public ResponseEntity getAnnounce(GuidelineFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", guideSvc.getListGuideline(filter, p)));
    }

    @PostMapping(value = "/guide/initial")
    public ResponseEntity update(@RequestBody @Validated GuidelineModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", guideSvc.initialGuideline(request)));
    }
}
