package com.meditrack.alerts.infrastructure;

import com.meditrack.alerts.domain.service.AlertEvaluator;
import com.meditrack.vitals.domain.service.ThresholdCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertDomainConfig {

    @Bean
    public ThresholdCatalog thresholdCatalog() {
        return new ThresholdCatalog();
    }

    @Bean
    public AlertEvaluator alertEvaluator(ThresholdCatalog thresholdCatalog) {
        return new AlertEvaluator(thresholdCatalog);
    }
}
