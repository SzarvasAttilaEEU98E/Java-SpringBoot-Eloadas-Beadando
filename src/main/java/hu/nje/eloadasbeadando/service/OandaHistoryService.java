
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.OandaHistory;
import hu.nje.eloadasbeadando.parser.OandaHistoryResponseParser;
import hu.nje.eloadasbeadando.request.OandaHistoryRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@Service
public class OandaHistoryService {

    private final OandaHistoryRequest oandaHistoryRequest;
    private final OandaHistoryResponseParser parser;
    private final HttpClient httpClient;

    // Konstruktor - Dependency Injection
    public OandaHistoryService(
            OandaHistoryRequest oandaHistoryRequest,
            OandaHistoryResponseParser parser) {

        this.oandaHistoryRequest = oandaHistoryRequest;
        this.parser = parser;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Történelmi árfolyamok lekérdezése és feldolgozása
    public List<OandaHistory> lekerTortenelmiArfolyamok(
            String devizapar,
            LocalDate kezdoDatum,
            LocalDate vegDatum)
            throws IOException, InterruptedException {

        // HTTP-kérés összeállítása
        HttpRequest request =
                oandaHistoryRequest.keszitTortenelmiKeres(
                        devizapar,
                        kezdoDatum,
                        vegDatum
                );

        // HTTP-kérés elküldése
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-státuszkód ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA történelmi árfolyamlekérdezés sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // JSON-válasz feldolgozása Java-objektumokká
        return parser.feldolgoz(response.body());
    }
}
