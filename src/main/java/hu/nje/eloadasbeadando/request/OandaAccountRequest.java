
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class OandaAccountRequest {

    private final String apiUrl;
    private final String apiToken;
    private final String accountId;

    public OandaAccountRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken,
            @Value("${oanda.account.id}") String accountId) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.accountId = accountId;
    }

    // Gyakorló számlák listájának lekérdezése
    public HttpRequest keszitSzamlaKeres() {

        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/v3/accounts"))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    // Részletes számlaadatok lekérdezése
    public HttpRequest keszitSzamlaOsszesitoKeres() {

        return HttpRequest.newBuilder()
                .uri(URI.create(
                        apiUrl + "/v3/accounts/" + accountId + "/summary"
                ))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
