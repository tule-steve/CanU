package com.canu.dto.responses;

import com.canu.model.JobReviewerModel;
import lombok.Getter;

@Getter
public class RatingDto {
    public RatingDto(JobReviewerModel data){
        jobId = data.getJob().getId();
        jobTitle = data.getJob().getTitle();
        userId = data.getReviewer().getId();
        name = data.getReviewer().getName();
        avatar = data.getReviewer().getAvatar();
        rating = data.getRating();
        content = data.getContent();
    }

    Long jobId;

    String jobTitle;

    Long userId;

    String name;

    String avatar;

    Integer rating;

    String content;

    public RatingDto() {

    }
}
