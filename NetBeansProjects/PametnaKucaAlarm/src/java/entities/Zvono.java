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
@Table(name = "zvono")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Zvono.findAll", query = "SELECT z FROM Zvono z"),
    @NamedQuery(name = "Zvono.findByIdKor", query = "SELECT z FROM Zvono z WHERE z.idKor = :idKor"),
    @NamedQuery(name = "Zvono.findByNaziv", query = "SELECT z FROM Zvono z WHERE z.naziv = :naziv")})
public class Zvono implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idKor")
    private Integer idKor;
    @Basic(optional = false)
    @Column(name = "naziv")
    private String naziv;
    @JoinColumn(name = "idKor", referencedColumnName = "idKor", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Korisnik korisnik;

    public Zvono() {
    }

    public Zvono(Integer idKor) {
        this.idKor = idKor;
    }

    public Zvono(Integer idKor, String naziv) {
        this.idKor = idKor;
        this.naziv = naziv;
    }

    public Integer getIdKor() {
        return idKor;
    }

    public void setIdKor(Integer idKor) {
        this.idKor = idKor;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
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
        if (!(object instanceof Zvono)) {
            return false;
        }
        Zvono other = (Zvono) object;
        if ((this.idKor == null && other.idKor != null) || (this.idKor != null && !this.idKor.equals(other.idKor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Zvono[ idKor=" + idKor + " ]";
    }
    
}
