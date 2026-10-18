
package hu.nje.eloadasbeadando.model;

import java.math.BigDecimal;

public class OandaAccount {

    private String id;
    private String currency;
    private BigDecimal balance;
    private BigDecimal nav;
    private BigDecimal marginAvailable;
    private int openTradeCount;
    private int openPositionCount;

    public OandaAccount(
            String id,
            String currency,
            BigDecimal balance,
            BigDecimal nav,
            BigDecimal marginAvailable,
            int openTradeCount,
            int openPositionCount) {

        this.id = id;
        this.currency = currency;
        this.balance = balance;
        this.nav = nav;
        this.marginAvailable = marginAvailable;
        this.openTradeCount = openTradeCount;
        this.openPositionCount = openPositionCount;
    }

    public String getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public BigDecimal getMarginAvailable() {
        return marginAvailable;
    }

    public int getOpenTradeCount() {
        return openTradeCount;
    }

    public int getOpenPositionCount() {
        return openPositionCount;
    }
}
