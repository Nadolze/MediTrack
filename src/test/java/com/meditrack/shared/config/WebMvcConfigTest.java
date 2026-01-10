package com.meditrack.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
