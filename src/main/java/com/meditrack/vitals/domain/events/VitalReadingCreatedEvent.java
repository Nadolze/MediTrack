package com.meditrack.vitals.domain.events;

import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;

public record VitalReadingCreatedEvent(
        String patientId,
        String vitalReadingId,
        VitalType type,
        double value,
        Unit unit
) {}
