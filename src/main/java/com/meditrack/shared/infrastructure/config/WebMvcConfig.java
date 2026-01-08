package com.meditrack.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registriert zentrale Converter für das Spring MVC Databinding.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToLocalDateConverter stringToLocalDateConverter;

    public WebMvcConfig(StringToLocalDateConverter stringToLocalDateConverter) {
        this.stringToLocalDateConverter = stringToLocalDateConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToLocalDateConverter);
    }
}
