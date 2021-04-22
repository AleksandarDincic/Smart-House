/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaalarm;

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

    public static final String NAPRAVI = "napravi";
    public static final String ZVONO = "zvono";

    public static final String NAZIV_UREDJAJ = "ALARM";

    @Resource(lookup = "pametnaKucaFactory")
    private static ConnectionFactory factory;

    @Resource(lookup = "pametnaKucaAlarmTopic")
    private static Topic alarmTopic;

    @Resource(lookup = "pametnaKucaZvucnikTopic")
    private static Topic zvucnikTopic;

    public static ConnectionFactory getFactory() {
        return factory;
    }

    public static Topic getZvucnikTopic() {
        return zvucnikTopic;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JMSContext context = factory.createContext();
        JMSConsumer napraviConsumer = context.createConsumer(alarmTopic, "tip='" + NAPRAVI + "'");
        JMSConsumer zvonoConsumer = context.createConsumer(alarmTopic, "tip='" + ZVONO + "'");

        AlarmHandler alarmHandler = new AlarmHandler();

        alarmHandler.start();

        napraviConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;
                alarmHandler.addAlarm(txtMsg.getText(), txtMsg.getStringProperty("korisnik"), txtMsg.getStringProperty("perioda"));
            } catch (JMSException ex) {
                System.out.println("JMS greska pri pravljenju alarma");
            }
        });

        zvonoConsumer.setMessageListener(msg -> {
            try {
                TextMessage txtMsg = (TextMessage) msg;
                alarmHandler.setZvono(txtMsg.getStringProperty("korisnik"), txtMsg.getText());
            } catch (JMSException ex) {
                System.out.println("JMS greska pri namestanju zvona");
            }
        });

        while (true);
    }

}
