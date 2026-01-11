package com.meditrack.medication.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Ein Eintrag innerhalb eines Medikationsplans (z.B. "Ibuprofen 400mg, 3x täglich").
 *
 * Entspricht der Tabelle mt_medication_plan_item.
 *
 * Hinweis: Lombok wurde entfernt, um Builds ohne Lombok stabil zu halten.
 */
@Entity
@Table(name = "mt_medication_plan_item")
public class MedicationPlanItem {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MedicationPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @Column(name = "medication_name", nullable = false, length = 255)
    private String medicationName;

    @Column(name = "dose", length = 50)
    private String dose;

    @Column(name = "dose_unit", length = 20)
    private String doseUnit;

    @Column(name = "frequency", length = 50)
    private String frequency;

    @Column(name = "time_of_day", length = 50)
    private String timeOfDay;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public MedicationPlanItem() {
        // JPA
    }

    public static MedicationPlanItem create(String medicationName,
                                            String dose,
                                            String doseUnit,
                                            String frequency,
                                            String timeOfDay,
                                            String instructions) {
        MedicationPlanItem item = new MedicationPlanItem();
        item.id = UUID.randomUUID().toString();
        item.medicationName = medicationName;
        item.dose = dose;
        item.doseUnit = doseUnit;
        item.frequency = frequency;
        item.timeOfDay = timeOfDay;
        item.instructions = instructions;
        item.createdAt = Instant.now();
        item.updatedAt = null;
        return item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MedicationPlan getPlan() {
        return plan;
    }

    public void setPlan(MedicationPlan plan) {
        this.plan = plan;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MedicationPlanItem{" +
                "id='" + id + '\'' +
                ", medicationName='" + medicationName + '\'' +
                ", dose='" + dose + '\'' +
                ", doseUnit='" + doseUnit + '\'' +
                ", frequency='" + frequency + '\'' +
                ", timeOfDay='" + timeOfDay + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicationPlanItem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
