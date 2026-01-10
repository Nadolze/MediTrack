package com.meditrack.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PasswordEncoderConfigTest {

    @Test
    void shouldExposePasswordEncoderBean(ApplicationContext ctx) {
        var beans = ctx.getBeansOfType(PasswordEncoder.class);
        assertThat(beans)
                .as("Es sollte mindestens ein PasswordEncoder-Bean vorhanden sein")
                .isNotEmpty();

        PasswordEncoder enc = beans.values().iterator().next();
        String hash = enc.encode("secret");

        assertThat(hash).isNotBlank();
        assertThat(enc.matches("secret", hash)).isTrue();
        assertThat(enc.matches("wrong", hash)).isFalse();
    }
}
