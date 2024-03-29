package com.canu.services;

import com.canu.dto.responses.CanIServiceDto;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import com.canu.repositories.SkillSetRepository;
import com.canu.specifications.CanIFilter;
import com.common.dtos.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CanIService {

    final private CanIRepository caniRepo;

    final private SkillSetRepository skillSetRepo;

    final private CanURepository canURepo;

    final private EntityManager em;

    public ResponseEntity signUp(CanIModel request, String email) {
        request.setStatus(CanIModel.Status.ACTIVE);
        CanUModel currentCanU = canURepo.findByEmail(email);
        if (currentCanU.isRegisterCanI() && request.getId() == null) {
            throw new GlobalValidationException("CanI is created for this user");
        }

        if (request.getId() != null) {
            CanIModel data = caniRepo.findById(request.getId())
                                     .orElseThrow(() -> new GlobalValidationException("CanI not exist"));
            request.setAvatar(currentCanU.getAvatar());
            if(data.getPhone() != null && data.getPhone().equalsIgnoreCase(request.getPhone()) && data.getPhoneVerified()){
                request.setPhoneVerified(true);
            }
            request.setRatingCount(data.getRatingCount());
            request.setRating(data.getRating());
            request.setTotalRating(data.getTotalRating());
//            request.setSentSmsCodeAt(data.getSentSmsCodeAt());
        } else {
            String avatar = currentCanU.getFiles()
                                       .stream()
                                       .filter(r -> "cani_avatar".equalsIgnoreCase(r.getFileName()))
                                       .map(r -> r.getFileName())
                                       .findFirst()
                                       .orElse(null);
            request.setAvatar(avatar);
        }

        List<SkillSetModel> skillSets = skillSetRepo.findAllById(request.getService());
        if (!Objects.equals(skillSets.size(), request.getService().size())) {
            throw new GlobalValidationException("Some selected services not existed");
        }

        if (request.getId() != null && !currentCanU.getCanIModel().getId().equals(request.getId())) {
            throw new GlobalValidationException("CanI not belong to current user");
        }

        request.setSkillSets(skillSets);
        request.setCanUModel(currentCanU);
        request.setEmail(email);
        request.setName(currentCanU.getName());

        CanIModel response = caniRepo.save(request);
        updateCanIResponse(response, currentCanU);
        return ResponseEntity.ok(CommonResponse.buildOkData("Create CanI user", response));
    }

    public ResponseEntity getDetail(String email) {
        CanUModel currentCanU = canURepo.findByEmail(email);

        if (currentCanU.getCanIModel() == null) {
            throw new GlobalValidationException("CanI is not created for current user");
        }

        CanIModel cani = currentCanU.getCanIModel();

        Hibernate.initialize(currentCanU.getFiles());
        updateCanIResponse(cani, currentCanU);

        return ResponseEntity.ok(CommonResponse.buildOkData("CanI detail", cani));
    }

    public void updateCanIResponse(CanIModel cani, CanUModel canu) {
        Map<String, List<FileModel>> data = canu.getFiles()
                                                .stream()
                                                .collect(Collectors.groupingBy(FileModel::getDescription));
        cani.setEmail(canu.getEmail());
        cani.setService(cani.getSkillSets().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        cani.setFiles(data);
    }

    public ResponseEntity GetCaniList(CanIFilter filter, Pageable p) {
        Page<CanIModel> canIModels = caniRepo.findAll(filter, p);
        decorateCanI(canIModels);
        return ResponseEntity.ok(CommonResponse.buildOkData("CanI Lis", canIModels));
    }

    public void decorateCanI(Iterable<CanIModel> canIModels) {
        for (CanIModel cani : canIModels) {
            List<JobModel> job = cani.getCanUModel().getPickedJob();
            Map<String, Integer> jobStatus = new HashMap<>();
            Map<JobModel.JobStatus, List<JobModel>> dividedJob = job.stream()
                                                                    .collect(Collectors.groupingBy(JobModel::getStatus));
            jobStatus.put("Total", job.size());
            dividedJob.forEach((k, v) -> jobStatus.put(k.toString(), v.size()));

            updateCanIResponse(cani, cani.getCanUModel());
            em.detach(cani);
            cani.setId(cani.getCanUModel().getId());
            cani.setJobStatus(jobStatus);
        }

    }

    public List<CanIServiceDto> countCanIByService(){
        List<Object[]> data = caniRepo.countCanIByService();
        List<CanIServiceDto> result = new ArrayList<>();
        for(Object[] row : data){
            CanIServiceDto dto = new CanIServiceDto((Long)row[0], row[1].toString(), (Long)row[2]);
            result.add(dto);
        }
        return result;
    }

    public void save(CanIModel cani){
        caniRepo.save(cani);
    }
}
