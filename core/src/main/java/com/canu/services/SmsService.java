package com.canu.services;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SmsService {

    public static final String ACCOUNT_SID = "ACa4c66b9d60b0308fd97d6e8cf8412994";

    public static final String AUTH_TOKEN = "4eed7f3d763400cee3eafeaf7527bcf2";

    public void sendSms() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Verification verification = Verification.creator(
                "VAf8f53bf724a25ce8ba2926c936dd5ccc",
                "+84924403113",
                "sms").create();

        verification.getStatus();
    }

    public void verifyCode(String code){
        VerificationCheck verificationCheck = VerificationCheck.creator(
                "VAf8f53bf724a25ce8ba2926c936dd5ccc",
                code).setTo("+84924403113").create();

        verificationCheck.getStatus();
        verificationCheck.getPayee();
    }
}
