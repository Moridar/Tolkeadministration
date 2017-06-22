/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bobbie
 */
@Entity
@Table(name = "bevilling")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bevilling.findAll", query = "SELECT b FROM Bevilling b"),
    @NamedQuery(name = "Bevilling.findByBevillingsnummer", query = "SELECT b FROM Bevilling b WHERE b.bevillingsnummer = :bevillingsnummer"),
    @NamedQuery(name = "Bevilling.findByNavn", query = "SELECT b FROM Bevilling b WHERE b.navn = :navn"),
    @NamedQuery(name = "Bevilling.findByOpgave", query = "SELECT b FROM Bevilling b WHERE b.opgave = :opgave"),
    @NamedQuery(name = "Bevilling.findByTimer", query = "SELECT b FROM Bevilling b WHERE b.timer = :timer")})
public class Bevilling implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Bevillingsnummer")
    private Integer bevillingsnummer;
    @Basic(optional = false)
    @Column(name = "Navn")
    private String navn;
    @Column(name = "Opgave")
    private String opgave;
    @Basic(optional = false)
    @Column(name = "Timer")
    private int timer;
    @Lob
    @Column(name = "PDF")
    private byte[] pdf;
    @JoinColumn(name = "Bruger", referencedColumnName = "BrugerID")
    @ManyToOne(optional = false)
    private Bruger bruger;
    @OneToMany(mappedBy = "bevillingsnummer")
    private Collection<Opgave> opgaveCollection;

    public Bevilling() {
    }

    public Bevilling(Integer bevillingsnummer) {
        this.bevillingsnummer = bevillingsnummer;
    }

    public Bevilling(Integer bevillingsnummer, String navn, int timer) {
        this.bevillingsnummer = bevillingsnummer;
        this.navn = navn;
        this.timer = timer;
    }

    public Integer getBevillingsnummer() {
        return bevillingsnummer;
    }

    public void setBevillingsnummer(Integer bevillingsnummer) {
        this.bevillingsnummer = bevillingsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getOpgave() {
        return opgave;
    }

    public void setOpgave(String opgave) {
        this.opgave = opgave;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public Bruger getBruger() {
        return bruger;
    }

    public void setBruger(Bruger bruger) {
        this.bruger = bruger;
    }

    @XmlTransient
    public Collection<Opgave> getOpgaveCollection() {
        return opgaveCollection;
    }

    public void setOpgaveCollection(Collection<Opgave> opgaveCollection) {
        this.opgaveCollection = opgaveCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bevillingsnummer != null ? bevillingsnummer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bevilling)) {
            return false;
        }
        Bevilling other = (Bevilling) object;
        if ((this.bevillingsnummer == null && other.bevillingsnummer != null) || (this.bevillingsnummer != null && !this.bevillingsnummer.equals(other.bevillingsnummer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Bevilling[ bevillingsnummer=" + bevillingsnummer + " ]";
    }
    
}
