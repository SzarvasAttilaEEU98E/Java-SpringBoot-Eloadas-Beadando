
package hu.nje.eloadasbeadando.parser;

import hu.nje.eloadasbeadando.model.OandaHistory;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OandaHistoryResponseParser {

    private final ObjectMapper objectMapper;

    public OandaHistoryResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // OANDA történelmi árfolyamok JSON-válaszának feldolgozása
    public List<OandaHistory> feldolgoz(String jsonValasz) {

        List<OandaHistory> tortenelmiArfolyamok = new ArrayList<>();

        // JSON szöveg átalakítása JsonNode objektummá
        JsonNode root = objectMapper.readTree(jsonValasz);

        // A candles tömb megkeresése
        JsonNode candles = root.path("candles");

        if (!candles.isArray()) {
            throw new IllegalArgumentException(
                    "Az OANDA válasz nem tartalmaz candles tömböt."
            );
        }

        // Végigmegyünk a gyertyaadatokon
        for (JsonNode candle : candles) {

            String time = candle.path("time").asText();

            long volume = candle.path("volume").asLong();

            boolean complete = candle.path("complete").asBoolean();

            // A mid objektum tartalmazza az OHLC adatokat
            JsonNode mid = candle.path("mid");

            if (mid.isMissingNode() || mid.isNull()) {
                throw new IllegalArgumentException(
                        "Hiányzó mid árfolyamadat az OANDA válaszban."
                );
            }

            BigDecimal open = new BigDecimal(
                    mid.path("o").asText()
            );

            BigDecimal high = new BigDecimal(
                    mid.path("h").asText()
            );

            BigDecimal low = new BigDecimal(
                    mid.path("l").asText()
            );

            BigDecimal close = new BigDecimal(
                    mid.path("c").asText()
            );

            // Saját Java-modell létrehozása
            OandaHistory arfolyam = new OandaHistory(
                    time,
                    open,
                    high,
                    low,
                    close,
                    volume,
                    complete
            );

            tortenelmiArfolyamok.add(arfolyam);
        }

        return tortenelmiArfolyamok;
    }
}
