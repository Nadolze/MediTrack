package com.meditrack.vitals.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit-Tests für Threshold.
 */
class ThresholdTest {

    @Test
    @DisplayName("Threshold kann ohne Grenzen erstellt werden (null/null)")
    void threshold_canBeCreatedWithNoBounds() {
        Threshold t = new Threshold(null, null);
        assertThat(t.minInclusive()).isNull();
        assertThat(t.maxInclusive()).isNull();

        // Ohne Grenzen ist weder below noch above true
        assertThat(t.isBelow(0)).isFalse();
        assertThat(t.isAbove(0)).isFalse();
    }

    @Test
    @DisplayName("Threshold mit nur max prüft 'above' korrekt")
    void threshold_onlyMax_checksAbove() {
        Threshold t = new Threshold(null, 110.0);

        assertThat(t.minInclusive()).isNull();
        assertThat(t.maxInclusive()).isEqualTo(110.0);

        assertThat(t.isAbove(111)).isTrue();
        assertThat(t.isAbove(110)).isFalse();
        assertThat(t.isAbove(0)).isFalse();

        // below ist ohne min nie true
        assertThat(t.isBelow(0)).isFalse();
    }

    @Test
    @DisplayName("Threshold mit nur min prüft 'below' korrekt")
    void threshold_onlyMin_checksBelow() {
        Threshold t = new Threshold(92.0, null);

        assertThat(t.minInclusive()).isEqualTo(92.0);
        assertThat(t.maxInclusive()).isNull();

        assertThat(t.isBelow(91.9)).isTrue();
        assertThat(t.isBelow(92.0)).isFalse();
        assertThat(t.isBelow(200)).isFalse();

        // above ist ohne max nie true
        assertThat(t.isAbove(200)).isFalse();
    }

    @Test
    @DisplayName("minInclusive darf nicht größer als maxInclusive sein")
    void threshold_minMustNotBeGreaterThanMax() {
        assertThatThrownBy(() -> new Threshold(10.0, 5.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minInclusive darf nicht größer");
    }
}
