package com.meditrack.shared.api;

/**
 * Zentrale Session-Keys, damit alle BCs exakt denselben Key verwenden.
 */
public final class SessionKeys {

    private SessionKeys() {
        // util
    }

    /**
     * Session-Attribut für den eingeloggten Benutzer.
     * Typ: com.meditrack.shared.valueobject.UserSession
     */
    public static final String LOGGED_IN_USER = "LOGGED_IN_USER";


    /**
     * Optional: merkt sich bei STAFF/ADMIN den zuletzt verwendeten Patient-Kontext,
     * damit Listen/Formulare ohne Query-Param weiterhin funktionieren.
     */
    public static final String ACTIVE_PATIENT_ID = "ACTIVE_PATIENT_ID";
}
