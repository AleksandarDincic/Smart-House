/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucaplaner;

import entities.Adresa;
import entities.Korisnik;
import entities.Obaveza;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author adinc
 */
public class PlanerHandler {

    private static final String ALARM_NAPRAVI = "napravi";

    private EntityManager em;
    private JMSContext context;
    private JMSProducer producer;

    private TextMessage formMessage(JMSContext context, String content, String type, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", type);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", Main.NAZIV_UREDJAJ);
        return txtMsg;
    }

    private int getDurationBing(String start, String end) {
        try {
            Socket clientSocket = new Socket("localhost", 5657);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(start);
            writer.println(end);
            String returnedDuration = reader.readLine();
            return Integer.parseInt(returnedDuration);
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    private synchronized Obaveza getObaveza(int idOb) {
        return em.find(Obaveza.class, idOb);
    }

    private synchronized Korisnik getKorisnik(String korisnikIme) {
        List<Korisnik> korisnikList = em.createNamedQuery("Korisnik.findByIme", Korisnik.class).setParameter("ime", korisnikIme).getResultList();

        return korisnikList.isEmpty() ? null : korisnikList.get(0);
    }

    private synchronized List<Obaveza> getObavezeFromDatabase(String korisnikIme) {
        return em.createQuery("SELECT o FROM Obaveza o WHERE o.idKor.ime=:ime", Obaveza.class).setParameter("ime", korisnikIme).getResultList();
    }

    private synchronized Obaveza[] getNearestObaveze(Korisnik korisnik, Date pocetak, boolean exclude, int idOb) {
        Obaveza[] retObaveze = new Obaveza[2];

        String prevQuery = "SELECT o FROM Obaveza o WHERE o.idKor = :idKor AND o.pocetak <= :pocetak";
        if (exclude) {
            prevQuery = prevQuery.concat(" AND o.idOb != :idOb");
        }
        prevQuery = prevQuery.concat(" ORDER BY o.pocetak DESC");

        Query obavezeQuery = em.createQuery(prevQuery, Obaveza.class);
        obavezeQuery.setParameter("idKor", korisnik);
        obavezeQuery.setParameter("pocetak", pocetak);
        if (exclude) {
            obavezeQuery.setParameter("idOb", idOb);
        }

        List<Obaveza> obavezeList = obavezeQuery.getResultList();

        retObaveze[0] = obavezeList.isEmpty() ? null : obavezeList.get(0);

        String nextQuery = "SELECT o FROM Obaveza o WHERE o.idKor = :idKor AND o.pocetak > :pocetak";
        if (exclude) {
            nextQuery = nextQuery.concat(" AND o.idOb != :idOb");
        }
        nextQuery = nextQuery.concat(" ORDER BY o.pocetak ASC");

        obavezeQuery = em.createQuery(nextQuery, Obaveza.class);
        obavezeQuery.setParameter("idKor", korisnik);
        obavezeQuery.setParameter("pocetak", pocetak);
        if (exclude) {
            obavezeQuery.setParameter("idOb", idOb);
        }

        obavezeList = obavezeQuery.getResultList();

        retObaveze[1] = obavezeList.isEmpty() ? null : obavezeList.get(0);

        return retObaveze;
    }

    private synchronized void persistObaveza(Obaveza o) {
        em.getTransaction().begin();
        em.persist(o);
        em.getTransaction().commit();
    }

    public String addObaveza(String destinacija, Date pocetak, int trajanjeSat, int trajanjeMin, String korisnikIme) {

        Korisnik korisnik = getKorisnik(korisnikIme);
        if (korisnik == null) {
            return "Neuspeh: Korisnik ne postoji";
        }

        Adresa adresa = korisnik.getAdresa();

        if (destinacija == null) {
            if (adresa == null) {
                return "Neuspeh: Korisnik nema adresu";
            }
            destinacija = adresa.getAdresa();
        }

        Obaveza[] nearestObaveze = getNearestObaveze(korisnik, pocetak, false, -1);

        Calendar calendar = Calendar.getInstance();

        if (nearestObaveze[0] != null) {
            System.out.println("Prethodna u: " + nearestObaveze[0].getPocetak());
            if (nearestObaveze[0].getPocetak().equals(pocetak)) {
                return "Neuspeh: Preklapanje";
            }

            calendar.setTime(nearestObaveze[0].getPocetak());
            calendar.add(Calendar.HOUR_OF_DAY, nearestObaveze[0].getTrajanjeSat());
            calendar.add(Calendar.MINUTE, nearestObaveze[0].getTrajanjeMinut());

            System.out.println("Kraj prethodne obaveze: " + calendar.getTime());

            if (calendar.getTime().compareTo(pocetak) > 0) {
                return "Neuspeh: Preklapanje";
            }

            int durationFromPrevious = getDurationBing(nearestObaveze[0].getDestinacija(), destinacija);

            System.out.println(durationFromPrevious);

            calendar.add(Calendar.SECOND, durationFromPrevious);

            System.out.println("Najranije se stize u: " + calendar.getTime());

            if (calendar.getTime().compareTo(pocetak) > 0) {
                return "Neuspeh: Ne stize se na vreme";
            }

        }

        if (nearestObaveze[1] != null) {
            System.out.println("Sledeca u: " + nearestObaveze[1].getPocetak());
            calendar.setTime(pocetak);
            calendar.add(Calendar.HOUR_OF_DAY, trajanjeSat);
            calendar.add(Calendar.MINUTE, trajanjeMin);

            System.out.println("Kraj nove obaveze: " + calendar.getTime());

            if (calendar.getTime().compareTo(nearestObaveze[1].getPocetak()) > 0) {
                return "Neuspeh: Preklapanje";
            }

            int durationFromNext = getDurationBing(destinacija, nearestObaveze[1].getDestinacija());

            System.out.println(durationFromNext);

            calendar.add(Calendar.SECOND, durationFromNext);

            System.out.println("Najranije na sledecu u: " + calendar.getTime());

            if (calendar.getTime().compareTo(nearestObaveze[1].getPocetak()) > 0) {
                return "Neuspeh: Ne stize se na vreme";
            }

        }

        Obaveza obaveza = new Obaveza();
        obaveza.setDestinacija(destinacija);
        obaveza.setIdKor(korisnik);
        obaveza.setPocetak(pocetak);
        obaveza.setTrajanjeMinut(trajanjeMin);
        obaveza.setTrajanjeSat(trajanjeSat);

        persistObaveza(obaveza);

        return "Uspeh";
    }

    private synchronized void persistAdresa(Adresa adresa) {
        em.getTransaction().begin();
        em.persist(adresa);
        em.getTransaction().commit();
    }

    public void setAdresa(String korisnikIme, String adresaNaziv) {
        Korisnik korisnik = getKorisnik(korisnikIme);
        if (korisnik != null) {
            Adresa adresa = korisnik.getAdresa();
            if (adresa != null) {
                adresa.setAdresa(adresaNaziv);
            } else {
                adresa = new Adresa();
                adresa.setAdresa(adresaNaziv);
                adresa.setIdKor(korisnik.getIdKor());
                adresa.setKorisnik(korisnik);
                korisnik.setAdresa(adresa);
            }
            persistAdresa(adresa);
        }
    }

    public String getObaveze(String korisnikIme) {
        try {
            List<Obaveza> retList = getObavezeFromDatabase(korisnikIme);

            String retXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><obaveze>";
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            JAXBContext jaxbContext = JAXBContext.newInstance(Obaveza.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            for (Obaveza o : retList) {
                marshaller.marshal(o, out);
            }

            retXml = retXml.concat(out.toString("UTF-8")).concat("</obaveze>");

            return retXml;

        } catch (JAXBException | UnsupportedEncodingException ex) {
            Logger.getLogger(PlanerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><obaveze></obaveze>";
    }

    public String editObaveza(int idOb, String korisnikIme, String destinacija, Date pocetak, int trajanjeSat, int trajanjeMin) {
        Korisnik korisnik = getKorisnik(korisnikIme);
        if (korisnik == null) {
            return "Neuspeh: Korisnik ne postoji";
        }

        Obaveza obaveza = getObaveza(idOb);
        if (obaveza == null || !obaveza.getIdKor().getIme().equals(korisnikIme)) {
            return "Neuspeh: Ne postoji/ne pripada korisniku";
        }

        Adresa adresa = korisnik.getAdresa();

        if (destinacija == null) {
            if (adresa == null) {
                return "Neuspeh: Korisnik nema adresu";
            }
            destinacija = adresa.getAdresa();
        }

        Obaveza[] nearestObaveze = getNearestObaveze(obaveza.getIdKor(), pocetak, true, idOb);

        Calendar calendar = Calendar.getInstance();

        if (nearestObaveze[0] != null) {
            System.out.println("Prethodna u: " + nearestObaveze[0].getPocetak());
            if (nearestObaveze[0].getPocetak().equals(pocetak)) {
                return "Neuspeh: Preklapanje";
            }

            calendar.setTime(nearestObaveze[0].getPocetak());
            calendar.add(Calendar.HOUR_OF_DAY, nearestObaveze[0].getTrajanjeSat());
            calendar.add(Calendar.MINUTE, nearestObaveze[0].getTrajanjeMinut());

            System.out.println("Kraj prethodne obaveze: " + calendar.getTime());

            if (calendar.getTime().compareTo(pocetak) > 0) {
                return "Neuspeh: Preklapanje";
            }

            int durationFromPrevious = getDurationBing(nearestObaveze[0].getDestinacija(), destinacija);

            System.out.println(durationFromPrevious);

            calendar.add(Calendar.SECOND, durationFromPrevious);

            System.out.println("Najranije se stize u: " + calendar.getTime());

            if (calendar.getTime().compareTo(pocetak) > 0) {
                return "Neuspeh: Ne stize se na vreme";
            }

        }

        if (nearestObaveze[1] != null) {
            System.out.println("Sledeca u: " + nearestObaveze[1].getPocetak());
            calendar.setTime(pocetak);
            calendar.add(Calendar.HOUR_OF_DAY, trajanjeSat);
            calendar.add(Calendar.MINUTE, trajanjeMin);

            System.out.println("Kraj nove obaveze: " + calendar.getTime());

            if (calendar.getTime().compareTo(nearestObaveze[1].getPocetak()) > 0) {
                return "Neuspeh: Preklapanje";
            }

            int durationFromNext = getDurationBing(destinacija, nearestObaveze[1].getDestinacija());

            System.out.println(durationFromNext);

            calendar.add(Calendar.SECOND, durationFromNext);

            System.out.println("Najranije na sledecu u: " + calendar.getTime());

            if (calendar.getTime().compareTo(nearestObaveze[1].getPocetak()) > 0) {
                return "Neuspeh: Ne stize se na vreme";
            }

        }

        obaveza.setDestinacija(destinacija);
        obaveza.setPocetak(pocetak);
        obaveza.setTrajanjeMinut(trajanjeMin);
        obaveza.setTrajanjeSat(trajanjeSat);

        persistObaveza(obaveza);

        return "Uspeh";
    }

    private synchronized void removeObavezaFromDatabase(Obaveza o) {
        em.getTransaction().begin();
        em.remove(o);
        em.getTransaction().commit();
    }

    public void deleteEntry(int idOb, String korisnikIme) {
        Obaveza obaveza = getObaveza(idOb);
        if (obaveza != null && obaveza.getIdKor().getIme().equals(korisnikIme)) {
            removeObavezaFromDatabase(obaveza);
        }
    }

    public PlanerHandler() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PametnaKucaPlanerPU");
        em = emf.createEntityManager();
        context = Main.getFactory().createContext();
        producer = context.createProducer();
    }

    public String createAlarm(int idOb, String korisnikIme) {
        try {
            System.out.println("TU SAM");
            Korisnik korisnik = getKorisnik(korisnikIme);

            Obaveza obaveza = getObaveza(idOb);
            if (obaveza == null || !obaveza.getIdKor().equals(korisnik)) {
                return "Neuspeh: Ne postoji/ne pripada korisniku";
            }
            String start = null;
            Obaveza[] nearestObaveze = getNearestObaveze(korisnik, obaveza.getPocetak(), true, idOb);

            if (nearestObaveze[0] == null) {
                Adresa adresa = korisnik.getAdresa();
                if (adresa == null) {
                    return "Neuspeh: Korisnik nema adresu";
                }
                start = adresa.getAdresa();
            } else {
                start = nearestObaveze[0].getDestinacija();
            }
            System.out.println("Start: " + start);
            System.out.println("End: " + obaveza.getDestinacija());

            int duration = getDurationBing(start, obaveza.getDestinacija());
            System.out.println(duration);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(obaveza.getPocetak());
            System.out.println("Pocetak obaveze: " + calendar.getTime());
            calendar.add(Calendar.SECOND, -duration);
            System.out.println("Treba se krene u: " + calendar.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy:HH:mm");

            String dateString = sdf.format(calendar.getTime());

            TextMessage txtMsg = formMessage(context, dateString, ALARM_NAPRAVI, korisnikIme);

            producer.send(Main.getAlarmTopic(), txtMsg);
            System.out.println("Alarm navijen u " + dateString);
            return "Alarm navijen u " + dateString;
        } catch (JMSException ex) {
            return "Neuspeh: Interna greska";
        }
    }

    public int calculateDuration(String start, String end, String korisnikIme) {
        Korisnik korisnik = getKorisnik(korisnikIme);
        if (start == null) {
            Date currentTime = new Date();
            Obaveza[] nearestObaveze = getNearestObaveze(korisnik, currentTime, false, 0);
            if (nearestObaveze[0] == null) {
                Adresa adresa = korisnik.getAdresa();
                if (adresa == null) {
                    return 0;
                } else {
                    start = adresa.getAdresa();
                }
            } else {
                start = nearestObaveze[0].getDestinacija();
            }
        }

        return getDurationBing(start, end);
    }

//KALKULATOR MRTVI
}
