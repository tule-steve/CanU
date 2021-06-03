package com.canu.services;

import com.canu.dto.responses.SystemRatingDto;
import com.canu.model.UserPropertyModel;
import com.canu.repositories.UserPropertyRepository;
import com.canu.specifications.SystemReviewFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemRatingService {
    final UserPropertyRepository userPropertyRepo;

    public Page<SystemRatingDto> getSystemReview(SystemReviewFilter filter, Pageable p) {
        Page<UserPropertyModel> ratingList = userPropertyRepo.findAll(filter, p);
        List<SystemRatingDto> resultData = new ArrayList<>();
        ratingList.forEach(r -> {
            resultData.add(SystemRatingDto.builder()
                                          .userID(r.getUser().getId())
                                          .userName(r.getUser().getName())
                                          .criteria(r.getProperty().getProperty())
                                          .rating(r.getRating())
                                          .build());
        });

        Page<SystemRatingDto> result = new PageImpl<>(resultData, p, ratingList.getTotalElements());
        return result;
    }
}
