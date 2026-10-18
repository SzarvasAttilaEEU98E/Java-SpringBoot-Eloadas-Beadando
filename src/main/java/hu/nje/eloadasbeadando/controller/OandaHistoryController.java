
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.model.OandaHistory;
import hu.nje.eloadasbeadando.service.OandaHistoryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class OandaHistoryController {

    private final OandaHistoryService oandaHistoryService;

    // Konstruktor - Dependency Injection
    public OandaHistoryController(
            OandaHistoryService oandaHistoryService) {

        this.oandaHistoryService = oandaHistoryService;
    }

    // Történelmi Forex árfolyamok lekérdezése
    @GetMapping(
            value = "/forex/history",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<OandaHistory>> lekerTortenelmiArfolyamok(

            @RequestParam String devizapar,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate kezdoDatum,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate vegDatum

    ) throws IOException, InterruptedException {

        List<OandaHistory> arfolyamok =
                oandaHistoryService.lekerTortenelmiArfolyamok(
                        devizapar,
                        kezdoDatum,
                        vegDatum
                );

        return ResponseEntity.ok(arfolyamok);
    }
}
