package com.meditrack.medication.application.dto;

import java.time.LocalDate;

/**
 * DTO für Listen-/Übersichtsansichten von Medikationsplänen.
 *
 * Dieses DTO ist bewusst ohne Lombok umgesetzt, damit das Projekt auch dann
 * problemlos baut, wenn IDE/Build-Umgebung kein Annotation-Processing für Lombok
 * aktiviert hat.
 */
public class MedicationPlanSummaryDto {

    private String id;
    private String patientId;
    private String name;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Default-Konstruktor (z.B. für Jackson).
     */
    public MedicationPlanSummaryDto() {
    }

    public MedicationPlanSummaryDto(String id,
                                    String patientId,
                                    String name,
                                    boolean active,
                                    LocalDate startDate,
                                    LocalDate endDate) {
        this.id = id;
        this.patientId = patientId;
        this.name = name;
        this.active = active;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public static final class Builder {
        private String id;
        private String patientId;
        private String name;
        private boolean active;
        private LocalDate startDate;
        private LocalDate endDate;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder patientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public MedicationPlanSummaryDto build() {
            return new MedicationPlanSummaryDto(id, patientId, name, active, startDate, endDate);
        }
    }
}
