
package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OandaPositionPageController {

    // Nyitott Forex pozíciók webes oldala
    @GetMapping("/forex/positions/page")
    public String poziciokOldal() {
        return "forex-positions";
    }
}
