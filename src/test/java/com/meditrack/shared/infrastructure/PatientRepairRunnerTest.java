package com.meditrack.shared.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
/**
 * Smoke- und Robustheitstest für den PatientRepairRunner.
 *
 * Ziel:
 * - sicherstellen, dass PatientRepairRunner als Spring-Bean registriert ist
 * - verifizieren, dass der Runner mit leeren Argumenten ausführbar ist
 *   (ApplicationRunner / CommandLineRunner / run()-Fallback)
 * - frühzeitiges Erkennen von fehlerhaften Konstruktoren oder
 *   nicht robustem Startverhalten beim Application-Bootstrap
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PatientRepairRunnerTest {

    @Test
    void shouldBePresentAsSpringBean(ApplicationContext ctx) {
        Object runner = findBeanBySimpleName(ctx, "PatientRepairRunner");
        assertThat(runner).isNotNull();
    }

    @Test
    void shouldRunWithEmptyArgs_andNotCrash(ApplicationContext ctx) {
        Object runner = findBeanBySimpleName(ctx, "PatientRepairRunner");
        assertThat(runner).isNotNull();

        try {
            runRunner(runner);
        } catch (Exception e) {
            fail("PatientRepairRunner sollte mit leeren Args nicht crashen: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static Object findBeanBySimpleName(ApplicationContext ctx, String simpleName) {
        for (String name : ctx.getBeanDefinitionNames()) {
            Class<?> type = ctx.getType(name);
            if (type != null && simpleName.equals(type.getSimpleName())) {
                return ctx.getBean(name);
            }
        }
        return null;
    }

    private static void runRunner(Object runner) throws Exception {
        // 1) Bevorzugt: ApplicationRunner
        if (runner instanceof ApplicationRunner ar) {
            ApplicationArguments args = new DefaultApplicationArguments(new String[0]);
            ar.run(args);
            return;
        }

        // 2) CommandLineRunner
        if (runner instanceof CommandLineRunner clr) {
            clr.run();
            return;
        }

        // 3) Fallback: run(...) per Reflection
        Method run = findRunMethod(runner.getClass());
        if (run == null) {
            fail("Kein run(...) EntryPoint gefunden in " + runner.getClass().getName());
            return;
        }

        run.setAccessible(true);

        Class<?>[] p = run.getParameterTypes();
        if (p.length == 1 && "org.springframework.boot.ApplicationArguments".equals(p[0].getName())) {
            run.invoke(runner, new DefaultApplicationArguments(new String[0]));
        } else if (p.length == 1 && p[0].isArray() && p[0].getComponentType().equals(String.class)) {
            run.invoke(runner, (Object) new String[0]);
        } else if (p.length == 0) {
            run.invoke(runner);
        } else {
            fail("Gefundene run(...) Signatur ist nicht unterstützt: " + run);
        }
    }

    private static Method findRunMethod(Class<?> type) {
        return Arrays.stream(type.getMethods())
                .filter(m -> "run".equals(m.getName()))
                .findFirst()
                .orElseGet(() -> Arrays.stream(type.getDeclaredMethods())
                        .filter(m -> "run".equals(m.getName()))
                        .findFirst()
                        .orElse(null));
    }
}
