package com.meditrack.medication.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Stammdaten eines Medikaments (z.B. "Ibuprofen 400mg").
 *
 * Entspricht der Tabelle mt_medication.
 *
 * Hinweis: Lombok wurde entfernt, um Builds ohne Lombok stabil zu halten.
 */
@Entity
@Table(name = "mt_medication")
public class Medication {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dosage_form", length = 50)
    private String dosageForm;

    @Column(name = "strength", length = 50)
    private String strength;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Medication() {
        // JPA
    }

    public static Medication create(String name, String dosageForm, String strength, String description) {
        Medication m = new Medication();
        m.id = UUID.randomUUID().toString();
        m.name = name;
        m.dosageForm = dosageForm;
        m.strength = strength;
        m.description = description;
        m.createdAt = Instant.now();
        m.updatedAt = null;
        return m;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Medication{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dosageForm='" + dosageForm + '\'' +
                ", strength='" + strength + '\'' +
                ", description='" + (description == null ? null : (description.length() > 60 ? description.substring(0, 60) + "…" : description)) + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medication medication)) return false;
        return Objects.equals(id, medication.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
