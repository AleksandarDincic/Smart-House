/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj.planer;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author adinc
 */
public class PlanerParser {

    public static List<PlanerEntry> parseXml(String xml) throws SAXException, IOException, ParserConfigurationException, IOException, ParseException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        
        InputSource is = new InputSource(new StringReader(xml));

        Document doc = builder.parse(is);
        
        Element rootElement = doc.getDocumentElement();
        
        NodeList obavezaNodeList = rootElement.getElementsByTagName("obaveza");
        
        
        ArrayList<PlanerEntry> entryList = new ArrayList<PlanerEntry>();
        
        for(int i = 0; i < obavezaNodeList.getLength(); i++){
            Element obavezaElement = (Element) obavezaNodeList.item(i);
            String destinacija = obavezaElement.getElementsByTagName("destinacija").item(0).getTextContent();
            int idOb = Integer.parseInt(obavezaElement.getElementsByTagName("idOb").item(0).getTextContent());
            Date pocetak = sdf.parse(obavezaElement.getElementsByTagName("pocetak").item(0).getTextContent());
            int trajanjeSat = Integer.parseInt(obavezaElement.getElementsByTagName("trajanjeSat").item(0).getTextContent());
            int trajanjeMinut = Integer.parseInt(obavezaElement.getElementsByTagName("trajanjeMinut").item(0).getTextContent());
            
            entryList.add(new PlanerEntry(idOb, destinacija, trajanjeSat, trajanjeMinut, pocetak));
        }
        
        return entryList;
    }
}
