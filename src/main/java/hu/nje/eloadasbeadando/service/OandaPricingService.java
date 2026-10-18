
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.OandaPricing;
import hu.nje.eloadasbeadando.parser.OandaPricingResponseParser;
import hu.nje.eloadasbeadando.request.OandaPricingRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class OandaPricingService {

    private final OandaPricingRequest oandaPricingRequest;
    private final OandaPricingResponseParser parser;
    private final HttpClient httpClient;

    // Konstruktor - Dependency Injection
    public OandaPricingService(
            OandaPricingRequest oandaPricingRequest,
            OandaPricingResponseParser parser) {

        this.oandaPricingRequest = oandaPricingRequest;
        this.parser = parser;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Aktuális Forex árfolyamok lekérdezése és feldolgozása
    public List<OandaPricing> lekerAktualisArfolyamok()
            throws IOException, InterruptedException {

        // HTTP-kérés összeállítása
        HttpRequest request =
                oandaPricingRequest.keszitArfolyamKeres();

        // HTTP-kérés elküldése
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-válasz ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA árfolyamlekérdezés sikertelen. HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // JSON-válasz feldolgozása saját Java-objektumokká
        return parser.feldolgoz(response.body());
    }
}
