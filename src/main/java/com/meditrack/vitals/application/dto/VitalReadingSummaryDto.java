package com.meditrack.vitals.application.dto;

import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;

import java.time.LocalDateTime;

public record VitalReadingSummaryDto(
        String id,
        String patientId,
        VitalType type,
        double value,
        Unit unit,
        LocalDateTime measuredAt
) {}
