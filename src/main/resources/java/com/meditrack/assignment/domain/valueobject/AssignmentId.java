package com.meditrack.assignment.domain.valueobject;

import java.util.UUID;

/**
 * Identifier für das Assignment-Aggregat.
 */
public record AssignmentId(String value) {

    public AssignmentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AssignmentId darf nicht leer sein");
        }
    }

    public static AssignmentId newId() {
        return new AssignmentId(UUID.randomUUID().toString());
    }
}
