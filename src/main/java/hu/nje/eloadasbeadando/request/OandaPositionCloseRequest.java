
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class OandaPositionCloseRequest {

    private static final String PRACTICE_API =
            "https://api-fxpractice.oanda.com";

    private final String apiUrl;
    private final String apiToken;
    private final String accountId;

    public OandaPositionCloseRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken,
            @Value("${oanda.account.id}") String accountId) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.accountId = accountId;
    }

    // Ellenőrizzük, hogy kizárólag gyakorlószámlán dolgozunk
    private void ellenorizPracticeKornyezet() {

        if (!PRACTICE_API.equals(apiUrl)) {
            throw new IllegalStateException(
                    "Pozíciózárás csak OANDA Practice környezetben engedélyezett."
            );
        }
    }

    // Meglévő funkció: Long vagy Short pozíció lezárása devizapár alapján
    public HttpRequest keszitPozicioZaras(
            String devizapar,
            String irany) {

        ellenorizPracticeKornyezet();

        if (devizapar == null ||
                !devizapar.matches("[A-Z]{3}_[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Érvénytelen devizapár."
            );
        }

        String jsonBody;

        if ("long".equalsIgnoreCase(irany)) {
            jsonBody = "{\"longUnits\":\"ALL\"}";

        } else if ("short".equalsIgnoreCase(irany)) {
            jsonBody = "{\"shortUnits\":\"ALL\"}";

        } else {
            throw new IllegalArgumentException(
                    "Az irány csak long vagy short lehet."
            );
        }

        String url = apiUrl
                + "/v3/accounts/"
                + accountId
                + "/positions/"
                + devizapar
                + "/close";

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    // ÚJ FUNKCIÓ: konkrét ügylet lezárása tradeId alapján
    public HttpRequest keszitTradeZaras(long tradeId) {

        ellenorizPracticeKornyezet();

        if (tradeId <= 0) {
            throw new IllegalArgumentException(
                    "A tradeId csak pozitív egész szám lehet."
            );
        }

        String url = apiUrl
                + "/v3/accounts/"
                + accountId
                + "/trades/"
                + tradeId
                + "/close";

        // Az ALL minden egységet lezár az adott ügyletből
        String jsonBody = "{\"units\":\"ALL\"}";

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }
}
