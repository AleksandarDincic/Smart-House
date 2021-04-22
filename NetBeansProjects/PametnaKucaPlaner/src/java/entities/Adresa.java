/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adinc
 */
@Entity
@Table(name = "adresa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Adresa.findAll", query = "SELECT a FROM Adresa a"),
    @NamedQuery(name = "Adresa.findByIdKor", query = "SELECT a FROM Adresa a WHERE a.idKor = :idKor"),
    @NamedQuery(name = "Adresa.findByAdresa", query = "SELECT a FROM Adresa a WHERE a.adresa = :adresa")})
public class Adresa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idKor")
    private Integer idKor;
    @Basic(optional = false)
    @Column(name = "adresa")
    private String adresa;
    @JoinColumn(name = "idKor", referencedColumnName = "idKor", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Korisnik korisnik;

    public Adresa() {
    }

    public Adresa(Integer idKor) {
        this.idKor = idKor;
    }

    public Adresa(Integer idKor, String adresa) {
        this.idKor = idKor;
        this.adresa = adresa;
    }

    public Integer getIdKor() {
        return idKor;
    }

    public void setIdKor(Integer idKor) {
        this.idKor = idKor;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKor != null ? idKor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Adresa)) {
            return false;
        }
        Adresa other = (Adresa) object;
        if ((this.idKor == null && other.idKor != null) || (this.idKor != null && !this.idKor.equals(other.idKor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Adresa[ idKor=" + idKor + " ]";
    }
    
}
