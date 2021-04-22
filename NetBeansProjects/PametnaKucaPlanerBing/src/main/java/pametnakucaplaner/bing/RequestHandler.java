/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaplaner.bing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
public class RequestHandler extends Thread {
    
    private static final String URL = "http://dev.virtualearth.net/REST/v1/Routes?wp.1=";
    private static final String BING_KEY = "ApqnF00QwILxmFTTvNs_p0HYFbdX0DeM2HGf34rSTFfkqJnfj53eWIPWY72zcPNU";
    
    private final Socket clientSocket;
    private DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String startPoint = reader.readLine();
            String endPoint = reader.readLine();
            String url = URL.concat(URLEncoder.encode(startPoint, StandardCharsets.UTF_8))
                    .concat("&wp.2=").concat(URLEncoder.encode(endPoint, StandardCharsets.UTF_8))
                    .concat("&key=").concat(URLEncoder.encode(BING_KEY, StandardCharsets.UTF_8))
                    .concat("&output=xml").concat("&ra=routeSummariesOnly");

            String response = HttpClient.handleGetRequest(url);
            response = response.substring(response.indexOf("<"));

            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response));

            Document xml = builder.parse(is);

            Element rootElement = xml.getDocumentElement();
            
            NodeList travelDuration = rootElement.getElementsByTagName("TravelDuration");

            if(travelDuration.getLength()>0){
                writer.println(travelDuration.item(0).getTextContent());
            }
            else writer.println("0");
            
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            System.out.println("Greska pri povezivanju sa Bing");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Greska pri zatvaranju socketa");
            }
        }
    }
}
