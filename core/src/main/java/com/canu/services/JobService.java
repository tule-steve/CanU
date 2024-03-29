package com.canu.services;

import com.canu.dto.requests.*;
import com.canu.dto.responses.JobDto;
import com.canu.dto.responses.RatingDto;
import com.canu.exception.GlobalValidationException;
import com.canu.model.*;
import com.canu.repositories.*;
import com.canu.specifications.JobFilter;
import com.canu.specifications.JobRatingFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    final private JobRepository jobRepo;

    final private CanURepository canURepo;

    final private TagRepository tagRepo;

    final private SkillSetRepository skillSetRepo;

    final private SocketService socketSvc;

    final private PaymentService paymentSvc;

    final JobReviewRepository jobReviewRepo;

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    public JobDto postJob(JobModel request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        if (user == null) {
            logger.error("User {} not found", user.getUsername());
            throw new GlobalValidationException("User is not exist or deleted");
        }

        if (request.getCreationUser() == null) {
            request.setCreationUser(uUser);
        }

        List<SkillSetModel> skillSets = skillSetRepo.findAllById(request.getService());
        if (!Objects.equals(skillSets.size(), request.getService().size())) {
            throw new GlobalValidationException("Some chosen services not existed. Please try again");
        }
        request.setSkillSets(skillSets);
        addKeywordsIntoJob(request.getKeyword(), request);
        request = jobRepo.save(request);
        JobDto job = getJobDetail(request.getId());
        socketSvc.pushCanUForPostJob(request);

        List<CanUModel> canus = canURepo.findCanIForJobNotification(request.getService(), request.getCreationUser().getId());
        for(CanUModel cani : canus){
            socketSvc.pushCanIForPostJob(request, Arrays.asList(cani));
        }

        return job;
    }

    private void addKeywordsIntoJob(Set<String> keywords, JobModel job) {
        List<TagModel> tagEntities = tagRepo.findAllByTagIn(keywords);
        job.setTags(tagEntities);
        if (!Objects.equals(tagEntities.size(), keywords.size())) {
            TagModel newTag;
            for (String keyword : keywords) {
                boolean isNeedAdd = true;
                for (TagModel tagEntity : tagEntities) {
                    if (tagEntity.getTag().trim().equalsIgnoreCase(keyword)) {
                        isNeedAdd = false;
                    }
                }

                if (isNeedAdd) {
                    newTag = new TagModel();
                    newTag.setTag(keyword.trim());
                    job.getTags().add(newTag);
                }
            }
        }
    }

    public JobDto getJobDetail(Long id) {
        JobModel job = jobRepo.findById(id)
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        return new JobDto(job);
    }

    private void setAdditionData(JobModel job) {
        job.setKeyword(job.getTags().stream().map(r -> r.getTag()).collect(Collectors.toSet()));
        job.setService(job.getSkillSets().stream().map(r -> r.getId()).collect(Collectors.toSet()));
        job.setPickupCanI(job.getCanus().stream().map(r -> r.getId()).collect(Collectors.toSet()));
    }

    public Page<JobDto> showJobList(JobFilter filter, Pageable p) {
        Page<JobModel> jobEntities = jobRepo.findAll(filter, p);
        List<JobDto> jobList = jobEntities.stream().map(r -> new JobDto(r)).collect(Collectors.toList());
        Page<JobDto> result = new PageImpl<>(jobList, p, jobEntities.getTotalElements());
        return result;
    }

    public void pickUpJob(Long jobId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findByIdFetchCreateUser(jobId)
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        if (!job.getCanus().stream().anyMatch(r -> r.getId() == uUser.getId())) {
            job.getCanus().add(uUser);
            jobRepo.save(job);
            socketSvc.pushNoticeForPickJob(job);
        }
    }

    public JobDto updateSubStatus(UpdateSubStatusRequest request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        JobModel job = jobRepo.findByIdFetchCreateUser(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));

        if (!(!JobModel.JobStatus.PROCESSING.equals(job.getStatus()) ||
              uUser.getId().equals(job.getRequestedUser().getId()))) {
            throw new GlobalValidationException("do not have privilege for this action");
        }
        LocalDateTime now = LocalDateTime.now();
        for (SubStatusModel.Status subStatus : SubStatusModel.Status.values()) {
            if (!job.getSubStatus().containsKey(subStatus.toString()) &&
                subStatus.ordinal() <= request.getStatus().ordinal()) {
                job.getSubStatus().put(subStatus.toString(), now);
            }
        }

        JobDto result = new JobDto(jobRepo.save(job));

        socketSvc.noticeCanUSubStatus(job, request.getStatus());
        return result;
    }

    public void cancelJob(Long jobId, String reason) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(jobId)
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        uUser.validatePrivilege(job.getCreationUser());
        job.setReason(reason);
        if (JobModel.JobStatus.PENDING.equals(job.getStatus())) {
            jobRepo.delete(job);
        } else {
            job.setStatus(JobModel.JobStatus.REQUEST_CANCEL);
            jobRepo.save(job);
        }
        socketSvc.noticeAdminCancelJob(job);

    }

    public void cancelJobByAdmin(AdminJobCancelRequest request) {
        JobModel job = jobRepo.findByIdFetchCreateAndRequestUser(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        if (request.getIsApproval()) {
            paymentSvc.cancelPaymentForAdmin(job.getId());
            jobRepo.delete(job);
            socketSvc.noticeUserJobCancelled(job);
        } else {
            job.setCancelStatus(JobModel.CancelStatus.REJECTED);
            job.setStatus(JobModel.JobStatus.PROCESSING);
            jobRepo.save(job);
            socketSvc.noticeCanUJobNotCancelled(job);
        }
        job.setAdminReason(request.getNote());

    }

    public void updateJob(UpdateJobRequest request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(request.getId())
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        uUser.validatePrivilege(job.getCreationUser());

        request.updateJobEntity(job);
        if (request.getKeyword().size() > 0) {
            addKeywordsIntoJob(request.getKeyword(), job);
        }
        jobRepo.save(job);
    }

    public void startJob(UpdateJobStatusRequest request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findByIdFetchCreateUser(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));
        CanUModel requestedUser = canURepo.findById(request.getRequestedUserId())
                                          .orElseThrow(() -> new GlobalValidationException(
                                                  "Cannot find the user for requested user. Please try again"));
        if (!uUser.getId().equals(request.getOwner()) && !uUser.getId().equals(request.getRequestedUserId()) ||
            !JobModel.JobStatus.PENDING.equals(job.getStatus())) {
            throw new GlobalValidationException("permission denied");
        }
        job.setRequestedUser(requestedUser);
        if (request.getPrice() != null && request.getCurrency() != null) {
            job.setTotal(request.getPrice());
            job.setCurrency(request.getCurrency());
        }
        //        job.setStatus(JobModel.JobStatus.PROCESSING);
        jobRepo.save(job);
        socketSvc.pushCanUForStartJob(job);
        socketSvc.pushCanIForStartJob(job);
    }

    public void completeJobByCanI(Long jobId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel cani = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findByIdFetchCreateUser(jobId)
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));

        if (!JobModel.JobStatus.PROCESSING.equals(job.getStatus()) ||
            !cani.getId().equals(job.getRequestedUser().getId())) {
            throw new GlobalValidationException("do not have privilege to complete this job");
        }

        socketSvc.noticeCanIJobComplete(job);
    }

    public void completeJobByCanU(Long jobId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel canu = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findByIdFetchCreateAndRequestUser(jobId)
                              .orElseThrow(() -> new GlobalValidationException("job is not existed or deleted"));

        if (!JobModel.JobStatus.PROCESSING.equals(job.getStatus()) ||
            !canu.getId().equals(job.getCreationUser().getId())) {
            throw new GlobalValidationException("do not have privilege to complete this job");
        }
        job.setStatus(JobModel.JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        paymentSvc.payout(job);
        PaymentModel payment = job.getPayments()
                                  .stream()
                                  .filter(r -> PaymentModel.Status.TOPPED_UP.equals(r.getStatus()))
                                  .findFirst()
                                  .orElse(null);
        if (payment == null) {
            throw new GlobalValidationException("do not have privilege to complete this job");
        }

        BigDecimal totalPoint = paymentSvc.convert2Point(payment.getTotal(), payment.getCurrency());
        if (totalPoint != null) {
            BigDecimal rate = job.getSkillSets()
                                 .stream()
                                 .map(r -> r.getCpointRate())
                                 .min(BigDecimal::compareTo)
                                 .orElse(BigDecimal.ZERO);
            int cpoint = totalPoint.multiply(rate).divide(BigDecimal.valueOf(100)).intValue();
            payment.setCpointReward(cpoint);
            canu.setCPoint(canu.getCPoint() + cpoint);
            canu.setCCash(canu.getCCash() - totalPoint.longValue());
            paymentSvc.save(payment);
            canURepo.save(canu);
        }
        socketSvc.noticeCanUJobComplete(job);
    }

    public void updateCpoin() {
        List<JobModel> jobs = jobRepo.findAll();
        jobs.stream().filter(r -> JobModel.JobStatus.COMPLETED.equals(r.getStatus())).forEach(job -> {
            PaymentModel payment = job.getPayments()
                                      .stream()
                                      .filter(r -> PaymentModel.Status.TOPPED_UP.equals(r.getStatus()))
                                      .findFirst()
                                      .orElse(null);
            if (payment == null) {
                return;
            }
            BigDecimal totalPoint = paymentSvc.convert2Point(payment.getTotal(), payment.getCurrency());
            if (totalPoint != null) {
                BigDecimal rate = job.getSkillSets()
                                     .stream()
                                     .map(r -> r.getCpointRate())
                                     .min(BigDecimal::compareTo)
                                     .orElse(BigDecimal.ZERO);
                int cpoint = totalPoint.multiply(rate).divide(BigDecimal.valueOf(100)).intValue();
                payment.setCpointReward(cpoint);
                paymentSvc.save(payment);
            }
        });
    }

    public void ratingUser(RatingUserRequest request) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel canu = canURepo.findByEmail(user.getUsername());
        JobModel job = jobRepo.findById(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException(
                                      "job is not existed or deleted"));
        if (!JobModel.JobStatus.COMPLETED.equals(job.getStatus())) {
            throw new GlobalValidationException("Cannot review for uncompleted job");
        }
        CanUModel reviewer;
        CanUModel targetUser;
        if (job.getCreationUser().getId().equals(canu.getId())) {
            reviewer = canu;
            targetUser = job.getRequestedUser();
            job.setRating(request.getValue());
            if (targetUser.isRegisterCanI()) {
                CanIModel cani = targetUser.getCanIModel();
                cani.setTotalRating(cani.getTotalRating() + request.getValue());
                cani.setRatingCount(cani.getRatingCount() + 1);
                cani.setRating(BigDecimal.valueOf(cani.getTotalRating())
                                         .divide(BigDecimal.valueOf(cani.getRatingCount()),
                                                 2,
                                                 BigDecimal.ROUND_CEILING));
            }
        } else {
            reviewer = canu;
            targetUser = job.getCreationUser();
        }

        if (jobReviewRepo.existsDistinctByJobAndReviewerAndTarget(job, reviewer, targetUser)) {
            throw new GlobalValidationException("only rating one time");
        }

        JobReviewerModel model = JobReviewerModel.builder()
                                                 .job(job)
                                                 .reviewer(reviewer)
                                                 .target(targetUser)
                                                 .rating(request.getValue())
                                                 .content(request.getContent())
                                                 .isHidden(false)
                                                 .build();

        jobReviewRepo.save(model);
        jobRepo.save(job);
    }

    public Object getRatingList(JobRatingFilter filter, Pageable p) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel canu = canURepo.findByEmail(user.getUsername());
        filter.setUserId(canu.getId());
        return buildRatingList(filter, p);
    }

    public Object getReviewList(JobRatingFilter filter, Pageable p) {
        filter.setIsGetReview(true);
        return buildRatingList(filter, p);
    }

    private Page<JobDto> buildRatingList(JobRatingFilter filter, Pageable p) {
        Page<JobReviewerModel> data = jobReviewRepo.findAll(filter, p);
        List ratingList = new ArrayList();
        for (JobReviewerModel review : data) {
            ratingList.add(new RatingDto(review));
        }
        Page<JobDto> result = new PageImpl<>(ratingList, p, data.getTotalElements());
        return result;
    }

    public Object getAdminRatingList(JobFilter filter, Pageable p) {
        filter.setStatus(JobModel.JobStatus.COMPLETED);
        Page<JobModel> data = jobRepo.findAll(filter, p);
        List ratingList = new ArrayList();
        for (JobModel job : data) {
            if (job.getReviewers().size() > 0) {
                JobReviewerModel review;
                if (filter.getIsCanI()) {
                    review = job.getReviewers()
                                .stream()
                                .filter(r -> job.getRequestedUser().getId().equals(r.getReviewer().getId()))
                                .findFirst()
                                .get();
                } else {
                    review = job.getReviewers()
                                .stream()
                                .filter(r -> job.getCreationUser().getId().equals(r.getReviewer().getId()))
                                .findFirst()
                                .get();
                }
                ratingList.add(new RatingDto(review));
            }
        }
        Page<JobDto> result = new PageImpl<>(ratingList, p, data.getTotalElements());
        return result;
    }

    public void hideReview(HideReviewRequest request) {
        JobModel job = jobRepo.findById(request.getJobId())
                              .orElseThrow(() -> new GlobalValidationException(
                                      "Cannot find the job with id: " + request.getJobId()));
        Long userId;
        if (request.getIsCanI()) {
            userId = job.getRequestedUser().getId();
        } else {
            userId = job.getCreationUser().getId();
        }
        JobReviewerModel review = job.getReviewers()
                                     .stream()
                                     .filter(r -> r.getReviewer().getId().equals(userId))
                                     .findFirst()
                                     .orElseThrow(() -> new GlobalValidationException("Cannot find review"));
        review.setIsHidden(true);
        jobReviewRepo.save(review);
    }

    public List<JobDto> getUnpaidJobList(JobFilter filter) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel canu = canURepo.findByEmail(user.getUsername());
        filter.setOwner(canu.getId());
        filter.setStatus(JobModel.JobStatus.PROCESSING);
        List<JobModel> jobEntities = jobRepo.findAll(filter);
        List<JobModel> nonePaymentJob = jobEntities.stream()
                                                   .filter(r -> !r.getPayments()
                                                                  .stream()
                                                                  .anyMatch(e -> PaymentModel.Status.TOPPED_UP.equals(e.getStatus()))
                                                   ).collect(Collectors.toList());

        List<JobDto> jobList = nonePaymentJob.stream().map(r -> new JobDto(r)).collect(Collectors.toList());
        return jobList;
    }

    public Map<String, Map<String, Long>>  getJobForDashboard(){
        List<Object[]> metadata = jobRepo.findJobReport();
        Map<String, Map<String, Long>> result = new HashMap<>();
        for(Object[] row : metadata){
            Map data = result.computeIfAbsent(row[0].toString(), k -> new HashMap<>());
            data.put(row[1].toString(), row[2]);
        }
        return result;
    }

}
