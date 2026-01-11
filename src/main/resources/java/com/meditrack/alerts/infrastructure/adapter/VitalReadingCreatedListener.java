package com.meditrack.alerts.infrastructure.adapter;

import com.meditrack.alerts.application.service.AlertService;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Adapter: konsumiert In-Process Domain Events aus dem vitals-BC.
 */
@Component
public class VitalReadingCreatedListener {

    private final AlertService alertService;

    public VitalReadingCreatedListener(AlertService alertService) {
        this.alertService = alertService;
    }

    @EventListener
    public void on(VitalReadingCreatedEvent event) {
        alertService.handle(event);
    }
}
