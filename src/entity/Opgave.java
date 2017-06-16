/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bobbie
 */
@Entity
@Table(name = "opgave")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Opgave.findAll", query = "SELECT o FROM Opgave o"),
    @NamedQuery(name = "Opgave.findByOpgavenummer", query = "SELECT o FROM Opgave o WHERE o.opgavenummer = :opgavenummer"),
    @NamedQuery(name = "Opgave.findByType", query = "SELECT o FROM Opgave o WHERE o.type = :type"),
    @NamedQuery(name = "Opgave.findByStartDatoTid", query = "SELECT o FROM Opgave o WHERE o.startDatoTid = :startDatoTid"),
    @NamedQuery(name = "Opgave.findByL\u00e6ngde", query = "SELECT o FROM Opgave o WHERE o.l\u00e6ngde = :l\u00e6ngde"),
    @NamedQuery(name = "Opgave.findByAdresse", query = "SELECT o FROM Opgave o WHERE o.adresse = :adresse"),
    @NamedQuery(name = "Opgave.findByPostnr", query = "SELECT o FROM Opgave o WHERE o.postnr = :postnr"),
    @NamedQuery(name = "Opgave.findByLokal", query = "SELECT o FROM Opgave o WHERE o.lokal = :lokal")})
public class Opgave implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Opgavenummer")
    private Integer opgavenummer;
    @Column(name = "Type")
    private String type;
    @Basic(optional = false)
    @Column(name = "StartDatoTid")
    @Temporal(TemporalType.DATE)
    private Date startDatoTid;
    @Column(name = "L\u00e6ngde")
    private Integer længde;
    @Column(name = "Adresse")
    private String adresse;
    @Column(name = "Postnr")
    private Integer postnr;
    @Column(name = "Lokal")
    private String lokal;
    @Lob
    @Column(name = "Ekstra")
    private String ekstra;
    @ManyToMany(mappedBy = "opgaveCollection")
    private Collection<Tolk> tolkCollection;
    @JoinTable(name = "opgavetolk", joinColumns = {
        @JoinColumn(name = "Opgavenummer", referencedColumnName = "Opgavenummer")}, inverseJoinColumns = {
        @JoinColumn(name = "TolkID", referencedColumnName = "TolkID")})
    @ManyToMany
    private Collection<Tolk> tolkCollection1;
    @ManyToMany(mappedBy = "opgaveCollection")
    private Collection<Bruger> brugerCollection;
    @ManyToMany(mappedBy = "opgaveCollection1")
    private Collection<Bruger> brugerCollection1;
    @JoinColumn(name = "Bevillingsnummer", referencedColumnName = "Bevillingsnummer")
    @ManyToOne
    private Bevilling bevillingsnummer;

    public Opgave() {
    }

    public Opgave(Integer opgavenummer) {
        this.opgavenummer = opgavenummer;
    }

    public Opgave(Integer opgavenummer, Date startDatoTid) {
        this.opgavenummer = opgavenummer;
        this.startDatoTid = startDatoTid;
    }

    public Integer getOpgavenummer() {
        return opgavenummer;
    }

    public void setOpgavenummer(Integer opgavenummer) {
        this.opgavenummer = opgavenummer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDatoTid() {
        return startDatoTid;
    }

    public void setStartDatoTid(Date startDatoTid) {
        this.startDatoTid = startDatoTid;
    }

    public Integer getLængde() {
        return længde;
    }

    public void setLængde(Integer længde) {
        this.længde = længde;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Integer getPostnr() {
        return postnr;
    }

    public void setPostnr(Integer postnr) {
        this.postnr = postnr;
    }

    public String getLokal() {
        return lokal;
    }

    public void setLokal(String lokal) {
        this.lokal = lokal;
    }

    public String getEkstra() {
        return ekstra;
    }

    public void setEkstra(String ekstra) {
        this.ekstra = ekstra;
    }

    @XmlTransient
    public Collection<Tolk> getTolkCollection() {
        return tolkCollection;
    }

    public void setTolkCollection(Collection<Tolk> tolkCollection) {
        this.tolkCollection = tolkCollection;
    }

    @XmlTransient
    public Collection<Tolk> getTolkCollection1() {
        return tolkCollection1;
    }

    public void setTolkCollection1(Collection<Tolk> tolkCollection1) {
        this.tolkCollection1 = tolkCollection1;
    }

    @XmlTransient
    public Collection<Bruger> getBrugerCollection() {
        return brugerCollection;
    }

    public void setBrugerCollection(Collection<Bruger> brugerCollection) {
        this.brugerCollection = brugerCollection;
    }

    @XmlTransient
    public Collection<Bruger> getBrugerCollection1() {
        return brugerCollection1;
    }

    public void setBrugerCollection1(Collection<Bruger> brugerCollection1) {
        this.brugerCollection1 = brugerCollection1;
    }

    public Bevilling getBevillingsnummer() {
        return bevillingsnummer;
    }

    public void setBevillingsnummer(Bevilling bevillingsnummer) {
        this.bevillingsnummer = bevillingsnummer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (opgavenummer != null ? opgavenummer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Opgave)) {
            return false;
        }
        Opgave other = (Opgave) object;
        if ((this.opgavenummer == null && other.opgavenummer != null) || (this.opgavenummer != null && !this.opgavenummer.equals(other.opgavenummer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Opgave[ opgavenummer=" + opgavenummer + " ]";
    }
    
}
