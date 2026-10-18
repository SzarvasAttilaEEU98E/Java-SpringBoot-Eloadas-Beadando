
package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OandaPricingPageController {

    @GetMapping("/forex/pricing/page")
    public String megjelenitOldal() {

        return "forex-pricing";
    }
}
