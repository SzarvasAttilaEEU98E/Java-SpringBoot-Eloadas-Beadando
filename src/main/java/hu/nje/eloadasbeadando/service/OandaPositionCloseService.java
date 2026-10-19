
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

    private static final String PRACTICE_API =
            "https://api-fxpractice.oanda.com";

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

    // Ellenőrizzük, hogy csak OANDA Practice API-t használunk
    private void ellenorizPracticeKornyezet() {

        if (!PRACTICE_API.equals(apiUrl)) {
            throw new IllegalStateException(
                    "Pozíciózárás csak OANDA Practice környezetben engedélyezett."
            );
        }
    }

    // Meglévő funkció: Long vagy Short pozíció lezárása
    public String lezarPozicio(
            String devizapar,
            String irany)
            throws IOException, InterruptedException {

        ellenorizPracticeKornyezet();

        HttpRequest request =
                oandaPositionCloseRequest.keszitPozicioZaras(
                        devizapar,
                        irany
                );

        return elkuldZarasiKeres(request);
    }

    // ÚJ FUNKCIÓ: ügylet lezárása tradeId alapján
    public String lezarTrade(long tradeId)
            throws IOException, InterruptedException {

        ellenorizPracticeKornyezet();

        HttpRequest request =
                oandaPositionCloseRequest.keszitTradeZaras(tradeId);

        return elkuldZarasiKeres(request);
    }

    // Közös HTTP-küldési és válaszellenőrzési logika
    private String elkuldZarasiKeres(HttpRequest request)
            throws IOException, InterruptedException {

        HttpResponse<String> response;

        try {
            response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA pozíciózárás sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        return response.body();
    }
}
