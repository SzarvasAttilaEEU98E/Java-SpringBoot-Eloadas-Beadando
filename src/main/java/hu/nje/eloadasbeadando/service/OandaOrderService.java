
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.request.OandaOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OandaOrderService {

    private final OandaOrderRequest oandaOrderRequest;
    private final HttpClient httpClient;

    public OandaOrderService(
            OandaOrderRequest oandaOrderRequest,
            @Value("${oanda.api.url}") String apiUrl) {

        // Kizárólag az OANDA gyakorló API használható
        if (!"https://api-fxpractice.oanda.com".equals(apiUrl)) {
            throw new IllegalStateException(
                    "Kereskedési művelet csak OANDA Practice API-val engedélyezett."
            );
        }

        this.oandaOrderRequest = oandaOrderRequest;
        this.httpClient = HttpClient.newHttpClient();
    }

    // Piaci megbízás elküldése az OANDA API-nak
    public String kuldPiaciMegbizas(
            String devizapar,
            int units)
            throws IOException, InterruptedException {

        // Csak kis méretű demo megbízásokat engedünk
        if (units == 0 || Math.abs((long) units) > 100) {
            throw new IllegalArgumentException(
                    "Az egységek száma -100 és 100 között lehet, nulla kivételével."
            );
        }

        // HTTP-kérés összeállítása
        HttpRequest request =
                oandaOrderRequest.keszitPiaciMegbizas(
                        devizapar,
                        units
                );

        // HTTP-kérés elküldése az OANDA API-nak
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // HTTP-válasz ellenőrzése
        if (response.statusCode() != 201) {
            throw new IOException(
                    "Az OANDA megbízás feldolgozása sikertelen. "
                            + "HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        return response.body();
    }
}
