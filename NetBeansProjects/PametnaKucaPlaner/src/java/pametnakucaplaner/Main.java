/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaplaner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author adinc
 */
public class Main {

    public static final String NAZIV_UREDJAJ = "PLANER";

    private static final String DODAJ = "dodaj";
    private static final String IZMENI = "izmeni";
    private static final String LISTAJ = "listaj";
    private static final String OBRISI = "obrisi";
    private static final String ODGOVOR = "odgovor";
    private static final String ADRESA = "adresa";
    private static final String ALARM = "alarm";
    private static final String KALKULATOR = "kalkulator";

    @Resource(lookup = "pametnaKucaFactory")
    private static ConnectionFactory factory;

    public static ConnectionFactory getFactory() {
        return factory;
    }

    @Resource(lookup = "pametnaKucaPlanerTopic")
    private static Topic planerTopic;

    public static Topic getPlanerTopic() {
        return planerTopic;
    }

    @Resource(lookup = "pametnaKucaAlarmTopic")
    private static Topic alarmTopic;

    public static Topic getAlarmTopic() {
        return alarmTopic;
    }

    private static TextMessage formReplyMessage(JMSContext context, String content, String user, String source) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", ODGOVOR);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", source);
        return txtMsg;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PlanerHandler planerHandler = new PlanerHandler();

        JMSContext context = factory.createContext();

        JMSConsumer dodajConsumer = context.createConsumer(planerTopic, "tip='" + DODAJ + "'");
        JMSConsumer listajConsumer = context.createConsumer(planerTopic, "tip='" + LISTAJ + "'");
        JMSConsumer izmeniConsumer = context.createConsumer(planerTopic, "tip='" + IZMENI + "'");
        JMSConsumer obrisiConsumer = context.createConsumer(planerTopic, "tip='" + OBRISI + "'");
        JMSConsumer adresaConsumer = context.createConsumer(planerTopic, "tip='" + ADRESA + "'");
        JMSConsumer alarmConsumer = context.createConsumer(planerTopic, "tip='" + ALARM + "'");
        JMSConsumer kalkulatorConsumer = context.createConsumer(planerTopic, "tip='" + KALKULATOR + "'");

        JMSProducer odgovorProducer = context.createProducer();

        dodajConsumer.setMessageListener(msg -> {
            try {
                System.out.println("Stiglo mi ");
                TextMessage txtMsg = (TextMessage) msg;
                SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                Date pocetak = sdf.parse(txtMsg.getText());
                String retVal = planerHandler.addObaveza(msg.getStringProperty("destinacija"), pocetak,
                        msg.getIntProperty("trajanjeSat"), msg.getIntProperty("trajanjeMinut"), korisnikIme); //, msg.getBooleanProperty("alarm"));

                TextMessage replyMsg = formReplyMessage(context, retVal, korisnikIme, izvor);

                odgovorProducer.send(planerTopic, replyMsg);
            } catch (JMSException | ParseException ex) {
                System.out.println("JMS greska pri kreiranju obaveze");
            }
        });

        listajConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;

                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                String retVal = planerHandler.getObaveze(korisnikIme);
                System.out.println(retVal);
                TextMessage replyMsg = formReplyMessage(context, retVal, korisnikIme, izvor);

                odgovorProducer.send(planerTopic, replyMsg);
            } catch (JMSException ex) {
                System.out.println("JMS greska pri listanju obaveza");
            }
        });

        adresaConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;
                String korisnikIme = txtMsg.getStringProperty("korisnik");
                planerHandler.setAdresa(korisnikIme, txtMsg.getText());

            } catch (JMSException ex) {
                System.out.println("JMS greska pri postavljanju adrese");
            }
        });

        izmeniConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;
                SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                Date pocetak = sdf.parse(txtMsg.getText());
                String retVal = planerHandler.editObaveza(txtMsg.getIntProperty("idOb"), korisnikIme, txtMsg.getStringProperty("destinacija"), pocetak,
                        msg.getIntProperty("trajanjeSat"), msg.getIntProperty("trajanjeMinut"));

                TextMessage replyMsg = formReplyMessage(context, retVal, korisnikIme, izvor);

                odgovorProducer.send(planerTopic, replyMsg);
            } catch (JMSException | ParseException ex) {
                System.out.println("JMS greska pri postavljanju adrese");
            }
        });

        obrisiConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;

                String korisnikIme = txtMsg.getStringProperty("korisnik");

                planerHandler.deleteEntry(Integer.parseInt(txtMsg.getText()), korisnikIme);
            } catch (JMSException ex) {
                System.out.println("JMS greska pri postavljanju adrese");
            }
        });

        alarmConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;

                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                String retVal = planerHandler.createAlarm(Integer.parseInt(txtMsg.getText()), korisnikIme);

                TextMessage replyMsg = formReplyMessage(context, retVal, korisnikIme, izvor);

                odgovorProducer.send(planerTopic, replyMsg);
            } catch (JMSException ex) {
                System.out.println("JMS greska pri stvaranju alarma");
            }
        });

        kalkulatorConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;

                String korisnikIme = txtMsg.getStringProperty("korisnik");
                String izvor = txtMsg.getStringProperty("izvor");

                String retVal = Integer.toString(planerHandler.calculateDuration(txtMsg.getStringProperty("start"), txtMsg.getText(), korisnikIme));

                TextMessage replyMsg = formReplyMessage(context, retVal, korisnikIme, izvor);

                odgovorProducer.send(planerTopic, replyMsg);
            } catch (JMSException ex) {
                System.out.println("JMS greska pri racunanju distance");
            }
        });

        while(true);
    }

}
