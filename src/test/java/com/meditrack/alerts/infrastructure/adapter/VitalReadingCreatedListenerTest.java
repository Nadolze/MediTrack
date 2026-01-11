package com.meditrack.alerts.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Minimaler Robustheits-Test für den VitalReadingCreatedListener.
 *
 * Ziel:
 * - Sicherstellen, dass ein Event-Handler existiert
 * - Verifizieren, dass der Listener beim Handling eines Events
 *   mindestens eine seiner Dependencies benutzt
 *
 */
class VitalReadingCreatedListenerTest {

    @Test
    void shouldHandleEvent_andUseAtLeastOneDependency() {
        try {
            InstanceWithDeps iw = newListenerInstanceWithDeps(VitalReadingCreatedListener.class);

            Method handler = findHandlerMethod(VitalReadingCreatedListener.class);
            Class<?> eventType = handler.getParameterTypes()[0];

            Object event = Mockito.mock(eventType);

            handler.setAccessible(true);
            handler.invoke(iw.instance, event);

            long totalInvocations = Arrays.stream(iw.deps)
                    .mapToLong(d -> Mockito.mockingDetails(d).getInvocations().size())
                    .sum();

            assertThat(totalInvocations)
                    .as("Listener hat keine Dependency benutzt – entweder ist der Handler leer oder Test findet falsche Methode.")
                    .isGreaterThan(0);

        } catch (Exception e) {
            fail("Listener-Test fehlgeschlagen: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static class InstanceWithDeps {
        final Object instance;
        final Object[] deps;

        InstanceWithDeps(Object instance, Object[] deps) {
            this.instance = instance;
            this.deps = deps;
        }
    }

    private static <T> InstanceWithDeps newListenerInstanceWithDeps(Class<T> type) throws Exception {
        Constructor<?> ctor = Arrays.stream(type.getDeclaredConstructors())
                .max((a, b) -> Integer.compare(a.getParameterCount(), b.getParameterCount()))
                .orElseThrow();

        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] deps = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            deps[i] = Mockito.mock(paramTypes[i]);
        }

        ctor.setAccessible(true);
        Object instance = ctor.newInstance(deps);

        return new InstanceWithDeps(instance, deps);
    }

    private static Method findHandlerMethod(Class<?> listenerType) {
        // 1) bevorzugt: @EventListener + 1 Parameter
        for (Method m : listenerType.getDeclaredMethods()) {
            if (m.isAnnotationPresent(EventListener.class) && m.getParameterCount() == 1) {
                return m;
            }
        }
        // 2) fallback: irgendeine Methode mit genau 1 Parameter (typischer Event-Handler)
        for (Method m : listenerType.getDeclaredMethods()) {
            if (m.getParameterCount() == 1) {
                return m;
            }
        }
        fail("Keine Handler-Methode (1 Param oder @EventListener) in " + listenerType.getSimpleName() + " gefunden");
        return null;
    }
}
