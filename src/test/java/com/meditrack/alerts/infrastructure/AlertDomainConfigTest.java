package com.meditrack.alerts.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AlertDomainConfigTest {

    private final ApplicationContextRunner ctx = new ApplicationContextRunner()
            .withUserConfiguration(AlertDomainConfig.class);

    @Test
    void shouldRegisterAllBeanMethods() {
        ctx.run(context -> {
            assertThat(context).hasNotFailed();

            for (Method m : AlertDomainConfig.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Bean.class)) {
                    Class<?> beanType = m.getReturnType();
                    Map<String, ?> beans = context.getBeansOfType(beanType);

                    assertThat(beans)
                            .as("Bean für @Bean-Methode %s() (Type=%s) fehlt", m.getName(), beanType.getName())
                            .isNotEmpty();
                }
            }
        });
    }
}
