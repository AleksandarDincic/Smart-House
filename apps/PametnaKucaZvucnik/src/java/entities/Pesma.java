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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adinc
 */
@Entity
@Table(name = "pesma")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pesma.findAll", query = "SELECT p FROM Pesma p"),
    @NamedQuery(name = "Pesma.findByIdPes", query = "SELECT p FROM Pesma p WHERE p.idPes = :idPes"),
    @NamedQuery(name = "Pesma.findByNaziv", query = "SELECT p FROM Pesma p WHERE p.naziv = :naziv")})
public class Pesma implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPes")
    private Integer idPes;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "naziv")
    private String naziv;
    @JoinColumn(name = "idKor", referencedColumnName = "idKor")
    @ManyToOne(optional = false)
    private Korisnik idKor;

    public Pesma() {
    }

    public Pesma(Integer idPes) {
        this.idPes = idPes;
    }

    public Pesma(Integer idPes, String naziv) {
        this.idPes = idPes;
        this.naziv = naziv;
    }

    public Integer getIdPes() {
        return idPes;
    }

    public void setIdPes(Integer idPes) {
        this.idPes = idPes;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
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
        hash += (idPes != null ? idPes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pesma)) {
            return false;
        }
        Pesma other = (Pesma) object;
        if ((this.idPes == null && other.idPes != null) || (this.idPes != null && !this.idPes.equals(other.idPes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Pesma[ idPes=" + idPes + " ]";
    }
    
}
