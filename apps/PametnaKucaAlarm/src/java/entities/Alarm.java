/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adinc
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdAl", query = "SELECT a FROM Alarm a WHERE a.idAl = :idAl"),
    @NamedQuery(name = "Alarm.findByVreme", query = "SELECT a FROM Alarm a WHERE a.vreme = :vreme")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAl")
    private Integer idAl;
    @Basic(optional = false)
    @Column(name = "vreme")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vreme;
    @JoinColumn(name = "idKor", referencedColumnName = "idKor")
    @ManyToOne(optional = false)
    private Korisnik idKor;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "alarm")
    private Periodican periodican;

    public Alarm() {
    }

    public Alarm(Integer idAl) {
        this.idAl = idAl;
    }

    public Alarm(Integer idAl, Date vreme) {
        this.idAl = idAl;
        this.vreme = vreme;
    }

    public Integer getIdAl() {
        return idAl;
    }

    public void setIdAl(Integer idAl) {
        this.idAl = idAl;
    }

    public Date getVreme() {
        return vreme;
    }

    public void setVreme(Date vreme) {
        this.vreme = vreme;
    }

    public Korisnik getIdKor() {
        return idKor;
    }

    public void setIdKor(Korisnik idKor) {
        this.idKor = idKor;
    }

    public Periodican getPeriodican() {
        return periodican;
    }

    public void setPeriodican(Periodican periodican) {
        this.periodican = periodican;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAl != null ? idAl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idAl == null && other.idAl != null) || (this.idAl != null && !this.idAl.equals(other.idAl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Alarm[ idAl=" + idAl + " ]";
    }
    
}
