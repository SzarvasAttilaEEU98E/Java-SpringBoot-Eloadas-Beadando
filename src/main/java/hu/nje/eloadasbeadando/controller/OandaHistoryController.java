
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.model.OandaHistory;
import hu.nje.eloadasbeadando.service.OandaHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/forex/history")
public class OandaHistoryController {

    private final OandaHistoryService historyService;

    public OandaHistoryController(
            OandaHistoryService historyService) {

        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<OandaHistory>> lekerTortenelmiArfolyamok(
            @RequestParam(defaultValue = "EUR_USD") String devizapar,
            @RequestParam(defaultValue = "D") String granularity) {

        List<OandaHistory> arfolyamok =
                historyService.lekerTortenelmiArfolyamok(
                        devizapar,
                        granularity
                );

        return ResponseEntity.ok(arfolyamok);
    }
}
