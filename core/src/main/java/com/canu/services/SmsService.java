package com.canu.services;

import com.canu.exception.GlobalValidationException;
import com.canu.model.CanIModel;
import com.canu.model.CanUModel;
import com.canu.repositories.CanIRepository;
import com.canu.repositories.CanURepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SmsService {

    final private CanURepository canURepo;

    final private CanIRepository caniRepo;

    //    public static final String ACCOUNT_SID = "ACa4c66b9d60b0308fd97d6e8cf8412994";
    //
    //    public static final String AUTH_TOKEN = "662f49ee0aee7fb929d26ad066228348";

//    public static final String ACCOUNT_SID = "a04590bd3e5b245bb47c3c9d2d287543";
//
//    public static final String AUTH_TOKEN = "683aa2b5914b1a0a8e7f39189b69df12";

    public static final String ACCOUNT_SID = "SK7cde095025c73f83fd1a68fe85f6e5f2";
    public static final String AUTH_TOKEN = "hlN4AhB3WJTtR3A2VxFYGGWEicFKJ1pf";

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @PostConstruct
    private void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public Long sendSms(String phoneNumber) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        CanIModel cani = uUser.getCanIModel();
        if (cani == null) {
            cani = new CanIModel();
            uUser.setCanIModel(cani);
            cani.setStatus(CanIModel.Status.DRAFT);
        }

        if (phoneNumber.equalsIgnoreCase(cani.getPhone()) && cani.getPhoneVerified()) {
            throw new GlobalValidationException("phone is ready verified");
        }

        if (cani.getSentSmsCodeAt() != null &&
            LocalDateTime.now().minusMinutes(5).isBefore(cani.getSentSmsCodeAt())) {
            throw new GlobalValidationException("please wait 5 minutes for the next phone verifying");
        }

        cani.setPhone(phoneNumber);
        cani.setSentSmsCodeAt(LocalDateTime.now());
        Verification verification = Verification.creator(
                "VAf8f53bf724a25ce8ba2926c936dd5ccc",
                phoneNumber,
                Verification.Channel.SMS.toString()).create();
        caniRepo.save(cani);
        logger.info("Send sms for verify phone with sid {}", verification.getSid());
        return cani.getId();
    }

    public void verifyCode(String code) {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CanUModel uUser = canURepo.findByEmail(user.getUsername());

            VerificationCheck verificationCheck = VerificationCheck.creator(
                    "VAf8f53bf724a25ce8ba2926c936dd5ccc",
                    code).setTo(uUser.getCanIModel().getPhone()).create();

            if ("pending".equalsIgnoreCase(verificationCheck.getStatus())) {
                throw new GlobalValidationException("Code is not matching");
            }
            uUser.getCanIModel().setPhoneVerified(true);
            caniRepo.save(uUser.getCanIModel());

        } catch (ApiException ex) {
            logger.error("error on verifying code", ex);
            throw new GlobalValidationException("Code is expired/verified, please try again");
        }
    }
}
