package com.common.mail;

import com.canu.exception.GlobalValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    final private EmailVerificationTemplate template;

    final private ResetPasswordTemplate resetPasswordTemplate;

    final private JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Value("${app.forgotPassUrl}")
    private String forgetPassUrl;

    @Value("${app.verifyEmailUrl}")
    private String verifyUrl;

    public void sendEmailVerification(String email, String token) {

        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            String link = verifyUrl + token;
            Map<String, String> replacements = new HashMap<String, String>();
            replacements.put("user", "Bro");
            replacements.put("verify-link", link);

            String message = template.getTemplate(replacements);
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom("support@ican-u.com");
            helper.setTo(email);
            helper.setSubject("OTP - Login");
            helper.setText(message, true);

            javaMailSender.send(msg);
        } catch (Exception ex) {
            logger.error("Error on sending email", ex);
            throw new GlobalValidationException("Error on sending the verification email");
        }
    }

    public void sendResetPasswordMail(String email, String token, String name) {

        try {
            MimeMessage msg = javaMailSender.createMimeMessage();

            String link = forgetPassUrl + token;
            Map<String, String> replacements = new HashMap<String, String>();
            replacements.put("user", name);
            replacements.put("reset-link", link);

            String message = resetPasswordTemplate.getTemplate(replacements);
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom("support@ican-u.com");
            helper.setTo(email);
            helper.setSubject("[CANU CANI] RESET PASSWORD\n");
            helper.setText(message, true);

            javaMailSender.send(msg);
        } catch (MessagingException ex) {
            logger.error("Error on sending email", ex);
            throw new GlobalValidationException("Error on sending the verification email");
        }
    }

    public void sendEmail(String[] email, String title, String body) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom("support@ican-u.com");
            helper.setSubject("[CANU CANI]" + title);
            helper.setText(body, true);
//            String[] array = {"stephenle1412@gmail.com"};
//            helper.setBcc(email);

            javaMailSender.send(msg);
        } catch (Exception ex){
            logger.error("Error on sending email", ex);
        }
    }
}
