package com.canu.dto.responses;

import com.canu.model.JobModel;
import com.canu.model.JobReviewerModel;
import lombok.Getter;

@Getter
public class AdminRatingDto extends RatingDto {
    public AdminRatingDto(JobReviewerModel data) {
        super();

        caniUserId = data.getReviewer().getId();
        caniName = data.getReviewer().getName();
        caniAvatar = data.getReviewer().getAvatar();
        caniRating = data.getRating();
        caniContent = data.getContent();
    }

    public AdminRatingDto(JobModel data) {
        super(data.getReviewers()
                  .stream()
                  .filter(r -> data.getCreationUser().getId().equals(r.getReviewer().getId()))
                  .findFirst()
                  .get());

        data.getReviewers()
            .stream()
            .filter(r -> data.getRequestedUser().getId().equals(r.getReviewer().getId()))
            .forEach(r -> {
                caniUserId = r.getReviewer().getId();
                caniName = r.getReviewer().getName();
                caniAvatar = r.getReviewer().getAvatar();
                caniRating = r.getRating();
                caniContent = r.getContent();
            });
    }

    Long caniUserId;

    String caniName;

    String caniAvatar;

    Integer caniRating;

    String caniContent;
}
