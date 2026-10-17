package hu.nje.eloadasbeadando.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;

@Component
public class OandaAccountRequest {

    private final String apiUrl;
    private final String apiToken;


    public OandaAccountRequest(@Value("${oanda.api.url}") String apiUrl, @Value("${oanda.api.token}") String apiToken)
    {
        this.apiUrl = apiUrl;
        this.apiToken = apiToken;
    }

    public HttpRequest keszitSzamlaKeres() {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/v3/accounts"))
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
    }
}
