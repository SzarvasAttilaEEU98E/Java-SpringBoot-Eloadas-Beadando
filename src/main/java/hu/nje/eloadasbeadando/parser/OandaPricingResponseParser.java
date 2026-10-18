
package hu.nje.eloadasbeadando.parser;

import hu.nje.eloadasbeadando.model.OandaPricing;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OandaPricingResponseParser {

    private final ObjectMapper objectMapper;

    public OandaPricingResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<OandaPricing> feldolgoz(String jsonValasz) {

        List<OandaPricing> arfolyamok = new ArrayList<>();

        JsonNode root = objectMapper.readTree(jsonValasz);

        // Az OANDA válasz prices tömbjének lekérése
        JsonNode prices = root.path("prices");

        if (!prices.isArray()) {
            throw new IllegalArgumentException(
                    "Az OANDA válasz nem tartalmaz prices tömböt."
            );
        }

        // Végigmegyünk a devizapárokon
        for (JsonNode price : prices) {

            String instrument =
                    price.path("instrument").asText();

            // A legjobb Bid ár a bids tömb első eleme
            JsonNode bids = price.path("bids");

            BigDecimal bid = null;

            if (bids.isArray() && !bids.isEmpty()) {
                bid = new BigDecimal(
                        bids.get(0).path("price").asText()
                );
            }

            // A legjobb Ask ár az asks tömb első eleme
            JsonNode asks = price.path("asks");

            BigDecimal ask = null;

            if (asks.isArray() && !asks.isEmpty()) {
                ask = new BigDecimal(
                        asks.get(0).path("price").asText()
                );
            }

            String time =
                    price.path("time").asText();

            String status =
                    price.path("status").asText();

            boolean tradeable =
                    price.path("tradeable").asBoolean();

            // Saját Java-modell létrehozása
            OandaPricing arfolyam = new OandaPricing(
                    instrument,
                    bid,
                    ask,
                    time,
                    status,
                    tradeable
            );

            arfolyamok.add(arfolyam);
        }

        return arfolyamok;
    }
}
