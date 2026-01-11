package com.meditrack.shared.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Erlaubt deutsche Eingabeformate für LocalDate, z.B.:
 * - 07.01.26
 * - 07.01.2026
 * Zusätzlich weiterhin ISO:
 * - 2026-01-07
 */
@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE, // yyyy-MM-dd
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withLocale(Locale.GERMANY),
            DateTimeFormatter.ofPattern("dd.MM.uu").withLocale(Locale.GERMANY)
    );

    @Override
    public LocalDate convert(String source) {
        if (source == null) return null;

        String s = source.trim();
        if (s.isEmpty()) return null;

        for (DateTimeFormatter fmt : FORMATTERS) {
            try {
                return LocalDate.parse(s, fmt);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }

        throw new IllegalArgumentException("Ungültiges Datum: '" + source + "'. Erwartet z.B. 07.01.2026 oder 2026-01-07.");
    }
}
