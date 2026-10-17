
package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.service.OandaAccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OandaAccountController {

    private final OandaAccountService oandaAccountService;

    public OandaAccountController(
            OandaAccountService oandaAccountService) {

        this.oandaAccountService = oandaAccountService;
    }

    @GetMapping(value = "/forex/account",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> lekerSzamlak()
            throws IOException, InterruptedException {

        String jsonValasz = oandaAccountService.lekerSzamlak();

        return ResponseEntity.ok(jsonValasz);
    }
}
