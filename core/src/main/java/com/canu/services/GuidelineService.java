package com.canu.services;

import com.canu.model.GuidelineModel;
import com.canu.repositories.GuidelineRepository;
import com.canu.specifications.GuidelineFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuidelineService {
    final GuidelineRepository guideRepo;

    public Page<GuidelineModel> getListGuideline(GuidelineFilter filter, Pageable p){
        return guideRepo.findAll(filter, p);
    }

    public GuidelineModel initialGuideline(GuidelineModel model){
        return guideRepo.save(model);
    }
}
