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
@Table(name = "periodican")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Periodican.findAll", query = "SELECT p FROM Periodican p"),
    @NamedQuery(name = "Periodican.findByIdAl", query = "SELECT p FROM Periodican p WHERE p.idAl = :idAl"),
    @NamedQuery(name = "Periodican.findByGodina", query = "SELECT p FROM Periodican p WHERE p.godina = :godina"),
    @NamedQuery(name = "Periodican.findByMesec", query = "SELECT p FROM Periodican p WHERE p.mesec = :mesec"),
    @NamedQuery(name = "Periodican.findByDan", query = "SELECT p FROM Periodican p WHERE p.dan = :dan"),
    @NamedQuery(name = "Periodican.findBySat", query = "SELECT p FROM Periodican p WHERE p.sat = :sat"),
    @NamedQuery(name = "Periodican.findByMinut", query = "SELECT p FROM Periodican p WHERE p.minut = :minut")})
public class Periodican implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idAl")
    private Integer idAl;
    @Basic(optional = false)
    @Column(name = "godina")
    private int godina;
    @Basic(optional = false)
    @Column(name = "mesec")
    private int mesec;
    @Basic(optional = false)
    @Column(name = "dan")
    private int dan;
    @Basic(optional = false)
    @Column(name = "sat")
    private int sat;
    @Basic(optional = false)
    @Column(name = "minut")
    private int minut;
    @JoinColumn(name = "idAl", referencedColumnName = "idAl", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Alarm alarm;

    public Periodican() {
    }

    public Periodican(Integer idAl) {
        this.idAl = idAl;
    }

    public Periodican(Integer idAl, int godina, int mesec, int dan, int sat, int minut) {
        this.idAl = idAl;
        this.godina = godina;
        this.mesec = mesec;
        this.dan = dan;
        this.sat = sat;
        this.minut = minut;
    }

    public Integer getIdAl() {
        return idAl;
    }

    public void setIdAl(Integer idAl) {
        this.idAl = idAl;
    }

    public int getGodina() {
        return godina;
    }

    public void setGodina(int godina) {
        this.godina = godina;
    }

    public int getMesec() {
        return mesec;
    }

    public void setMesec(int mesec) {
        this.mesec = mesec;
    }

    public int getDan() {
        return dan;
    }

    public void setDan(int dan) {
        this.dan = dan;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getMinut() {
        return minut;
    }

    public void setMinut(int minut) {
        this.minut = minut;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
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
        if (!(object instanceof Periodican)) {
            return false;
        }
        Periodican other = (Periodican) object;
        if ((this.idAl == null && other.idAl != null) || (this.idAl != null && !this.idAl.equals(other.idAl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Periodican[ idAl=" + idAl + " ]";
    }
    
}
