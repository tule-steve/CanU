package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.CanIModel;
import com.canu.model.CanUModel;
import com.canu.model.SkillSetModel;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import com.canu.repositories.SkillSetRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CanIService {

    final private CanIRepository caIRepo;

    final private SkillSetRepository skillSetRepo;

    final private CanURepository canURepo;

    public void signUp(CanIModel request, String email) {

        List<SkillSetModel> skillSets = skillSetRepo.findAllById(request.getService());
        if(!Objects.equals(skillSets.size(), request.getService().size())){
            throw new GlobalValidationException("Some chosen services not existed");
        }

        CanUModel currentCanU = canURepo.findByEmail(email);
        if(currentCanU.getCanIModel() != null){
            throw new GlobalValidationException("CanI is created for this user.");
        }

        request.setSkillSets(skillSets);
        request.setCanUModel(currentCanU);
        caIRepo.save(request);
    }

    public CanIModel getDetail(String email) {
        CanUModel currentCanU = canURepo.findByEmail(email);
        CanIModel cani = currentCanU.getCanIModel();
        if(cani == null){
            throw new GlobalValidationException("CanI is not created for this user.");
        }

        cani.setEmail(email);
        cani.setService(cani.getSkillSets().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        return currentCanU.getCanIModel();
    }
}
