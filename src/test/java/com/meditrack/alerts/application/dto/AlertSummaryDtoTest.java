package com.meditrack.alerts.application.dto;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Minimaler Robustheits-Test für AlertSummaryDto.
 *
 * Ziel:
 * - DTO muss instanziierbar sein (auch nach Refactorings)
 * - toString() darf nicht leer sein (Debug-/Logging-Tauglichkeit)
 */
class AlertSummaryDtoTest {

    @Test
    void shouldBeInstantiableAndHaveNonBlankToString() {
        AlertSummaryDto dto = newInstance(AlertSummaryDto.class);

        assertThat(dto).isNotNull();
        assertThat(dto.toString()).isNotBlank();
    }

    /**
     * Erstellt eine Instanz über den "größten" Konstruktor und füllt Argumente mit
     * sinnvollen Defaults. Damit bleibt der Test robust bei DTO-Änderungen.
     */
    static <T> T newInstance(Class<T> type) {
        try {
            Constructor<?> ctor = selectConstructor(type);
            Object[] args = buildArgs(ctor.getParameterTypes());
            ctor.setAccessible(true);
            @SuppressWarnings("unchecked")
            T instance = (T) ctor.newInstance(args);
            return instance;
        } catch (Exception e) {
            fail("Konnte " + type.getSimpleName() + " nicht instanziieren: " + e.getMessage());
            return null;
        }
    }

    private static Constructor<?> selectConstructor(Class<?> type) {
        Constructor<?>[] ctors = type.getDeclaredConstructors();
        assertThat(ctors).isNotEmpty();

        Constructor<?> best = ctors[0];
        for (Constructor<?> c : ctors) {
            if (c.getParameterCount() > best.getParameterCount()) best = c;
        }
        return best;
    }

    private static Object[] buildArgs(Class<?>[] paramTypes) {
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = defaultValue(paramTypes[i]);
        }
        return args;
    }

    private static Object defaultValue(Class<?> t) {
        if (t == String.class) return "x";
        if (t == UUID.class) return UUID.fromString("00000000-0000-0000-0000-000000000001");
        if (t == Instant.class) return Instant.parse("2026-01-10T12:00:00Z");
        if (t == LocalDateTime.class) return LocalDateTime.of(2026, 1, 10, 12, 0);
        if (t == LocalDate.class) return LocalDate.of(2026, 1, 10);
        if (t == OffsetDateTime.class) return OffsetDateTime.parse("2026-01-10T12:00:00+01:00");
        if (t == boolean.class || t == Boolean.class) return true;
        if (t == int.class || t == Integer.class) return 1;
        if (t == long.class || t == Long.class) return 1L;
        if (t == double.class || t == Double.class) return 1.0;

        if (Optional.class.isAssignableFrom(t)) return Optional.of("x");

        if (t.isEnum()) {
            Object[] constants = t.getEnumConstants();
            return constants != null && constants.length > 0 ? constants[0] : null;
        }

        // Fallback: mock (funktioniert i.d.R. auch für final classes bei Mockito inline)
        return Mockito.mock(t);
    }
}
