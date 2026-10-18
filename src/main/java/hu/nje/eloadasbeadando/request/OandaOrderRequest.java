
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class OandaOrderRequest {

    private final String apiUrl;
    private final String apiToken;
    private final String accountId;

    // Konstruktor - konfigurációs értékek beolvasása
    public OandaOrderRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken,
            @Value("${oanda.account.id}") String accountId) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.accountId = accountId;
    }

    // Piaci megbízás HTTP-kérésének összeállítása
    public HttpRequest keszitPiaciMegbizas(
            String devizapar,
            int units) {

        // Devizapár ellenőrzése
        if (devizapar == null ||
                !devizapar.matches("[A-Z]{3}_[A-Z]{3}")) {

            throw new IllegalArgumentException(
                    "Érvénytelen devizapár."
            );
        }

        // Egységek számának ellenőrzése
        if (units == 0) {
            throw new IllegalArgumentException(
                    "A kereskedési egységek száma nem lehet nulla."
            );
        }

        // OANDA REST API végpont
        String url = apiUrl
                + "/v3/accounts/"
                + accountId
                + "/orders";

        // Piaci megbízás JSON-törzse
        String jsonBody = """
                {
                    "order": {
                        "type": "MARKET",
                        "instrument": "%s",
                        "units": "%d",
                        "timeInForce": "FOK",
                        "positionFill": "DEFAULT"
                    }
                }
                """.formatted(devizapar, units);

        // HTTP POST-kérés összeállítása
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }
}
