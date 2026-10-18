
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.OandaPosition;
import hu.nje.eloadasbeadando.parser.OandaPositionResponseParser;
import hu.nje.eloadasbeadando.request.OandaPositionRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class OandaPositionService {

    private final OandaPositionRequest oandaPositionRequest;
    private final OandaPositionResponseParser parser;
    private final HttpClient httpClient;

    // Konstruktor - Dependency Injection
    public OandaPositionService(
            OandaPositionRequest oandaPositionRequest,
            OandaPositionResponseParser parser) {

        this.oandaPositionRequest = oandaPositionRequest;
        this.parser = parser;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Nyitott Forex pozíciók lekérdezése
    public List<OandaPosition> lekerNyitottPoziciok()
            throws IOException, InterruptedException {

        // HTTP-kérés összeállítása
        HttpRequest request =
                oandaPositionRequest.keszitNyitottPoziciokKeres();

        // HTTP-kérés elküldése az OANDA API-nak
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-válasz ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA pozíciólekérdezés sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // JSON-válasz feldolgozása Java-objektumokká
        return parser.feldolgoz(response.body());
    }
}
