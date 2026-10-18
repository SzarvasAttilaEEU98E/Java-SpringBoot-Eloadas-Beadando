
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.model.OandaPosition;
import hu.nje.eloadasbeadando.service.OandaPositionService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class OandaPositionController {

    private final OandaPositionService oandaPositionService;

    // Konstruktor - Dependency Injection
    public OandaPositionController(
            OandaPositionService oandaPositionService) {

        this.oandaPositionService = oandaPositionService;
    }

    // Nyitott Forex pozíciók lekérdezése
    @GetMapping(
            value = "/forex/positions",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<OandaPosition>> lekerNyitottPoziciok()
            throws IOException, InterruptedException {

        // Service meghívása
        List<OandaPosition> poziciok =
                oandaPositionService.lekerNyitottPoziciok();

        // Feldolgozott pozíciók visszaadása JSON formátumban
        return ResponseEntity.ok(poziciok);
    }
}
