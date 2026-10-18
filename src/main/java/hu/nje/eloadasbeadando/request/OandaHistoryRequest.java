
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Set;

@Component
public class OandaHistoryRequest {

    private static final Set<String> ENGEDÉLYEZETT_GRANULARITY =
            Set.of("M15", "H1", "H4", "D", "W");

    private final String apiUrl;
    private final String apiToken;

    public OandaHistoryRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    public HttpRequest keszitTortenelmiKeres(
            String devizapar,
            String granularity) {

        if (devizapar == null ||
                !devizapar.matches("[A-Z]{3}_[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Érvénytelen devizapár!"
            );
        }

        if (!ENGEDÉLYEZETT_GRANULARITY.contains(granularity)) {
            throw new IllegalArgumentException(
                    "Érvénytelen Granularity érték!"
            );
        }

        String url = apiUrl
                + "/v3/instruments/"
                + devizapar
                + "/candles?price=M"
                + "&granularity=" + granularity
                + "&count=10";

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
