package com.canu.anticorrupt;

import com.canu.model.TemplateModel;
import com.canu.repositories.TemplateRepository;
import freemarker.cache.StringTemplateLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
@RequiredArgsConstructor
public class FreemarkerConfig {

    final private TemplateRepository tempRepo;

    @Primary
    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        // Create new configuration bean
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        // Create template loader
        StringTemplateLoader sTempLoader = new StringTemplateLoader();
        // Find all templates
        Iterable<TemplateModel> ite = tempRepo.findAll();
        ite.forEach((template) -> {
            // Put them in loader
            sTempLoader.putTemplate(template.getType().toString(), template.getTemplate());
            sTempLoader.putTemplate(template.getType().toTitleString() , template.getDescription());
            sTempLoader.putTemplate(template.getType().toEmailString() , template.getEmail());
        });
        // Set loader
        bean.setPreTemplateLoaders(sTempLoader);
        return bean;
    }

}