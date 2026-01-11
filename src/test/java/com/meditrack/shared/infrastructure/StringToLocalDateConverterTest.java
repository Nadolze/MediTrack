package com.meditrack.shared.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
/**
 * Unit-Test für den String → LocalDate Converter.
 *
 * Ziel:
 * - prüfen, dass die Klasse org.springframework.core.convert.converter.Converter implementiert
 * - sicherstellen, dass ISO-Datumsstrings korrekt in LocalDate konvertiert werden
 * - ungültige Strings führen zu null oder einer Exception (kein falsches Datum)
 */
class StringToLocalDateConverterTest {

    @Test
    void shouldImplementSpringConverter() {
        Class<?> type = findClassBySimpleName("StringToLocalDateConverter");
        assertThat(Converter.class.isAssignableFrom(type))
                .as("StringToLocalDateConverter sollte org.springframework.core.convert.converter.Converter implementieren")
                .isTrue();
    }

    @Test
    void shouldConvertIsoDate() {
        Class<?> type = findClassBySimpleName("StringToLocalDateConverter");
        Object instance = newInstance(type);

        @SuppressWarnings("unchecked")
        Converter<String, LocalDate> c = (Converter<String, LocalDate>) instance;

        LocalDate d = c.convert("2026-01-10");
        assertThat(d).isEqualTo(LocalDate.of(2026, 1, 10));
    }

    @Test
    void invalidInputShouldReturnNullOrThrow() {
        Class<?> type = findClassBySimpleName("StringToLocalDateConverter");
        Object instance = newInstance(type);

        @SuppressWarnings("unchecked")
        Converter<String, LocalDate> c = (Converter<String, LocalDate>) instance;

        try {
            LocalDate d = c.convert("not-a-date");
            assertThat(d)
                    .as("Bei invaliden Strings darf null zurückkommen oder eine Exception fliegen – aber kein falsches Datum")
                    .isNull();
        } catch (RuntimeException ex) {
            // ok: Exception ist ebenfalls akzeptabel
            assertThat(ex.getMessage()).isNotBlank();
        }
    }

    private static Object newInstance(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException e) {
            fail("StringToLocalDateConverter braucht einen No-Args-Constructor für diesen Test.");
            return null;
        } catch (Exception e) {
            fail("Konnte Converter nicht instanziieren: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
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
