
package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class OandaPricingRequest {

    private final String apiUrl;
    private final String apiToken;
    private final String accountId;

    public OandaPricingRequest(
            @Value("${oanda.api.url}") String apiUrl,
            @Value("${oanda.api.token}") String apiToken,
            @Value("${oanda.account.id}") String accountId) {

        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
        this.accountId = accountId;
    }

    public HttpRequest keszitArfolyamKeres() {

        String url = apiUrl
                + "/v3/accounts/"
                + accountId
                + "/pricing?instruments=EUR_USD,GBP_USD";

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
