package com.canu.services;

import com.canu.dto.requests.TemplateRequest;
import com.canu.dto.responses.Member;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.TemplateModel;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import com.canu.repositories.JobRepository;
import com.canu.repositories.TemplateRepository;
import com.canu.specifications.TemplateFilter;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final private CanURepository canURepo;

    final private CanIRepository caniRepo;

    final private CanIService caniSvc;

    final private EntityManager em;

    final private JobRepository jobRepo;

    final Configuration config;

    final TemplateRepository templateRepo;

    public Page<Member> getMembers(Pageable p, Long userId) {
        //        p.getSort()
        //        p.getPageNumber()
        StringBuilder sb = new StringBuilder();
        sb.append(
                "select u.id as userId, u.first_name as firstName, u.last_name as lastName, u.email as email, u.created_at as createdAt, ");
        sb.append("  u.cani_id as caniId ");
        sb.append(" from user u ");

        if (userId != null) {
            sb.append(" where u.id = " + userId);
        }

        if (p.getSort() != Sort.unsorted()) {
            sb.append(" order by ");
            StringJoiner joiner = new StringJoiner(", ");
            for (Sort.Order order : p.getSort()) {
                CharSequence orderCmd = order.getProperty() + " " + order.getDirection().toString();
                joiner.add(orderCmd);
            }
            sb.append(joiner.toString());
        }
        Query q = this.em.createNativeQuery(sb.toString(), "MemberMapping");
        this.em.getEntityManagerFactory().addNamedQuery("CanUModel.getMembership", q);
        Page<Member> memberList = canURepo.getMembership(p);
        memberList.forEach(r -> {
            if (r.getCaniId() != null) {
                r.setCani(caniRepo.findById(r.getCaniId()).orElse(null));
                r.setFavoriteCount(r.getCani().getFavoriteCanU().size());
                CanUModel canUModel = canURepo.findById(r.getUserId()).get();
                caniSvc.updateCanIResponse(r.getCani(), canUModel);
                updateJobInformation(canUModel, r);
            }

        });
        return memberList;
    }

    private void updateJobInformation(CanUModel canUModel, Member member) {
//        List<JobModel> jobs = jobRepo.findJobForCreationUser(canUModel.getId());
        List<JobModel> jobs = canUModel.getCreatedJob();
        member.setCreatedJob(jobs.size());
        member.setCanceledJob(jobs
                                      .stream()
                                      .filter(r -> JobModel.JobStatus.CANCEL.equals(r.getStatus()))
                                      .count());

        List<JobModel> job = canUModel.getPickedJob();
        Map<String, Integer> jobStatus = new HashMap<>();
        Map<JobModel.JobStatus, List<JobModel>> dividedJob = job.stream()
                                                                .collect(Collectors.groupingBy(JobModel::getStatus));
        dividedJob.forEach((k, v) -> jobStatus.put(k.toString(), v.size()));
        List<JobModel> completedJob = dividedJob.get(JobModel.JobStatus.COMPLETED);
        List<JobModel> processingJob = dividedJob.get(JobModel.JobStatus.PROCESSING);

        member.setProcessingJob(processingJob != null ? processingJob.size() : 0);
        member.setFinishedJob(completedJob != null ? completedJob.size() : 0);
    }

    public void setupTemplate(TemplateRequest template){
        ((StringTemplateLoader) config.getTemplateLoader()).putTemplate(template.getType().toString(), template.getTemplate());
//        ((StringTemplateLoader) config.getTemplateLoader()).putTemplate(template.getType().toTitleString(), template.getDescription());
        TemplateModel updatedTemplate = templateRepo.findFirstByType(template.getType());
        updatedTemplate.setTemplate(template.getTemplate());
        updatedTemplate.setTitle(template.getTitle());
        templateRepo.save(updatedTemplate);
    }

    public Slice<TemplateModel> getTemplate(TemplateFilter filter, Pageable p){
        return templateRepo.findAll(filter, p);
    }

}
