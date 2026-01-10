package com.meditrack.alerts.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AlertMessageTest {

    @Test
    void constructor_shouldTrimInput() {
        var msg = new AlertMessage("   Hallo Welt   ");
        assertThat(msg.value()).isEqualTo("Hallo Welt");
    }

    @Test
    void constructor_shouldRejectNull() {
        assertThatThrownBy(() -> new AlertMessage(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("darf nicht null");
    }

    @Test
    void constructor_shouldRejectBlank() {
        assertThatThrownBy(() -> new AlertMessage("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("darf nicht leer");
    }

    @Test
    void constructor_shouldRejectTooLong() {
        String tooLong = "a".repeat(301);
        assertThatThrownBy(() -> new AlertMessage(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximal 300");
    }
}
