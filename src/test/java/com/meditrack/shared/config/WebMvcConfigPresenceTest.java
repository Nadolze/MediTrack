package com.meditrack.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
/**
 * Test für die Existenz der WebMvcConfig.
 *
 * Ziel:
 * - sicherstellen, dass eine zentrale WebMvcConfig-Klasse existiert
 * - absichern, dass MVC-spezifische Konfiguration (z.B. ViewController,
 *   Interceptors, Formatter, ArgumentResolver) vorgesehen ist
 * - frühzeitiges Fehlschlagen, falls die Web-Konfiguration versehentlich entfernt wird
 */
class WebMvcConfigPresenceTest {

    @Test
    void webMvcConfigClassShouldExist() {
        Class<?> type = findClassBySimpleName("WebMvcConfig");
        assertThat(type).isNotNull();
    }

    private static Class<?> findClassBySimpleName(String simpleName) {
        try {
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);

            TypeFilter filter = (metadataReader, metadataReaderFactory) ->
                    metadataReader.getClassMetadata().getClassName().endsWith("." + simpleName);

            scanner.addIncludeFilter(filter);

            return scanner.findCandidateComponents("com.meditrack").stream()
                    .findFirst()
                    .map(bd -> {
                        try {
                            return Class.forName(bd.getBeanClassName());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElseThrow(() -> new AssertionError("Klasse nicht gefunden: " + simpleName));
        } catch (RuntimeException e) {
            fail("Classpath-Scan fehlgeschlagen: " + e.getMessage());
            return null;
        }
    }
}
