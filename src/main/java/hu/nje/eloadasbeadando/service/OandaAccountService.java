
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.request.OandaAccountRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OandaAccountService {

    private final HttpClient httpClient;
    private final OandaAccountRequest oandaAccountRequest;

    public OandaAccountService(OandaAccountRequest oandaAccountRequest) {
        this.httpClient = HttpClient.newHttpClient();
        this.oandaAccountRequest = oandaAccountRequest;
    }

    public String lekerSzamlak() throws IOException, InterruptedException {

        // HTTP-kérés összeállítása
        HttpRequest request = oandaAccountRequest.keszitSzamlaKeres();

        // HTTP-kérés elküldése az OANDA API felé
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-státuszkód ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA számlalekérdezés sikertelen. HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // Az OANDA JSON-válaszának visszaadása
        return response.body();
    }
}
