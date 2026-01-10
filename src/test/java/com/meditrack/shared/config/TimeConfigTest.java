package com.meditrack.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class TimeConfigTest {

    @Test
    void shouldDeclareAtLeastOneTimeRelatedBeanMethod() {
        Class<?> cfg = findClassBySimpleName("TimeConfig");

        boolean hasBean = false;
        for (Method m : cfg.getDeclaredMethods()) {
            if (!m.isAnnotationPresent(Bean.class)) continue;

            Class<?> rt = m.getReturnType();
            if (Clock.class.isAssignableFrom(rt)
                    || Instant.class.isAssignableFrom(rt)
                    || ZoneId.class.isAssignableFrom(rt)) {
                hasBean = true;
                break;
            }
        }

        assertThat(hasBean)
                .as("TimeConfig sollte mindestens eine @Bean-Methode für Clock/Instant/ZoneId bereitstellen")
                .isTrue();
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
