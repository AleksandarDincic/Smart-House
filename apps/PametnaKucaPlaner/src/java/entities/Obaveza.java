/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adinc
 */
@Entity
@Table(name = "obaveza")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Obaveza.findAll", query = "SELECT o FROM Obaveza o"),
    @NamedQuery(name = "Obaveza.findByIdOb", query = "SELECT o FROM Obaveza o WHERE o.idOb = :idOb"),
    @NamedQuery(name = "Obaveza.findByDestinacija", query = "SELECT o FROM Obaveza o WHERE o.destinacija = :destinacija"),
    @NamedQuery(name = "Obaveza.findByPocetak", query = "SELECT o FROM Obaveza o WHERE o.pocetak = :pocetak"),
    @NamedQuery(name = "Obaveza.findByTrajanjeSat", query = "SELECT o FROM Obaveza o WHERE o.trajanjeSat = :trajanjeSat"),
    @NamedQuery(name = "Obaveza.findByTrajanjeMinut", query = "SELECT o FROM Obaveza o WHERE o.trajanjeMinut = :trajanjeMinut")})
public class Obaveza implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idOb")
    private Integer idOb;
    @Basic(optional = false)
    @Column(name = "destinacija")
    private String destinacija;
    @Basic(optional = false)
    @Column(name = "pocetak")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pocetak;
    @Basic(optional = false)
    @Column(name = "trajanjeSat")
    private int trajanjeSat;
    @Basic(optional = false)
    @Column(name = "trajanjeMinut")
    private int trajanjeMinut;
    @JoinColumn(name = "idKor", referencedColumnName = "idKor")
    @ManyToOne(optional = false)
    private Korisnik idKor;

    public Obaveza() {
    }

    public Obaveza(Integer idOb) {
        this.idOb = idOb;
    }

    public Obaveza(Integer idOb, String destinacija, Date pocetak, int trajanjeSat, int trajanjeMinut) {
        this.idOb = idOb;
        this.destinacija = destinacija;
        this.pocetak = pocetak;
        this.trajanjeSat = trajanjeSat;
        this.trajanjeMinut = trajanjeMinut;
    }

    public Integer getIdOb() {
        return idOb;
    }

    public void setIdOb(Integer idOb) {
        this.idOb = idOb;
    }

    public String getDestinacija() {
        return destinacija;
    }

    public void setDestinacija(String destinacija) {
        this.destinacija = destinacija;
    }

    public Date getPocetak() {
        return pocetak;
    }

    public void setPocetak(Date pocetak) {
        this.pocetak = pocetak;
    }

    public int getTrajanjeSat() {
        return trajanjeSat;
    }

    public void setTrajanjeSat(int trajanjeSat) {
        this.trajanjeSat = trajanjeSat;
    }

    public int getTrajanjeMinut() {
        return trajanjeMinut;
    }

    public void setTrajanjeMinut(int trajanjeMinut) {
        this.trajanjeMinut = trajanjeMinut;
    }

    public Korisnik getIdKor() {
        return idKor;
    }

    public void setIdKor(Korisnik idKor) {
        this.idKor = idKor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOb != null ? idOb.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Obaveza)) {
            return false;
        }
        Obaveza other = (Obaveza) object;
        if ((this.idOb == null && other.idOb != null) || (this.idOb != null && !this.idOb.equals(other.idOb))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Obaveza[ idOb=" + idOb + " ]";
    }
    
}
