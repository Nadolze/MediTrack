package com.meditrack.vitals.domain.events;

import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;

// Domain Event, welches in VitalReadingService ausgelöst wird.
public record VitalReadingCreatedEvent(
        String patientId,
        String vitalReadingId,
        VitalType type,
        double value,
        Unit unit
) {}
