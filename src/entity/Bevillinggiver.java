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
@Table(name = "bevillinggiver")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bevillinggiver.findAll", query = "SELECT b FROM Bevillinggiver b"),
    @NamedQuery(name = "Bevillinggiver.findByGiverID", query = "SELECT b FROM Bevillinggiver b WHERE b.giverID = :giverID"),
    @NamedQuery(name = "Bevillinggiver.findByNavn", query = "SELECT b FROM Bevillinggiver b WHERE b.navn = :navn"),
    @NamedQuery(name = "Bevillinggiver.findByTelefon", query = "SELECT b FROM Bevillinggiver b WHERE b.telefon = :telefon"),
    @NamedQuery(name = "Bevillinggiver.findByEmail", query = "SELECT b FROM Bevillinggiver b WHERE b.email = :email")})
public class Bevillinggiver implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "GiverID")
    private Integer giverID;
    @Basic(optional = false)
    @Column(name = "Navn")
    private String navn;
    @Basic(optional = false)
    @Column(name = "Telefon")
    private String telefon;
    @Basic(optional = false)
    @Column(name = "Email")
    private String email;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "giver")
    private Collection<Bevilling> bevillingCollection;

    public Bevillinggiver() {
    }

    public Bevillinggiver(Integer giverID) {
        this.giverID = giverID;
    }

    public Bevillinggiver(Integer giverID, String navn, String telefon, String email) {
        this.giverID = giverID;
        this.navn = navn;
        this.telefon = telefon;
        this.email = email;
    }

    public Integer getGiverID() {
        return giverID;
    }

    public void setGiverID(Integer giverID) {
        this.giverID = giverID;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
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

    @XmlTransient
    public Collection<Bevilling> getBevillingCollection() {
        return bevillingCollection;
    }

    public void setBevillingCollection(Collection<Bevilling> bevillingCollection) {
        this.bevillingCollection = bevillingCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (giverID != null ? giverID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bevillinggiver)) {
            return false;
        }
        Bevillinggiver other = (Bevillinggiver) object;
        if ((this.giverID == null && other.giverID != null) || (this.giverID != null && !this.giverID.equals(other.giverID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Bevillinggiver[ giverID=" + giverID + " ]";
    }
    
}
