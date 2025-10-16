package com.commons.hibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ResourceBundleMessageInterpolator(
                new PlatformResourceBundleLocator("validation-messages")))
            .buildValidatorFactory();
        return factory.getValidator();
    }
}
