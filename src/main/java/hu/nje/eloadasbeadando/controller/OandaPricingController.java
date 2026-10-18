
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.model.OandaPricing;
import hu.nje.eloadasbeadando.service.OandaPricingService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class OandaPricingController {

    private final OandaPricingService oandaPricingService;

    // Konstruktor - Dependency Injection
    public OandaPricingController(
            OandaPricingService oandaPricingService) {

        this.oandaPricingService = oandaPricingService;
    }

    // Aktuális Forex árfolyamok lekérdezése
    @GetMapping(
            value = "/forex/pricing",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<OandaPricing>> lekerAktualisArfolyamok()
            throws IOException, InterruptedException {

        List<OandaPricing> arfolyamok =
                oandaPricingService.lekerAktualisArfolyamok();

        return ResponseEntity.ok(arfolyamok);
    }
}
