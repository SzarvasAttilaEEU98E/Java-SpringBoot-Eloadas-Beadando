
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.service.OandaOrderService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OandaOrderController {

    private final OandaOrderService oandaOrderService;

    // Konstruktor - Dependency Injection
    public OandaOrderController(
            OandaOrderService oandaOrderService) {

        this.oandaOrderService = oandaOrderService;
    }

    // Long (vételi) megbízás
    @PostMapping(
            value = "/forex/order/long",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> nyitLongPozicio(
            @RequestParam String devizapar,
            @RequestParam int units)
            throws IOException, InterruptedException {

        // Long esetén pozitív egységszám szükséges
        if (units <= 0) {
            throw new IllegalArgumentException(
                    "Long megbízásnál pozitív egységszám szükséges."
            );
        }

        // Pozitív egységszám továbbítása a Service-nek
        String valasz = oandaOrderService.kuldPiaciMegbizas(
                devizapar,
                units
        );

        return ResponseEntity.ok(valasz);
    }

    // Short (eladási) megbízás
    @PostMapping(
            value = "/forex/order/short",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> nyitShortPozicio(
            @RequestParam String devizapar,
            @RequestParam int units)
            throws IOException, InterruptedException {

        // A felhasználó pozitív mennyiséget ad meg
        if (units <= 0) {
            throw new IllegalArgumentException(
                    "Short megbízásnál pozitív egységszám szükséges."
            );
        }

        // Negatív egységszám továbbítása a Service-nek
        String valasz = oandaOrderService.kuldPiaciMegbizas(
                devizapar,
                -units
        );

        return ResponseEntity.ok(valasz);
    }
}
