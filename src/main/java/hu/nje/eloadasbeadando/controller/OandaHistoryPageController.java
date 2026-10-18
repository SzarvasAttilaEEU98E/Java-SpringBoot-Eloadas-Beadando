
package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OandaHistoryPageController {

    @GetMapping("/forex/history/page")
    public String tortenelmiArfolyamOldal() {

        return "forex-history";
    }
}
