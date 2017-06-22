/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "bruger")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bruger.findAll", query = "SELECT b FROM Bruger b"),
    @NamedQuery(name = "Bruger.findByBrugerID", query = "SELECT b FROM Bruger b WHERE b.brugerID = :brugerID"),
    @NamedQuery(name = "Bruger.findByNavn", query = "SELECT b FROM Bruger b WHERE b.navn = :navn"),
    @NamedQuery(name = "Bruger.findByEfternavn", query = "SELECT b FROM Bruger b WHERE b.efternavn = :efternavn"),
    @NamedQuery(name = "Bruger.findByTelefon", query = "SELECT b FROM Bruger b WHERE b.telefon = :telefon"),
    @NamedQuery(name = "Bruger.findByEmail", query = "SELECT b FROM Bruger b WHERE b.email = :email"),
    @NamedQuery(name = "Bruger.findByAdresse", query = "SELECT b FROM Bruger b WHERE b.adresse = :adresse"),
    @NamedQuery(name = "Bruger.findByPostnr", query = "SELECT b FROM Bruger b WHERE b.postnr = :postnr")})
public class Bruger implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BrugerID")
    private Integer brugerID;
    @Basic(optional = false)
    @Column(name = "Navn")
    private String navn;
    @Basic(optional = false)
    @Column(name = "Efternavn")
    private String efternavn;
    @Column(name = "Telefon")
    private String telefon;
    @Column(name = "Email")
    private String email;
    @Column(name = "Adresse")
    private String adresse;
    @Column(name = "Postnr")
    private String postnr;
    @JoinTable(name = "modtager", joinColumns = {
        @JoinColumn(name = "BrugerID", referencedColumnName = "BrugerID")}, inverseJoinColumns = {
        @JoinColumn(name = "Opgavenummer", referencedColumnName = "Opgavenummer")})
    @ManyToMany
    private Collection<Opgave> opgaveCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bruger")
    private Collection<Bevilling> bevillingCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bestiller")
    private Collection<Opgave> opgaveCollection1;

    public Bruger() {
    }

    public Bruger(Integer brugerID) {
        this.brugerID = brugerID;
    }

    public Bruger(Integer brugerID, String navn, String efternavn) {
        this.brugerID = brugerID;
        this.navn = navn;
        this.efternavn = efternavn;
    }

    public Integer getBrugerID() {
        return brugerID;
    }

    public void setBrugerID(Integer brugerID) {
        this.brugerID = brugerID;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getEfternavn() {
        return efternavn;
    }

    public void setEfternavn(String efternavn) {
        this.efternavn = efternavn;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPostnr() {
        return postnr;
    }

    public void setPostnr(String postnr) {
        this.postnr = postnr;
    }

    @XmlTransient
    public Collection<Opgave> getOpgaveCollection() {
        return opgaveCollection;
    }

    public void setOpgaveCollection(Collection<Opgave> opgaveCollection) {
        this.opgaveCollection = opgaveCollection;
    }

    @XmlTransient
    public Collection<Bevilling> getBevillingCollection() {
        return bevillingCollection;
    }

    public void setBevillingCollection(Collection<Bevilling> bevillingCollection) {
        this.bevillingCollection = bevillingCollection;
    }

    @XmlTransient
    public Collection<Opgave> getOpgaveCollection1() {
        return opgaveCollection1;
    }

    public void setOpgaveCollection1(Collection<Opgave> opgaveCollection1) {
        this.opgaveCollection1 = opgaveCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (brugerID != null ? brugerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bruger)) {
            return false;
        }
        Bruger other = (Bruger) object;
        if ((this.brugerID == null && other.brugerID != null) || (this.brugerID != null && !this.brugerID.equals(other.brugerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Bruger[ brugerID=" + brugerID + " ]";
    }
    
}
