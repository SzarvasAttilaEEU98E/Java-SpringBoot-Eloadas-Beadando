
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.model.MnbSoapArfolyam;
import hu.nje.eloadasbeadando.parser.MnbSoapResponseParser;
import hu.nje.eloadasbeadando.request.MnbSoapRequest;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@Service
public class MnbSoapService {

    // HTTP kliens az MNB webszolgáltatás eléréséhez
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // SOAP-kérés összeállításáért felelős osztály
    private final MnbSoapRequest mnbSoapRequest = new MnbSoapRequest();

    // SOAP XML-válasz feldolgozásáért felelős osztály
    private final MnbSoapResponseParser parser = new MnbSoapResponseParser();

    public String lekerArfolyamok(
            String deviza,
            LocalDate kezdoDatum,
            LocalDate vegDatum) throws Exception {

        // 1. SOAP HTTP-kérés összeállítása
        HttpRequest request = mnbSoapRequest.keszitArfolyamKeres(
                deviza,
                kezdoDatum,
                vegDatum
        );

        System.out.println("MNB kérés URL: " + request.uri());

        System.out.println("SOAPAction: " +
                request.headers()
                        .firstValue("SOAPAction")
                        .orElse("hiányzik")
        );

        // 2. HTTP-kérés elküldése és a válasz fogadása
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("MNB válasz státuszkód: " + response.statusCode());

        // 3. HTTP-státuszkód ellenőrzése
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Az MNB SOAP kérés sikertelen. HTTP státuszkód: "
                            + response.statusCode()
            );
        }

        // 4. SOAP XML feldolgozása a Parser segítségével
        List<MnbSoapArfolyam> arfolyamok = parser.feldolgoz(
                response.body()
        );

        // 5. Feldolgozott árfolyamok kiírása teszteléshez
        System.out.println(
                "Feldolgozott árfolyamok száma: " + arfolyamok.size()
        );

        for (MnbSoapArfolyam arfolyam : arfolyamok) {

            System.out.println(
                    arfolyam.getDatum() + " | " +
                            arfolyam.getDeviza() + " | " +
                            arfolyam.getArfolyam()
            );
        }

        // 6. Egyelőre visszaadjuk az eredeti SOAP XML-választ
        return response.body();
    }
}
