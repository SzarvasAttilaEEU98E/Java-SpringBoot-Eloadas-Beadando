package hu.nje.eloadasbeadando.controller;

import hu.nje.eloadasbeadando.service.MnbSoapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
public class MnbSoapController
{
    private final MnbSoapService mnbSoapService;

    public MnbSoapController(MnbSoapService mnbSoapService){
        this.mnbSoapService = mnbSoapService;
    }

    @GetMapping("/soap")
    public String lekerArfolyamok(
            @RequestParam String deviza,
            @RequestParam LocalDate kezdoDatum,
            @RequestParam LocalDate vegDatum
    ) throws Exception
    {
      return mnbSoapService.lekerArfolyamok(
              deviza,
              kezdoDatum,
              vegDatum
      );
    }
}
