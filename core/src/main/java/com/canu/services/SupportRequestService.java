package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.JobModel;
import com.canu.model.SupportRequestModel;
import com.canu.repositories.SupportRequestRepository;
import com.canu.specifications.SupportRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportRequestService {
    final SupportRequestRepository supportRepo;

    public Page<SupportRequestModel> getListSupportRequest(SupportRequestFilter filter, Pageable p){
        return supportRepo.findAll(filter, p);
    }

    public SupportRequestModel initialGuideline(SupportRequestModel model){
        model.setStatus(JobModel.JobStatus.PENDING);
        return supportRepo.save(model);
    }

    public void delete(Long id) {
        supportRepo.deleteById(id);
    }

    public SupportRequestModel getById(Long id) {
        return supportRepo.findById(id).orElseThrow(() -> new GlobalValidationException(
                "cannot find the support request with id: " + id));
    }
}
