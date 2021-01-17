package com.canu.services;

import com.canu.dto.CanUSignUpRequest;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CanUService {

    final private BCryptPasswordEncoder encoder;

    final private CanURepository caURepo;

    public void signUp(CanUSignUpRequest request) {
        String cryptPass = encoder.encode(request.getPassword());
        CanUModel data = new CanUModel();
        data.setEmail(request.getEmail());
        data.setPassword(cryptPass);
        caURepo.save(data);
    }
}
