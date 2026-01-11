package com.meditrack.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Integrationstest für die WebMvcConfig.
 *
 * Ziel:
 * - sicherstellen, dass ein mvcConversionService-Bean existiert
 * - prüfen, dass Standard-Typkonvertierungen für Web-Requests verfügbar sind
 *   (z.B. String → LocalDate für Request-Parameter/Formulare)
 * - absichern, dass MVC-Datenbindung korrekt konfiguriert ist
 */
@SpringBootTest
class WebMvcConfigTest {

    @Autowired
    @Qualifier("mvcConversionService")
    private ConversionService conversionService;

    @Test
    void conversionServiceShouldConvertStringToLocalDate() {
        LocalDate result = conversionService.convert("2026-01-10", LocalDate.class);
        assertThat(result).isEqualTo(LocalDate.of(2026, 1, 10));
    }
}
