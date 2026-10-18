
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.request.OandaPositionCloseRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OandaPositionCloseService {

    private final OandaPositionCloseRequest oandaPositionCloseRequest;
    private final HttpClient httpClient;
    private final String apiUrl;

    // Konstruktor - Dependency Injection
    public OandaPositionCloseService(
            OandaPositionCloseRequest oandaPositionCloseRequest,
            @Value("${oanda.api.url}") String apiUrl) {

        this.oandaPositionCloseRequest = oandaPositionCloseRequest;
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Long vagy Short Forex pozíció lezárása
    public String lezarPozicio(String devizapar, String irany)
            throws IOException, InterruptedException {

        // Biztonsági ellenőrzés:
        // kizárólag OANDA Practice környezetben engedélyezett
        if (!"https://api-fxpractice.oanda.com".equals(apiUrl)) {
            throw new IllegalStateException(
                    "Pozíciózárás csak OANDA Practice környezetben engedélyezett."
            );
        }

        // HTTP PUT-kérés összeállítása
        HttpRequest request =
                oandaPositionCloseRequest.keszitPozicioZaras(
                        devizapar,
                        irany
                );

        // HTTP-kérés elküldése az OANDA API-nak
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-válasz ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA pozíciózárás sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // Az OANDA JSON-válaszának visszaadása
        return response.body();
    }
}
