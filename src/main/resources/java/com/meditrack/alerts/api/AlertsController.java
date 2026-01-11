package com.meditrack.alerts.api;

import com.meditrack.alerts.application.dto.AlertSummaryDto;
import com.meditrack.alerts.application.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    private final AlertService service;

    public AlertsController(AlertService service) {
        this.service = service;
    }

    @GetMapping("/patient/{patientId}")
    public List<AlertSummaryDto> listForPatient(@PathVariable String patientId) {
        return service.listForPatient(patientId);
    }

    @PostMapping("/{alertId}/acknowledge")
    public ResponseEntity<Void> acknowledge(@PathVariable String alertId) {
        service.acknowledge(alertId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{alertId}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable String alertId) {
        service.resolve(alertId);
        return ResponseEntity.ok().build();
    }
}
