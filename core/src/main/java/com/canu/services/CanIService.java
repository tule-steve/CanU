package com.canu.services;

import com.canu.model.CanIModel;
import com.canu.repositories.CanIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CanIService {

    final private CanIRepository caIRepo;

    public void signUp(CanIModel request) {
        caIRepo.save(request);
    }
}
