package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OandaAccountPageController {

    @GetMapping("/forex/account/page")
    public String megjelenitOldal() {

        return "forex-account";
    }
}
