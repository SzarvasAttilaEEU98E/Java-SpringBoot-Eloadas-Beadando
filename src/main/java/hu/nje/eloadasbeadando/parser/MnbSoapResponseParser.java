
package hu.nje.eloadasbeadando.parser;

import hu.nje.eloadasbeadando.model.MnbSoapArfolyam;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;
import java.util.List;

import org.xml.sax.InputSource;

public class MnbSoapResponseParser {

    public List<MnbSoapArfolyam> feldolgoz(String soapValasz) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);

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

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document dokumentum = builder.parse(
                new InputSource(new StringReader(soapValasz))
        );

        NodeList eredmenyek = dokumentum.getElementsByTagNameNS(
                "http://www.mnb.hu/webservices/",
                "GetExchangeRatesResult"
        );

        return List.of();
    }
}
