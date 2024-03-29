package com.canu.services;

import com.canu.dto.requests.LockUserRequest;
import com.canu.dto.requests.TemplateRequest;
import com.canu.dto.responses.Dashboard;
import com.canu.dto.responses.Member;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.specifications.PropertyFilter;
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

    final private JobService jobSvc;

    final private EntityManager em;

    final private JobRepository jobRepo;

    private final PropertyRepository propertyRepo;

    final Configuration config;

    final TemplateRepository templateRepo;

    final TemplateMultiLangRepository templateMultiRepo;

    private final MetadataRepository metadataRepo;

    final private NotificationRepository notiRepo;

    final private NotificationDetailRepository notiDetailRepo;

    final private ReportRepository reportRepo;

    public Page<Member> getMembers(Pageable p, Long userId) {
        //        p.getSort()
        //        p.getPageNumber()
        StringBuilder sb = new StringBuilder();
        sb.append(
                "select u.id as userId, u.first_name as firstName, u.last_name as lastName, u.email as email, u.created_at as createdAt,u.activated as isActivated, u.is_blocked as isBlocked, ");
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
            CanUModel canUModel = canURepo.findById(r.getUserId()).get();
            updateJobInformation(canUModel, r);
            r.setCCash(canUModel.getCCash());
            r.setCPoint(canUModel.getCPoint());
            if (r.getCaniId() != null) {
                r.setCani(caniRepo.findById(r.getCaniId()).orElse(null));
                r.setFavoriteCount(r.getCani().getFavoriteCanU().size());
                caniSvc.updateCanIResponse(r.getCani(), canUModel);
            }

        });
        return memberList;
    }

    private void updateJobInformation(CanUModel canUModel, Member member) {
        //        List<JobModel> jobs = jobRepo.findJobForCreationUser(canUModel.getId());

        //update for canu
        List<JobModel> canuJobs = canUModel.getCreatedJob();
        member.setCreatedJob(canuJobs.size());
        Map<String, Integer> jobStatus = new HashMap<>();

        Map<JobModel.JobStatus, List<JobModel>> dividedJob = canuJobs.stream()
                                                                     .collect(Collectors.groupingBy(JobModel::getStatus));
        dividedJob.forEach((k, v) -> jobStatus.put(k.toString(), v.size()));
        List<JobModel> completedJob = dividedJob.get(JobModel.JobStatus.COMPLETED);
        List<JobModel> processingJob = dividedJob.get(JobModel.JobStatus.PROCESSING);
        member.setCanuProcessingJob(processingJob != null ? processingJob.size() : 0);
        member.setCanuFinishedJob(completedJob != null ? completedJob.size() : 0);

        member.setCanceledJob(canuJobs
                                      .stream()
                                      .filter(r -> JobModel.JobStatus.CANCEL.equals(r.getStatus()))
                                      .count());

        List<JobModel> caniJobs = canUModel.getPickedJob();
        dividedJob = caniJobs.stream()
                             .collect(Collectors.groupingBy(JobModel::getStatus));
        dividedJob.forEach((k, v) -> jobStatus.put(k.toString(), v.size()));
        completedJob = dividedJob.get(JobModel.JobStatus.COMPLETED);
        processingJob = dividedJob.get(JobModel.JobStatus.PROCESSING);

        member.setProcessingJob(processingJob != null ? processingJob.size() : 0);
        member.setFinishedJob(completedJob != null ? completedJob.size() : 0);
        member.setApplyingJob(canUModel.getJobs().size());
    }

    public void setupTemplate(TemplateRequest template) {
        ((StringTemplateLoader) config.getTemplateLoader()).putTemplate(template.getType().toString(),
                                                                        template.getTemplate());
        ((StringTemplateLoader) config.getTemplateLoader()).putTemplate(template.getType().toTitleString(),
                                                                        template.getDescription());
        TemplateModel updatedTemplate = templateRepo.findFirstByType(template.getType());
        if (CountryModel.Locale.en.equals(template.getLocale())) {
            updatedTemplate.setTemplate(template.getTemplate());
            updatedTemplate.setTitle(template.getTitle());
            updatedTemplate.setDescription(template.getDescription());
            templateRepo.save(updatedTemplate);
        } else {
            TemplateMultiLangModel templateMultiLang = updatedTemplate.getMultiLang()
                                                             .stream()
                                                             .filter(r -> r.getLocale().equals(template.getLocale()))
                                                             .findFirst()
                                                             .orElseThrow(() -> new GlobalValidationException(
                                                                     "this language is not supported"));
            templateMultiLang.setTemplate(template.getTemplate());
            templateMultiLang.setTitle(template.getTitle());
            templateMultiLang.setDescription(template.getDescription());
            templateMultiRepo.save(templateMultiLang);
        }
    }

    public Slice<TemplateModel> getTemplate(TemplateFilter filter, Pageable p) {
        return templateRepo.findAll(filter, p);
    }

    public void saveProperties(List<MetadataModel> models) {
        metadataRepo.saveAll(models);
    }

    public Object getRatingCriteria(PropertyFilter filter, Pageable p) {
        filter.setType(PropertyModel.Type.RATING_CRITERIA);
        return propertyRepo.findAll(filter, p);
    }

    public Object initialRatingCriteria(PropertyModel entity) {
        PropertyModel model = entity;
        entity.setType(PropertyModel.Type.RATING_CRITERIA);
        PropertyModel existingProperty = null;
        if (entity.getId() != null) {
            existingProperty = propertyRepo.findById(entity.getId())
                                           .orElseThrow(() -> new GlobalValidationException(
                                                   "The Rating Criteria is not exist"));
        }

        //        if (existingProperty == null) {
        //            existingProperty = propertyRepo.findFirstByKeyAndType(entity.getKey(), PropertyModel.Type.RATING_CRITERIA);
        //        }
        if (existingProperty != null) {
            model = existingProperty;
            model.setKey(entity.getKey());
            model.setLocale(entity.getLocale());
            if (CountryModel.Locale.en.equals(entity.getLocale())) {
                model.setProperty(entity.getProperty());
            }
        }
        model.getMultiLanguage().put(entity.getLocale().toString(), entity.getProperty());
        return propertyRepo.save(model);
    }

    public void deleteRatingCriteria(Long propertyId) {
        PropertyModel propertyModel = propertyRepo.findFirstByTypeAndId(PropertyModel.Type.RATING_CRITERIA, propertyId)
                                                  .orElseThrow(() -> new GlobalValidationException(
                                                          "cannot find the rating criteria"));
        propertyRepo.delete(propertyModel);
    }

    public Object upsertExchangeRate(List<PropertyModel> property) {
        property.stream().forEach(r -> r.setType(PropertyModel.Type.POINT_EXCHANGE));
        return propertyRepo.saveAll(property);

    }

    public Object getExchangeRate(PropertyFilter filter, Pageable p) {
        filter.setType(PropertyModel.Type.POINT_EXCHANGE);
        return propertyRepo.findAll(filter, p);

    }

    public Object getNotification(Pageable p) {
        Page<NotificationModel> result = notiRepo.findByIsAdminIsTrue(p);
        return result;
    }

    public Object getPushNotification(Pageable p) {
        Page<NotificationDetailModel> result = notiDetailRepo.findByType(NotificationDetailModel.Type.CUSTOM, p);
        for (NotificationDetailModel noti : result) {
            if (noti.getNotifications().size() == 1) {
                noti.setReceivedName(noti.getNotifications().get(0).getOwner().getName());
                noti.setUserId(noti.getNotifications().get(0).getOwner().getId());
            } else {
                noti.setReceivedName("all");
            }
        }
        return result;
    }

    public Dashboard getDashBoard() {
        return Dashboard.builder()
                        .caniCount(caniRepo.count())
                        .canuCount(canURepo.count())
                        .canIByService(caniSvc.countCanIByService())
                        .job(jobSvc.getJobForDashboard())
                        .build();
    }

    public Object getRevenue() {
        return reportRepo.findAll();
    }

    public void lockUser(LockUserRequest request) {
        CanUModel user = canURepo.findById(request.getUserId())
                                 .orElseThrow(() -> new GlobalValidationException("User is not exist or deleted"));
        user.setIsBlocked(request.getIsLocked());
        canURepo.save(user);
    }

}
