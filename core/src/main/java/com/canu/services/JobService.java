package com.canu.services;

import com.canu.dto.requests.UpdateJobRequest;
import com.canu.dto.responses.JobDto;
import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.SkillSetModel;
import com.canu.model.TagModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.JobRepository;
import com.canu.repositories.SkillSetRepository;
import com.canu.repositories.TagRepository;
import com.canu.specifications.JobFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    final private JobRepository jobRepo;

    final private CanURepository canURepo;

    final private TagRepository tagRepo;

    final private SkillSetRepository skillSetRepo;

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    public JobModel postJob(JobModel request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        if (user == null) {
            logger.error("User {} not found", user.getUsername());
            throw new GlobalValidationException("User " + user.getUsername() + " was not found in the database");
        }

        if (request.getCreationUser() == null) {
            request.setCreationUser(uUser);
        }

        List<SkillSetModel> skillSets = skillSetRepo.findAllById(request.getService());
        if (!Objects.equals(skillSets.size(), request.getService().size())) {
            throw new GlobalValidationException("Some chosen services not existed");
        }
        request.setSkillSets(skillSets);

        List<TagModel> tagEntities = tagRepo.findAllByTagIn(request.getKeyword());
        request.getTags().addAll(tagEntities);
        if (!Objects.equals(tagEntities.size(), request.getKeyword().size())) {
            TagModel newTag;
            for (String keywork : request.getKeyword()) {
                boolean isNeedAdd = true;
                for (TagModel tagEntity : tagEntities) {
                    if (tagEntity.getTag().equalsIgnoreCase(keywork)) {
                        isNeedAdd = false;
                    }
                }

                if (isNeedAdd) {
                    newTag = new TagModel();
                    newTag.setTag(keywork);
                    request.getTags().add(newTag);
                }
            }
        }
        return jobRepo.save(request);
    }

    public JobDto getJobDetail(Long id) {
        JobModel job = jobRepo.findById(id)
                              .orElseThrow(() -> new GlobalValidationException("Cannot find job with id: " + id));
        return new JobDto(job);
    }

    private void setAdditionData(JobModel job) {
        job.setKeyword(job.getTags().stream().map(r -> r.getTag()).collect(Collectors.toSet()));
        job.setService(job.getSkillSets().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        job.setPickupCanI(job.getCanus().stream().map(r -> r.getId()).collect(Collectors.toSet()));
    }

    public Page<JobDto> showJobList(JobFilter filter, Pageable p) {
        Slice<JobModel> jobEntities = jobRepo.findAll(filter, p);
        List<JobDto> jobList = jobEntities.stream().map(r -> new JobDto(r)).collect(Collectors.toList());
        Page<JobDto> result = new PageImpl<>(jobList, p, jobRepo.count());
        return result;
    }

    public void pickUpJob(Long jobId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(jobId).orElseThrow(() -> new GlobalValidationException("Cannot find the job"));
        if (!job.getCanus().stream().anyMatch(r -> r.getId() == uUser.getId())) {
            job.getCanus().add(uUser);
            jobRepo.save(job);
        }
    }

    public void cancelJob(Long jobId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(jobId)
                              .orElseThrow(() -> new GlobalValidationException("Cannot find the job"));
        uUser.validatePrivilege(job.getCreationUser());
        jobRepo.delete(job);
    }

    public void updateJob(UpdateJobRequest request){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(request.getId())
                              .orElseThrow(() -> new GlobalValidationException("Cannot find the job"));
        uUser.validatePrivilege(job.getCreationUser());

        request.updateJobEntity(job);
        jobRepo.save(job);
    }

}
