/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author adinc
 */
@Entity
@Table(name = "korisnik")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Korisnik.findAll", query = "SELECT k FROM Korisnik k"),
    @NamedQuery(name = "Korisnik.findByIdKor", query = "SELECT k FROM Korisnik k WHERE k.idKor = :idKor"),
    @NamedQuery(name = "Korisnik.findByIme", query = "SELECT k FROM Korisnik k WHERE k.ime = :ime"),
    @NamedQuery(name = "Korisnik.findBySifra", query = "SELECT k FROM Korisnik k WHERE k.sifra = :sifra")})
public class Korisnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idKor")
    private Integer idKor;
    @Basic(optional = false)
    @Column(name = "ime")
    private String ime;
    @Basic(optional = false)
    @Column(name = "sifra")
    private String sifra;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "korisnik")
    private Adresa adresa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idKor")
    private List<Obaveza> obavezaList;

    public Korisnik() {
    }

    public Korisnik(Integer idKor) {
        this.idKor = idKor;
    }

    public Korisnik(Integer idKor, String ime, String sifra) {
        this.idKor = idKor;
        this.ime = ime;
        this.sifra = sifra;
    }

    public Integer getIdKor() {
        return idKor;
    }

    public void setIdKor(Integer idKor) {
        this.idKor = idKor;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    @XmlTransient
    public Adresa getAdresa() {
        return adresa;
    }

    public void setAdresa(Adresa adresa) {
        this.adresa = adresa;
    }

    @XmlTransient
    public List<Obaveza> getObavezaList() {
        return obavezaList;
    }

    public void setObavezaList(List<Obaveza> obavezaList) {
        this.obavezaList = obavezaList;
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
        if (!(object instanceof Korisnik)) {
            return false;
        }
        Korisnik other = (Korisnik) object;
        if ((this.idKor == null && other.idKor != null) || (this.idKor != null && !this.idKor.equals(other.idKor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Korisnik[ idKor=" + idKor + " ]";
    }
    
}
