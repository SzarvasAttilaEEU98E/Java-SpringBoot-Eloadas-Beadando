
package hu.nje.eloadasbeadando.service;

import hu.nje.eloadasbeadando.request.MnbSoapRequest;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.io.IOException;
import java.net.http.HttpResponse;

@Service
public class MnbSoapService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final MnbSoapRequest mnbSoapRequest = new MnbSoapRequest();

    public String lekerArfolyamok(String deviza, LocalDate kezdoDatum, LocalDate vegDatum) throws IOException, InterruptedException
            {
                HttpRequest request = mnbSoapRequest.keszitArfolyamKeres(
                        deviza,
                        kezdoDatum,
                        vegDatum
                );
                // ------------------
                System.out.println("MNB kérés URL: " + request.uri());
                System.out.println("SOAPAction: "
                        + request.headers().firstValue("SOAPAction").orElse("hiányzik"));
                //--------------------

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("MNB válasz státuszkód: " + response.statusCode());
                System.out.println("MNB válasz végső URL: " + response.uri());
                System.out.println("MNB válasz törzse: " + response.body());

                // TESZT -------------------------------------------
                System.out.println("MNB HTTP státuszkód: " + response.statusCode());
                if (response.statusCode() != 200) {
                    throw new IOException(
                            "Az MNB SOAP kérés sikertelen. HTTP státuszkód: "
                                    + response.statusCode()
                    );
                }
                // TESZT --------------------------------------------

                return response.body();
            }
}
