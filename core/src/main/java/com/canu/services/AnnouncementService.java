package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.AnnouncementModel;
import com.canu.repositories.AnnouncementRepository;
import com.canu.specifications.AnnouncementFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    final AnnouncementRepository announceRepo;

    public Page<AnnouncementModel> getListAnnounces(AnnouncementFilter filter, Pageable p) {
        return announceRepo.findAll(filter, p);
    }

    public AnnouncementModel initialAnnounce(AnnouncementModel model) {
        return announceRepo.save(model);
    }

    public void delete(Long id) {
        announceRepo.deleteById(id);
    }

    public AnnouncementModel getById(Long id) {
        return announceRepo.findById(id).orElseThrow(() -> new GlobalValidationException(
                "cannot find announcement"));
    }
}
