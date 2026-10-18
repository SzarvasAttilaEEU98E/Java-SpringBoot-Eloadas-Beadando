
package hu.nje.eloadasbeadando.model;

import java.math.BigDecimal;

public class OandaPricing {

    private String instrument;
    private BigDecimal bid;
    private BigDecimal ask;
    private String time;
    private String status;
    private boolean tradeable;

    public OandaPricing(
            String instrument,
            BigDecimal bid,
            BigDecimal ask,
            String time,
            String status,
            boolean tradeable) {

        this.instrument = instrument;
        this.bid = bid;
        this.ask = ask;
        this.time = time;
        this.status = status;
        this.tradeable = tradeable;
    }

    public String getInstrument() {
        return instrument;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public boolean isTradeable() {
        return tradeable;
    }
}
