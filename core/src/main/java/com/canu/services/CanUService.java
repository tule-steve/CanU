package com.canu.services;

import com.canu.dto.requests.CanUSignUpRequest;
import com.canu.dto.requests.SocialSignUpRequest;
import com.canu.exception.GlobalValidationException;
import com.canu.model.AuthProviderModel;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CanUService {

    final private BCryptPasswordEncoder encoder;

    final private CanURepository caURepo;

    final private ProviderRepository providerRepo;

    public void signUp(CanUSignUpRequest request) {

        if(caURepo.findByEmail(request.getEmail()) != null){
            throw new GlobalValidationException("Email is used.");
        }

        String cryptPass = encoder.encode(request.getPassword());
        CanUModel data = new CanUModel();
        data.setEmail(request.getEmail());
        data.setPassword(cryptPass);
        caURepo.save(data);
    }

    public void signUpBySocial(SocialSignUpRequest request) {

        if(caURepo.findByEmail(request.getEmail()) != null){
            throw new GlobalValidationException("Email is used.");
        }

        CanUModel user = new CanUModel();
        user.setEmail(request.getEmail());

        AuthProviderModel provider = new AuthProviderModel();
        provider.setUser(user);
        provider.setProviderKey(request.getProviderKey());
        provider.setProviderKey(request.getProviderType());
        providerRepo.save(provider);
    }
}
