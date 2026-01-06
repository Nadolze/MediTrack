package com.meditrack.medication.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CreateMedicationPlanCommand {

    @NotBlank(message = "PatientId darf nicht leer sein")
    private String patientId;

    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
