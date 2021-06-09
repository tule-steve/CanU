package com.canu.controller;

import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.services.*;
import com.canu.specifications.*;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/data")
@RequiredArgsConstructor
public class MetaDataController {

    private final SkillSetRepository skillSetRepo;

    private final MetadataRepository propertyRepo;

    private final CityRepository cityRepo;

    private final CountryRepository countryRepo;

    final private TagRepository tagRepo;

    final private CanIService canIService;

    final private FAQService faqSvc;

    final private AnnouncementService announceSvc;

    final private GuidelineService guideSvc;

    final private AdminService adminSvc;

    final private SupportRequestService supportSvc;

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
    public Object addOptions(@RequestBody List<MetadataModel> list) {
        adminSvc.saveProperties(list);
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

    @GetMapping(value = "/announce/{id}")
    public ResponseEntity getAnnounce(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", announceSvc.getById(id)));
    }

    @PostMapping(value = "/announce/initial")
    public ResponseEntity update(@RequestBody @Validated AnnouncementModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", announceSvc.initialAnnounce(request)));
    }

    @PostMapping(value = "/announce/delete")
    public ResponseEntity deleteAnnounce(@RequestBody Map<String, Long> request) {
        announceSvc.delete(request.get("id"));
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @GetMapping(value = "/guide/list")
    public ResponseEntity getAnnounce(GuidelineFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", guideSvc.getListGuideline(filter, p)));
    }

    @GetMapping(value = "/guide/{id}")
    public ResponseEntity getGuilde(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", guideSvc.getById(id)));
    }

    @PostMapping(value = "/guide/initial")
    public ResponseEntity update(@RequestBody @Validated GuidelineModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", guideSvc.initialGuideline(request)));
    }

    @PostMapping(value = "/guide/delete")
    public ResponseEntity deleteGuilde(@RequestBody Map<String, Long> request) {
        guideSvc.delete(request.get("id"));
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }

    @GetMapping(value = "/rating-criteria/list")
    public Object getRatingCriteria(PropertyFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", adminSvc.getRatingCriteria(filter, p)));
    }

    @GetMapping(value = "/support/list")
    public Object sendSupportRequest(SupportRequestFilter filter, Pageable p) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", supportSvc.getListSupportRequest(filter, p)));
    }

    @GetMapping(value = "/support/{id}")
    public Object getSupportRequest(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", supportSvc.getById(id)));
    }

    @PostMapping(value = "/support/initial")
    public Object sendSupportRequest(@RequestBody @Validated SupportRequestModel request) {
        return ResponseEntity.ok(CommonResponse.buildOkData("OK", supportSvc.upsertSupportRequest(request)));
    }

    @PostMapping(value = "/support/delete")
    public ResponseEntity deleteSupport(@RequestBody Map<String, Long> request) {
        supportSvc.delete(request.get("id"));
        return ResponseEntity.ok(CommonResponse.buildOkData("OK"));
    }



}
