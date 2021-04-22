/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucazvucnik;

import entities.Korisnik;
import entities.Pesma;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author adinc
 */
public class Main {

    private static final String PUSTI = "pusti";
    private static final String ISTORIJA = "istorija";
    private static final String ODGOVOR = "odgovor";

    @Resource(lookup = "pametnaKucaFactory")
    private static ConnectionFactory factory;

    @Resource(lookup = "pametnaKucaZvucnikTopic")
    private static Topic topic;

    private static TextMessage formReplyMessage(JMSContext context, String content, String device, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", ODGOVOR);
        txtMsg.setStringProperty("izvor", device);
        txtMsg.setStringProperty("korisnik", user);
        return txtMsg;
    }

    public static void main(String[] args) {
        ZvucnikHandler zvucnikHandler = new ZvucnikHandler();

        JMSContext context = factory.createContext();
        JMSConsumer consumerPusti = context.createConsumer(topic, "tip='" + PUSTI + "'");
        JMSConsumer consumerIstorija = context.createConsumer(topic, "tip='" + ISTORIJA + "'");
        JMSProducer producer = context.createProducer();
        
        consumerPusti.setMessageListener((Message message) -> {
            try {
                TextMessage txtMsg = (TextMessage) message;
                String korisnikIme = txtMsg.getStringProperty("korisnik");

                zvucnikHandler.pustiPesmu(txtMsg.getText(), korisnikIme, txtMsg.getBooleanProperty("persist"));
            } catch (JMSException ex) {
                System.out.println("JMS Greska");
            }
        });

        consumerIstorija.setMessageListener((Message message) -> {
            try {
                TextMessage txtMsg = (TextMessage) message;
                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                String replyBody = zvucnikHandler.dohvIstoriju(korisnikIme);

                TextMessage replyMsg = formReplyMessage(context, replyBody, izvor, korisnikIme);
                producer.send(topic, replyMsg);
            } catch (JMSException ex) {
                System.out.println("JMS Greska");
            }
        });

        while (true);
    }
}
