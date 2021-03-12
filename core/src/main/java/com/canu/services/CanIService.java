package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.CanIModel;
import com.canu.model.CanUModel;
import com.canu.model.FileModel;
import com.canu.model.SkillSetModel;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import com.canu.repositories.SkillSetRepository;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CanIService {

    final private CanIRepository caIRepo;

    final private SkillSetRepository skillSetRepo;

    final private CanURepository canURepo;

    public ResponseEntity signUp(CanIModel request, String email) {

        List<SkillSetModel> skillSets = skillSetRepo.findAllById(request.getService());
        if(!Objects.equals(skillSets.size(), request.getService().size())){
            throw new GlobalValidationException("Some chosen services not existed");
        }

        CanUModel currentCanU = canURepo.findByEmail(email);
        if(currentCanU.getCanIModel() != null && request.getId() == null){
            throw new GlobalValidationException("CanI is created for this user.");
        }

        if(request.getId() != null && !currentCanU.getCanIModel().getId().equals(request.getId()) ){
            throw new GlobalValidationException("CanI not belong to current user");
        }

        request.setSkillSets(skillSets);
        request.setCanUModel(currentCanU);
        request.setEmail(email);

        CanIModel response = caIRepo.save(request);
        updateCanIResponse(response, currentCanU);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create CanI user", response));
    }

    public ResponseEntity getDetail(String email) {
        CanUModel currentCanU = canURepo.findByEmail(email);

        CanIModel cani = currentCanU.getCanIModel();
        if(cani == null){
            throw new GlobalValidationException("CanI is not created for this user.");
        }
        Hibernate.initialize(currentCanU.getFiles());
        updateCanIResponse(cani, currentCanU);

        return ResponseEntity.ok(CommonResponse.buildOkData("CanI detail", cani));
    }

    public void updateCanIResponse(CanIModel cani, CanUModel canu){
        Map<String, List<FileModel>> data = canu.getFiles().stream().collect(Collectors.groupingBy(FileModel::getDescription));
        cani.setEmail(canu.getEmail());
        cani.setService(cani.getSkillSets().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        cani.setFiles(data);
    }
}
