
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDate;

@Component
public class OandaHistoryRequest {

    private final String apiUrl;
    private final String apiToken;

    public OandaHistoryRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    // Történelmi árfolyamlekérdezés összeállítása
    public HttpRequest keszitTortenelmiKeres(
            String devizapar,
            LocalDate kezdoDatum,
            LocalDate vegDatum) {

        // Csak szabályos OANDA devizapár-formátumot fogadunk el
        if (devizapar == null ||
                !devizapar.matches("[A-Z]{3}_[A-Z]{3}")) {

            throw new IllegalArgumentException(
                    "Érvénytelen devizapár."
            );
        }

        if (kezdoDatum == null ||
                vegDatum == null ||
                kezdoDatum.isAfter(vegDatum)) {

            throw new IllegalArgumentException(
                    "Érvénytelen dátumtartomány."
            );
        }

        // A kezdődátum 00:00 UTC időpontja
        String from = kezdoDatum + "T00:00:00Z";

        // A végdátumot is tartalmazó intervallum
        String to = vegDatum.plusDays(1) + "T00:00:00Z";

        String url = apiUrl
                + "/v3/instruments/"
                + devizapar
                + "/candles"
                + "?price=M"
                + "&granularity=D"
                + "&from=" + from
                + "&to=" + to;

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
