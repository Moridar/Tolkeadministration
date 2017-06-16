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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bobbie
 */
@Entity
@Table(name = "tolk")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tolk.findAll", query = "SELECT t FROM Tolk t"),
    @NamedQuery(name = "Tolk.findByTolkID", query = "SELECT t FROM Tolk t WHERE t.tolkID = :tolkID"),
    @NamedQuery(name = "Tolk.findByNavn", query = "SELECT t FROM Tolk t WHERE t.navn = :navn"),
    @NamedQuery(name = "Tolk.findByEfternavn", query = "SELECT t FROM Tolk t WHERE t.efternavn = :efternavn"),
    @NamedQuery(name = "Tolk.findByTelefon", query = "SELECT t FROM Tolk t WHERE t.telefon = :telefon"),
    @NamedQuery(name = "Tolk.findByEmail", query = "SELECT t FROM Tolk t WHERE t.email = :email"),
    @NamedQuery(name = "Tolk.findByAdresse", query = "SELECT t FROM Tolk t WHERE t.adresse = :adresse"),
    @NamedQuery(name = "Tolk.findByPostnr", query = "SELECT t FROM Tolk t WHERE t.postnr = :postnr")})
public class Tolk implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "TolkID")
    private Integer tolkID;
    @Basic(optional = false)
    @Column(name = "Navn")
    private String navn;
    @Basic(optional = false)
    @Column(name = "Efternavn")
    private String efternavn;
    @Basic(optional = false)
    @Column(name = "Telefon")
    private String telefon;
    @Basic(optional = false)
    @Column(name = "Email")
    private String email;
    @Basic(optional = false)
    @Column(name = "Adresse")
    private String adresse;
    @Column(name = "Postnr")
    private Integer postnr;
    @JoinTable(name = "tilknyttet", joinColumns = {
        @JoinColumn(name = "TolkID", referencedColumnName = "TolkID")}, inverseJoinColumns = {
        @JoinColumn(name = "Opgavenummer", referencedColumnName = "Opgavenummer")})
    @ManyToMany
    private Collection<Opgave> opgaveCollection;
    @ManyToMany(mappedBy = "tolkCollection1")
    private Collection<Opgave> opgaveCollection1;

    public Tolk() {
    }

    public Tolk(Integer tolkID) {
        this.tolkID = tolkID;
    }

    public Tolk(Integer tolkID, String navn, String efternavn, String telefon, String email, String adresse) {
        this.tolkID = tolkID;
        this.navn = navn;
        this.efternavn = efternavn;
        this.telefon = telefon;
        this.email = email;
        this.adresse = adresse;
    }

    public Integer getTolkID() {
        return tolkID;
    }

    public void setTolkID(Integer tolkID) {
        this.tolkID = tolkID;
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

    public Integer getPostnr() {
        return postnr;
    }

    public void setPostnr(Integer postnr) {
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
    public Collection<Opgave> getOpgaveCollection1() {
        return opgaveCollection1;
    }

    public void setOpgaveCollection1(Collection<Opgave> opgaveCollection1) {
        this.opgaveCollection1 = opgaveCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tolkID != null ? tolkID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tolk)) {
            return false;
        }
        Tolk other = (Tolk) object;
        if ((this.tolkID == null && other.tolkID != null) || (this.tolkID != null && !this.tolkID.equals(other.tolkID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Tolk[ tolkID=" + tolkID + " ]";
    }
    
}
