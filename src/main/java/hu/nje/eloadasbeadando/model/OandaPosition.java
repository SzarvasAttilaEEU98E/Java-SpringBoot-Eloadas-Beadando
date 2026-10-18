
package hu.nje.eloadasbeadando.model;

import java.math.BigDecimal;

public class OandaPosition {

    private final String instrument;
    private final BigDecimal longUnits;
    private final BigDecimal shortUnits;
    private final BigDecimal longUnrealizedPL;
    private final BigDecimal shortUnrealizedPL;
    private final BigDecimal unrealizedPL;

    public OandaPosition(
            String instrument,
            BigDecimal longUnits,
            BigDecimal shortUnits,
            BigDecimal longUnrealizedPL,
            BigDecimal shortUnrealizedPL,
            BigDecimal unrealizedPL) {

        this.instrument = instrument;
        this.longUnits = longUnits;
        this.shortUnits = shortUnits;
        this.longUnrealizedPL = longUnrealizedPL;
        this.shortUnrealizedPL = shortUnrealizedPL;
        this.unrealizedPL = unrealizedPL;
    }

    public String getInstrument() {
        return instrument;
    }

    public BigDecimal getLongUnits() {
        return longUnits;
    }

    public BigDecimal getShortUnits() {
        return shortUnits;
    }

    public BigDecimal getLongUnrealizedPL() {
        return longUnrealizedPL;
    }

    public BigDecimal getShortUnrealizedPL() {
        return shortUnrealizedPL;
    }

    public BigDecimal getUnrealizedPL() {
        return unrealizedPL;
    }
}
