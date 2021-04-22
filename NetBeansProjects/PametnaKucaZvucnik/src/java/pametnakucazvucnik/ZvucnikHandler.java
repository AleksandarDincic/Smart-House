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
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author adinc
 */
public class ZvucnikHandler {

    private static final String YT_LINK = "https://www.youtube.com/watch?v=";
    private static final String AUTOPLAY_PARAM = "&autoplay=1";

    private EntityManager em;

    private synchronized void dodajPesmu(String pesmaNaziv, String korisnikIme) {
        TypedQuery<Korisnik> query = em.createNamedQuery("Korisnik.findByIme", Korisnik.class);
        query.setParameter("ime", korisnikIme);
        List<Korisnik> korisnici = query.getResultList();

        Korisnik korisnik = korisnici.get(0);

        Pesma pesma = new Pesma();
        pesma.setNaziv(pesmaNaziv);
        pesma.setIdKor(korisnik);
        em.getTransaction().begin();
        em.persist(pesma);
        em.getTransaction().commit();
    }

    private synchronized String dohvPesme(String korisnikIme) {
        TypedQuery<Korisnik> query = em.createNamedQuery("Korisnik.findByIme", Korisnik.class);
        query.setParameter("ime", korisnikIme);
        List<Korisnik> korisnici = query.getResultList();

        Korisnik korisnik = korisnici.get(0);

        StringBuilder sb = new StringBuilder();
        List<Pesma> pesme = korisnik.getPesmaList();

        for (Pesma p : pesme) {
            sb.append(p.getNaziv()).append("\r\n");
        }

        return sb.toString();
    }

    public ZvucnikHandler() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PametnaKucaZvucnikPU");
        em = emf.createEntityManager();
    }

    public void pustiPesmu(String naziv, String korisnikIme, boolean persist) {
        try {
            Socket clientSocket = new Socket("localhost", 5656);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(naziv);
            String returnedId = reader.readLine();
            Desktop.getDesktop().browse(new URI(YT_LINK + returnedId + AUTOPLAY_PARAM));

            if (persist) {
                dodajPesmu(naziv, korisnikIme);
            }
        } catch (IOException ex) {
            System.out.println("Greska pri povezivanju sa serverom");
        } catch (URISyntaxException ex) {
            System.out.println("Greska pri otvaranju browsera");
        }
    }
    
    public String dohvIstoriju(String korisnikIme){
        return dohvPesme(korisnikIme);
    }
}
