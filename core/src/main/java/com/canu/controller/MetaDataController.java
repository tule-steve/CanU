package com.canu.controller;

import com.canu.model.MetadataPropertyModel;
import com.canu.model.SkillSetModel;
import com.canu.repositories.*;
import com.canu.services.CanIService;
import com.canu.specifications.CanIFilter;
import com.canu.specifications.SkillSetFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public Object getNational(){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK",countryRepo.findAll()));
    }

    @GetMapping(value = "/get-city")
    public Object getCity(@RequestParam("country") String country){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK",cityRepo.findByCountryId(country)));
    }

    @GetMapping(value = "/get-keyword")
    public Object getKeyword(){
        return ResponseEntity.ok(CommonResponse.buildOkData("OK",tagRepo.findAll()));
    }

    @GetMapping(value = "/detail-list")
    public ResponseEntity getDetail(CanIFilter filter, Pageable p) {
        return canIService.GetCaniList(filter, p);
    }

}
