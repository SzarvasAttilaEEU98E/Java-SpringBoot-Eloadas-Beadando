package hu.nje.eloadasbeadando.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class MnbSoapPageController {

    @GetMapping("/soap/page")
    public String megjelenitOldal() {

        return "soap";
    }
}
