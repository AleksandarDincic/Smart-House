/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaalarm;

import entities.Alarm;
import entities.Korisnik;
import entities.Periodican;
import entities.Zvono;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author adinc
 */
public class AlarmHandler extends Thread {

    private static final String DEFAULT_ZVONO = "crazy frog";
    private static final String ZVUCNIK_PUSTI = "pusti";

    private JMSContext context;
    private JMSProducer producer;

    private List<Alarm> alarms;
    private EntityManager em;

    private Calendar currentTime, alarmTime;

    private synchronized void updateAlarms() {
        TypedQuery<Alarm> alarmsQuery = em.createNamedQuery("Alarm.findAll", Alarm.class);
        alarms = alarmsQuery.getResultList();
    }

    private TextMessage formZvucnikMessage(String content, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", ZVUCNIK_PUSTI);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", Main.NAZIV_UREDJAJ);
        txtMsg.setBooleanProperty("persist", false);
        return txtMsg;
    }

    private synchronized void checkAlarms() {
        currentTime.setTime(new Date());
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);

        boolean changesMade = false;

        for (Alarm alarm : alarms) {
            alarmTime.setTime(alarm.getVreme());

            Periodican periodican = alarm.getPeriodican();

            if (periodican != null) {
                while (alarmTime.compareTo(currentTime) < 0) {
                    alarmTime.add(Calendar.YEAR, periodican.getGodina());
                    alarmTime.add(Calendar.MONTH, periodican.getMesec());
                    alarmTime.add(Calendar.DAY_OF_MONTH, periodican.getDan());
                    alarmTime.add(Calendar.HOUR_OF_DAY, periodican.getSat());
                    alarmTime.add(Calendar.MINUTE, periodican.getMinut());
                }

                if (alarmTime.getTime().compareTo(alarm.getVreme()) != 0) {
                    em.getTransaction().begin();
                    alarm.setVreme(alarmTime.getTime());
                    em.getTransaction().commit();
                    changesMade = true;
                }
            }

            if (alarmTime.compareTo(currentTime) == 0) {
                System.out.println("Alarm u " + alarmTime.getTime() + "za korisnika " + alarm.getIdKor().getIme());
                Korisnik korisnik = alarm.getIdKor();
                Zvono zvono = korisnik.getZvono();
                String zvonoNaziv = zvono != null ? zvono.getNaziv() : DEFAULT_ZVONO;

                try {
                    TextMessage txtMsg = formZvucnikMessage(zvonoNaziv, korisnik.getIme());
                    producer.send(Main.getZvucnikTopic(), txtMsg);
                } catch (JMSException ex) {
                    System.out.println("Nije uspelo pustanje zvona");
                }
            }
        }

        if (changesMade) {
            updateAlarms();
        }
    }

    private synchronized Korisnik getKorisnik(String korisnikIme) {
        List<Korisnik> korisnikList = em.createNamedQuery("Korisnik.findByIme", Korisnik.class).setParameter("ime", korisnikIme).getResultList();

        return korisnikList.isEmpty() ? null : korisnikList.get(0);
    }

    private synchronized void persistAlarm(Alarm alarm) {
        em.getTransaction().begin();
        em.persist(alarm);
        em.getTransaction().commit();
    }

    private synchronized void persistPeriodican(Periodican periodican) {
        em.getTransaction().begin();
        em.persist(periodican);
        periodican.getAlarm().setPeriodican(periodican);
        em.getTransaction().commit();
    }

    private synchronized void persistZvono(Zvono zvono) {
        em.getTransaction().begin();
        em.persist(zvono);
        zvono.getKorisnik().setZvono(zvono);
        em.getTransaction().commit();
    }

    public AlarmHandler() {
        context = Main.getFactory().createContext();
        producer = context.createProducer();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PametnaKucaAlarmPU");
        em = emf.createEntityManager();

        currentTime = Calendar.getInstance();
        alarmTime = Calendar.getInstance();
    }

    public void addAlarm(String time, String user, String period) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

            Date paramTime = sdf.parse(time);

            Korisnik korisnik = getKorisnik(user);

            if (korisnik != null) {
                Alarm newAlarm = new Alarm();

                newAlarm.setVreme(paramTime);
                newAlarm.setIdKor(korisnik);

                persistAlarm(newAlarm);

                if (period != null) {
                    String[] periodValues = period.split(":");
                    int godina = Integer.parseInt(periodValues[0]);
                    int mesec = Integer.parseInt(periodValues[1]);
                    int dan = Integer.parseInt(periodValues[2]);
                    int sat = Integer.parseInt(periodValues[3]);
                    int minut = Integer.parseInt(periodValues[4]);

                    Periodican periodican = new Periodican();
                    periodican.setIdAl(newAlarm.getIdAl());
                    periodican.setAlarm(newAlarm);
                    periodican.setGodina(godina);
                    periodican.setMesec(mesec);
                    periodican.setDan(dan);
                    periodican.setSat(sat);
                    periodican.setMinut(minut);

                    persistPeriodican(periodican);
                }

                alarms.add(newAlarm);
            }
        } catch (ParseException ex) {
            System.out.println("Greska u parsiranju vremena");
        }
    }

    public synchronized void setZvono(String korisnikIme, String naziv) {
        Korisnik korisnik = getKorisnik(korisnikIme);

        Zvono zvono = korisnik.getZvono();
        if (zvono == null) {
            zvono = new Zvono();
            zvono.setIdKor(korisnik.getIdKor());
            zvono.setKorisnik(korisnik);
            zvono.setNaziv(naziv);
        } else {
            zvono.setNaziv(naziv);
        }
        
        persistZvono(zvono);
    }

    @Override
    public void run() {
        updateAlarms();

        while (true) {
            try {
                checkAlarms();

                currentTime.setTime(new Date());
                Thread.sleep((60 - currentTime.get(Calendar.SECOND)) * 1000);
            } catch (InterruptedException ex) {
            }
        }
    }
}
