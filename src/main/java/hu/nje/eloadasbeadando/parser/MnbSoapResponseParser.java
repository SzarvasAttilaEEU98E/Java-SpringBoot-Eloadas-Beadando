
package hu.nje.eloadasbeadando.parser;

import hu.nje.eloadasbeadando.model.MnbSoapArfolyam;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MnbSoapResponseParser {

    public List<MnbSoapArfolyam> feldolgoz(String soapValasz) throws Exception {

        // 1. XML-feldolgozó létrehozása Factory segítségével
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);

        // 2. Biztonsági beállítások
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true
        );

        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false
        );

        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false
        );

        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document dokumentum = builder.parse(
                new InputSource(new StringReader(soapValasz))
        );

        NodeList eredmenyek = dokumentum.getElementsByTagNameNS(
                "http://www.mnb.hu/webservices/",
                "GetExchangeRatesResult"
        );

        if (eredmenyek.getLength() == 0) {
            throw new IllegalArgumentException(
                    "Az MNB SOAP válasz nem tartalmaz GetExchangeRatesResult elemet."
            );
        }

        String arfolyamXml = eredmenyek.item(0).getTextContent();

        Document arfolyamDokumentum = builder.parse(
                new InputSource(new StringReader(arfolyamXml))
        );

        NodeList napok = arfolyamDokumentum.getElementsByTagName("Day");

        List<MnbSoapArfolyam> arfolyamLista = new ArrayList<>();

        for (int i = 0; i < napok.getLength(); i++) {

            Element nap = (Element) napok.item(i);

            String datumSzoveg = nap.getAttribute("date");

            LocalDate datum = LocalDate.parse(datumSzoveg);

            NodeList arfolyamok = nap.getElementsByTagName("Rate");

            for (int j = 0; j < arfolyamok.getLength(); j++) {

                Element arfolyamElem = (Element) arfolyamok.item(j);

                String deviza = arfolyamElem.getAttribute("curr");

                String arfolyamSzoveg = arfolyamElem.getTextContent();

                arfolyamSzoveg = arfolyamSzoveg.replace(",", ".");

                BigDecimal arfolyam = new BigDecimal(arfolyamSzoveg);

                String unitSzoveg = arfolyamElem.getAttribute("unit");

                BigDecimal unit = new BigDecimal(unitSzoveg);

                if (unit.signum() <= 0) {
                    throw new IllegalArgumentException(
                            "Érvénytelen árfolyamegység: " + unit
                    );
                }

                BigDecimal egysegreJutoArfolyam = arfolyam.divide(
                        unit,
                        MathContext.DECIMAL64
                );

                MnbSoapArfolyam mnbArfolyam = new MnbSoapArfolyam(
                        datum,
                        deviza,
                        egysegreJutoArfolyam
                );

                arfolyamLista.add(mnbArfolyam);
            }
        }

        return arfolyamLista;
    }
}
