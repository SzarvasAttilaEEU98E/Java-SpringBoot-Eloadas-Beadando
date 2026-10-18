
package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OandaOrderPageController {

    // Forex kereskedési oldal megjelenítése
    @GetMapping("/forex/order/page")
    public String kereskedesiOldal() {

        return "forex-order";
    }
}
