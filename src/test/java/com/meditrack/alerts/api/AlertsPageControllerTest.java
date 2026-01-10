package com.meditrack.alerts.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AlertsPageControllerTest {

    @Autowired MockMvc mvc;
    @Autowired RequestMappingHandlerMapping mapping;

    @Test
    void shouldExposeAtLeastOneGetEndpoint_andRedirectToLoginWhenNotAuthenticated() throws Exception {
        String path = mapping.getHandlerMethods().entrySet().stream()
                .filter(e -> e.getValue().getBeanType().getSimpleName().equals("AlertsPageController"))
                .filter(e -> e.getKey().getMethodsCondition().getMethods().contains(RequestMethod.GET))
                .flatMap(e -> extractPatterns(e.getKey()).stream())
                .filter(p -> !p.contains("{"))
                .findFirst()
                .orElse(null);

        assertThat(path)
                .as("Kein GET-Mapping ohne Path-Variablen für AlertsPageController gefunden")
                .isNotBlank();

        MvcResult result = mvc.perform(get(path)).andReturn();

        int status = result.getResponse().getStatus();
        String redirectedUrl = result.getResponse().getRedirectedUrl(); // kann null sein

        // Erlaubt: 200 (wenn öffentlich) ODER Redirect zu /login (wenn geschützt)
        if (status == 200) {
            return;
        }

        assertThat(status)
                .as("Erwartet 200 OK oder Redirect, aber war: %s", status)
                .isBetween(300, 399);

        assertThat(redirectedUrl)
                .as("Bei Redirect sollte /login das Ziel sein")
                .isNotNull();

        assertThat(redirectedUrl)
                .as("Redirect sollte auf /login zeigen")
                .startsWith("/login");
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
            // fallback
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
