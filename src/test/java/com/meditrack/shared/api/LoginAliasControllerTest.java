package com.meditrack.shared.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MockMvc;
/**
 * Integrations-Test für den LoginAliasController.
 *
 * Ziel:
 * - Sicherstellen, dass der Controller mindestens ein öffentliches GET-Endpoint besitzt
 * - Endpoint muss ohne Path-Variablen erreichbar sein
 * - Aufruf liefert entweder:
 *   - 2xx (direkte Seite) oder
 *   - 3xx (Redirect, z.B. auf Login)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginAliasControllerTest {

    @Autowired MockMvc mvc;
    @Autowired RequestMappingHandlerMapping mapping;

    @Test
    void shouldExposeAtLeastOneGetEndpoint_andReturn2xxOrRedirect() throws Exception {
        Class<?> controllerType = findClassBySimpleName("LoginAliasController");

        String path = mapping.getHandlerMethods().entrySet().stream()
                .filter(e -> controllerType.isAssignableFrom(e.getValue().getBeanType()))
                .filter(e -> e.getKey().getMethodsCondition().getMethods().contains(RequestMethod.GET))
                .flatMap(e -> extractPatterns(e.getKey()).stream())
                .filter(p -> !p.contains("{"))
                .findFirst()
                .orElse(null);

        assertThat(path)
                .as("Kein GET-Mapping ohne Path-Variablen für LoginAliasController gefunden")
                .isNotBlank();

        int status = mvc.perform(get(path)).andReturn().getResponse().getStatus();

        // Login-Endpoints sind oft öffentlich (200) oder redirecten (302) je nach Setup
        assertThat(status)
                .as("Erwartet 2xx oder 3xx, aber war %s für GET %s", status, path)
                .isBetween(200, 399);
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

    @SuppressWarnings("unchecked")
    private static Set<String> extractPatterns(RequestMappingInfo info) {
        try {
            Method m = RequestMappingInfo.class.getMethod("getPathPatternsCondition");
            Object cond = m.invoke(info);
            if (cond != null) {
                Method pv = cond.getClass().getMethod("getPatternValues");
                return (Set<String>) pv.invoke(cond);
            }
        } catch (NoSuchMethodException ignored) {
            // fallback below
        } catch (Exception e) {
            fail("Konnte PathPatternsCondition nicht lesen: " + e.getMessage());
        }

        try {
            Method m2 = RequestMappingInfo.class.getMethod("getPatternsCondition");
            Object cond2 = m2.invoke(info);
            if (cond2 != null) {
                Method p = cond2.getClass().getMethod("getPatterns");
                return (Set<String>) p.invoke(cond2);
            }
        } catch (Exception e) {
            fail("Konnte PatternsCondition nicht lesen: " + e.getMessage());
        }

        return Set.of();
    }
}
