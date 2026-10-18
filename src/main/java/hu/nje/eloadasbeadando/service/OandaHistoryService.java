
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.OandaHistory;
import hu.nje.eloadasbeadando.parser.OandaHistoryResponseParser;
import hu.nje.eloadasbeadando.request.OandaHistoryRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class OandaHistoryService {

    private final OandaHistoryRequest historyRequest;
    private final OandaHistoryResponseParser historyParser;
    private final HttpClient httpClient;

    public OandaHistoryService(
            OandaHistoryRequest historyRequest,
            OandaHistoryResponseParser historyParser) {

        this.historyRequest = historyRequest;
        this.historyParser = historyParser;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<OandaHistory> lekerTortenelmiArfolyamok(
            String devizapar,
            String granularity) {

        HttpRequest request = historyRequest.keszitTortenelmiKeres(
                devizapar,
                granularity
        );

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "OANDA API hiba. HTTP státusz: "
                                + response.statusCode()
                );
            }

            return historyParser.feldolgoz(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "A historikus árfolyamlekérdezés megszakadt.", e
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Nem sikerült kapcsolódni az OANDA API-hoz.", e
            );
        }
    }
}
