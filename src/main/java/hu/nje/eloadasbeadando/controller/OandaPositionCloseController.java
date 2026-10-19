
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.service.OandaPositionCloseService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OandaPositionCloseController {

    private final OandaPositionCloseService oandaPositionCloseService;

    // Konstruktor - Dependency Injection
    public OandaPositionCloseController(
            OandaPositionCloseService oandaPositionCloseService) {

        this.oandaPositionCloseService = oandaPositionCloseService;
    }

    // Meglévő funkció: Long pozíció lezárása devizapár alapján
    @PutMapping(
            value = "/forex/positions/close/long",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> lezarLongPozicio(
            @RequestParam String devizapar)
            throws IOException, InterruptedException {

        String jsonValasz =
                oandaPositionCloseService.lezarPozicio(
                        devizapar,
                        "long"
                );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonValasz);
    }

    // Meglévő funkció: Short pozíció lezárása devizapár alapján
    @PutMapping(
            value = "/forex/positions/close/short",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> lezarShortPozicio(
            @RequestParam String devizapar)
            throws IOException, InterruptedException {

        String jsonValasz =
                oandaPositionCloseService.lezarPozicio(
                        devizapar,
                        "short"
                );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonValasz);
    }

    // ÚJ FUNKCIÓ: konkrét ügylet lezárása tradeId alapján
    @PutMapping(
            value = "/forex/trades/{tradeId}/close",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> lezarTrade(
            @PathVariable long tradeId)
            throws IOException, InterruptedException {

        // A tradeId csak pozitív egész szám lehet
        if (tradeId <= 0) {
            throw new IllegalArgumentException(
                    "A tradeId csak pozitív egész szám lehet."
            );
        }

        // A Service végrehajtja az OANDA API-kérést
        String jsonValasz =
                oandaPositionCloseService.lezarTrade(tradeId);

        // Az OANDA JSON-válaszát visszaadjuk a böngészőnek
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonValasz);
    }
}
