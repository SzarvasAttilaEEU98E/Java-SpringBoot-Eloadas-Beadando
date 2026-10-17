
package hu.nje.eloadasbeadando.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MnbSoapArfolyam {

    private LocalDate datum;
    private String deviza;
    private BigDecimal arfolyam;

    public MnbSoapArfolyam(LocalDate datum, String deviza, BigDecimal arfolyam) {
        this.datum = datum;
        this.deviza = deviza;
        this.arfolyam = arfolyam;
    }

    public LocalDate getDatum() {

        return datum;
    }

    public String getDeviza() {

        return deviza;
    }

    public BigDecimal getArfolyam() {

        return arfolyam;
    }
}
