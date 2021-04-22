/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pametnakucauredjaj.planer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author adinc
 */
public class PlanerEntry {

    private int idOb;
    private String destinacija;
    private int trajanjeSat, trajanjeMinut;
    private Date pocetak;

    public PlanerEntry(int idOb, String destinacija, int trajanjeSat, int trajanjeMinut, Date pocetak) {
        this.idOb = idOb;
        this.destinacija = destinacija;
        this.trajanjeSat = trajanjeSat;
        this.trajanjeMinut = trajanjeMinut;
        this.pocetak = pocetak;
    }

    public int getIdOb() {
        return idOb;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public int getTrajanjeSat() {
        return trajanjeSat;
    }

    public int getTrajanjeMinut() {
        return trajanjeMinut;
    }

    public Date getPocetak() {
        return pocetak;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        return destinacija + ", " + sdf.format(pocetak) + ", trajanje " + trajanjeSat + ":" + trajanjeMinut;
    }
}
