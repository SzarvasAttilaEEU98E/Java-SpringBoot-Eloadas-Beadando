
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.service.OandaPositionCloseService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // Long pozíció lezárása
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

    // Short pozíció lezárása
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
}
