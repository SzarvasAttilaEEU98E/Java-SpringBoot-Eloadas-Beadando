
package hu.nje.eloadasbeadando.request;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDate;

public class MnbSoapRequest {

    private static final String MNB_SOAP_URL =
            "http://www.mnb.hu/arfolyamok.asmx";

    public HttpRequest keszitArfolyamKeres(
            String deviza,
            LocalDate kezdoDatum,
            LocalDate vegDatum) {

        String soapXml = """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                    <soap:Body>
                        <GetExchangeRates xmlns="http://www.mnb.hu/webservices/">
                            <startDate>%s</startDate>
                            <endDate>%s</endDate>
                            <currencyNames>%s</currencyNames>
                        </GetExchangeRates>
                    </soap:Body>
                </soap:Envelope>
                """.formatted(kezdoDatum, vegDatum, deviza);

        return HttpRequest.newBuilder()
                .uri(URI.create(MNB_SOAP_URL))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://www.mnb.hu/webservices/MNBArfolyamServiceSoap/GetExchangeRates")
                .POST(HttpRequest.BodyPublishers.ofString(soapXml))
                .build();
    }
}
