package com.common.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfig {

    @Bean
    public EmailVerificationTemplate getVerificationTemplate() throws Exception {
        return new EmailVerificationTemplate();
    }
}
