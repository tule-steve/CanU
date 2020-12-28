package com.canu.dto.responses;

import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.JobReviewerModel;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JobDto {

    public JobDto(JobModel job) {
        id = job.getId();
        status = job.getStatus();

        creationUser = CanIDto.builder()
                              .id(job.getCreationUser().getId())
                              .name(job.getCreationUser().getName())
                              .avatar(job.getCreationUser().getAvatar())
                              .build();

        title = job.getTitle();
        content = job.getContent();
        nation = job.getNation();
        total = job.getTotal();
        createdAt = job.getCreatedAt();
        currency = job.getCurrency();
        image = job.getImage();
        city = job.getCity();
        keyword = job.getTags().stream().map(r -> r.getTag()).collect(Collectors.toSet());
        service = job.getSkillSets()
                     .stream()
                     .map(r -> new ServiceDto(r.getId(), r.getTitle(), r.getSlug()))
                     .collect(Collectors.toList());

        if (job.getRequestedUser() != null) {
            CanUModel r = job.getRequestedUser();
            requestedUser = CanIDto.builder()
                                   .id(r.getId())
                                   .avatar(r.getCanIModel().getAvatar())
                                   .name(r.getCanIModel().getName())
                                   .price(r.getCanIModel().getPrice())
                                   .build();
        } else {
            pickupCanI = job.getCanus()
                            .stream()
                            .map(r -> {
                                if (r.getCanIModel() == null) {
                                    return CanIDto.builder()
                                                  .id(r.getId())
                                                  .name(r.getName())
                                                  .build();
                                }
                                return CanIDto.builder()
                                              .id(r.getId())
                                              .avatar(r.getCanIModel().getAvatar())
                                              .name(r.getCanIModel().getName())
                                              .price(r.getCanIModel().getPrice())
                                              .nation(r.getCanIModel().getNational())
                                              .address(r.getCanIModel().getAddress())
                                              .build();
                            })
                            .collect(Collectors.toList());
        }
        if (JobModel.JobStatus.COMPLETED.equals(job.getStatus()) && job.getReviewers() != null) {
            job.getReviewers().forEach(r -> {
                if (r.getReviewer().getId().equals(job.getCreationUser().getId())) {
                    review.put("canu_review", r);
                } else {
                    review.put("cani_review", r);
                }
            });
        }

    }

    Long id;

    @Enumerated(EnumType.STRING)
    JobModel.JobStatus status;

    CanIDto creationUser;

    CanIDto requestedUser;

    String title;

    String content;

    String nation;

    Long total;

    String currency;

    private List<ServiceDto> service;

    private Set<String> keyword;

    private List<CanIDto> pickupCanI;

    LocalDateTime createdAt;

    String image;

    String city;

    Map<String, JobReviewerModel> review = new HashMap<>();

    @JsonAnyGetter
    public Map<String, JobReviewerModel> getReview() {
        return review;
    }

}
