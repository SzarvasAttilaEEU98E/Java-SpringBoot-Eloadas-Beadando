
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

    // Meglévő funkció: nyitott Forex pozíciók lekérdezése
    public List<OandaPosition> lekerNyitottPoziciok()
            throws IOException, InterruptedException {

        HttpRequest request =
                oandaPositionRequest.keszitNyitottPoziciokKeres();

        String jsonValasz = elkuldLekeres(
                request,
                "pozíciólekérdezés"
        );

        // A meglévő Parser feldolgozza a pozíciókat
        return parser.feldolgoz(jsonValasz);
    }

    // ÚJ FUNKCIÓ: nyitott Forex ügyletek lekérdezése
    public String lekerNyitottTradek()
            throws IOException, InterruptedException {

        HttpRequest request =
                oandaPositionRequest.keszitNyitottTradeKeres();

        // Egyelőre a teljes OANDA JSON-választ adjuk vissza.
        // A következő lépésben ebből készítünk táblázatot.
        return elkuldLekeres(
                request,
                "ügyletlekérdezés"
        );
    }

    // Közös HTTP-kérés küldése és válaszellenőrzés
    private String elkuldLekeres(
            HttpRequest request,
            String muvelet)
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
                    "Az OANDA " + muvelet + " sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        return response.body();
    }
}
