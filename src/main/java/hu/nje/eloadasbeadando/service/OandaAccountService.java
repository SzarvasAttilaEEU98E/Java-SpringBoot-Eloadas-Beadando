
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.OandaAccount;
import hu.nje.eloadasbeadando.parser.OandaAccountResponseParser;
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
    private final OandaAccountResponseParser parser;

    public OandaAccountService(
            OandaAccountRequest oandaAccountRequest,
            OandaAccountResponseParser parser) {

        this.httpClient = HttpClient.newHttpClient();
        this.oandaAccountRequest = oandaAccountRequest;
        this.parser = parser;
    }

    // Gyakorló számlák listájának lekérdezése
    public String lekerSzamlak()
            throws IOException, InterruptedException {

        HttpRequest request = oandaAccountRequest.keszitSzamlaKeres();

        return kuldKeres(request);
    }

    // Részletes számlaadatok lekérdezése és feldolgozása
    public OandaAccount lekerSzamlaOsszesito() throws Exception {

        HttpRequest request =
                oandaAccountRequest.keszitSzamlaOsszesitoKeres();

        String jsonValasz = kuldKeres(request);

        return parser.feldolgoz(jsonValasz);
    }

    // Közös HTTP-kérés küldő metódus
    private String kuldKeres(HttpRequest request)
            throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az OANDA API-kérés sikertelen. HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        return response.body();
    }
}
