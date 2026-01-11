package com.meditrack.alerts.api;

import com.meditrack.alerts.application.dto.AlertSummaryDto;
import com.meditrack.alerts.application.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für Alerts (Warnmeldungen).
 *
 *  * Verantwortlich für:
 *  * - Lesen von Alerts eines Patienten
 *  * - Statusänderungen eines Alerts (acknowledge / resolve)
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    private final AlertService service;

    public AlertsController(AlertService service) {
        this.service = service;
    }

    /**
     * Liefert alle Alerts für einen bestimmten Patienten.
     */
    @GetMapping("/patient/{patientId}")
    public List<AlertSummaryDto> listForPatient(@PathVariable String patientId) {
        return service.listForPatient(patientId);
    }

    /**
     * Bestätigt (acknowledge) einen Alert.
     * Typischer Use-Case: Alert wurde wahrgenommen, aber noch nicht gelöst.
     */
    @PostMapping("/{alertId}/acknowledge")
    public ResponseEntity<Void> acknowledge(@PathVariable String alertId) {
        service.acknowledge(alertId);
        return ResponseEntity.ok().build();
    }

    /**
     * Markiert einen Alert als erledigt (resolved).
     * Typischer Use-Case: Problem wurde behoben.
     */
    @PostMapping("/{alertId}/resolve")
    public ResponseEntity<Void> resolve(@PathVariable String alertId) {
        service.resolve(alertId);
        return ResponseEntity.ok().build();
    }
}
