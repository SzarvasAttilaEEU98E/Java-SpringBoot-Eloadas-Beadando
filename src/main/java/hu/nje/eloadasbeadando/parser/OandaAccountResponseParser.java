
package hu.nje.eloadasbeadando.parser;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import hu.nje.eloadasbeadando.model.OandaAccount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OandaAccountResponseParser {

    private final ObjectMapper objectMapper;

    public OandaAccountResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OandaAccount feldolgoz(String jsonValasz) throws Exception {

        // A teljes JSON-válasz feldolgozása
        JsonNode root = objectMapper.readTree(jsonValasz);

        // Az "account" objektum kiválasztása
        JsonNode account = root.path("account");

        if (account.isMissingNode() || account.isNull()) {
            throw new IllegalArgumentException(
                    "Az OANDA válasz nem tartalmaz account objektumot."
            );
        }

        // Számla alapadatai
        String id = account.path("id").asText();
        String currency = account.path("currency").asText();

        // Pénzügyi adatok
        BigDecimal balance = new BigDecimal(
                account.path("balance").asText()
        );

        BigDecimal nav = new BigDecimal(
                account.path("NAV").asText()
        );

        BigDecimal marginAvailable = new BigDecimal(
                account.path("marginAvailable").asText()
        );

        // Nyitott ügyletek és pozíciók száma
        int openTradeCount = account.path("openTradeCount").asInt();
        int openPositionCount = account.path("openPositionCount").asInt();

        // Java-modell létrehozása
        return new OandaAccount(
                id,
                currency,
                balance,
                nav,
                marginAvailable,
                openTradeCount,
                openPositionCount
        );
    }
}
