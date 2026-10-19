
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

        if (units <= 0) {
            throw new IllegalArgumentException(
                    "Long megbízásnál pozitív egységszám szükséges."
            );
        }

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

        if (units <= 0) {
            throw new IllegalArgumentException(
                    "Short megbízásnál pozitív egységszám szükséges."
            );
        }

        String valasz = oandaOrderService.kuldPiaciMegbizas(
                devizapar,
                -units
        );

        return ResponseEntity.ok(valasz);
    }

    // ÚJ: előjeles mennyiség alapján történő pozíciónyitás
    @PostMapping(
            value = "/forex/order/signed",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> nyitElojelesPozicio(
            @RequestParam String devizapar,
            @RequestParam int units)
            throws IOException, InterruptedException {

        // A nulla nem érvényes, a maximális méret 100 egység
        if (units == 0 || units < -100 || units > 100) {
            throw new IllegalArgumentException(
                    "A mennyiség -100 és 100 között lehet, nulla kivételével."
            );
        }

        // Devizapár formátumának ellenőrzése
        if (devizapar == null ||
                !devizapar.matches("[A-Z]{3}_[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Érvénytelen devizapár."
            );
        }

        // A Service már kezeli az előjeles mennyiséget:
        // pozitív = Long, negatív = Short
        String valasz = oandaOrderService.kuldPiaciMegbizas(
                devizapar,
                units
        );

        return ResponseEntity.ok(valasz);
    }
}
