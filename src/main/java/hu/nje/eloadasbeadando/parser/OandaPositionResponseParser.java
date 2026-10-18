
package hu.nje.eloadasbeadando.parser;

import hu.nje.eloadasbeadando.model.OandaPosition;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OandaPositionResponseParser {

    private final ObjectMapper objectMapper;

    public OandaPositionResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // OANDA nyitott pozíciók JSON-válaszának feldolgozása
    public List<OandaPosition> feldolgoz(String jsonValasz) {

        List<OandaPosition> poziciok = new ArrayList<>();

        // JSON szöveg feldolgozása
        JsonNode root = objectMapper.readTree(jsonValasz);

        // Pozíciók tömbjének megkeresése
        JsonNode positions = root.path("positions");

        if (!positions.isArray()) {
            throw new IllegalArgumentException(
                    "Az OANDA válasz nem tartalmaz positions tömböt."
            );
        }

        // Végigmegyünk a nyitott pozíciókon
        for (JsonNode position : positions) {

            String instrument =
                    position.path("instrument").asText();

            // Long és Short oldal
            JsonNode longSide = position.path("long");
            JsonNode shortSide = position.path("short");

            BigDecimal longUnits = new BigDecimal(
                    longSide.path("units").asText("0")
            );

            BigDecimal shortUnits = new BigDecimal(
                    shortSide.path("units").asText("0")
            );

            BigDecimal longUnrealizedPL = new BigDecimal(
                    longSide.path("unrealizedPL").asText("0")
            );

            BigDecimal shortUnrealizedPL = new BigDecimal(
                    shortSide.path("unrealizedPL").asText("0")
            );

            BigDecimal unrealizedPL = new BigDecimal(
                    position.path("unrealizedPL").asText("0")
            );

            // Saját Java-modell létrehozása
            OandaPosition pozicio = new OandaPosition(
                    instrument,
                    longUnits,
                    shortUnits,
                    longUnrealizedPL,
                    shortUnrealizedPL,
                    unrealizedPL
            );

            poziciok.add(pozicio);
        }

        return poziciok;
    }
}
