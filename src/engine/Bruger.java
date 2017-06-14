/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author Bobbie
 */
public class Bruger {
    String navn;
    String adresse;
    String email;
    int telefon;
    int cpr;

    public Bruger(String navn, String adresse, String email, int telefon, int cpr) {
        this.navn = navn;
        this.adresse = adresse;
        this.email = email;
        this.telefon = telefon;
        this.cpr = cpr;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefon() {
        return telefon;
    }

    public void setTelefon(int telefon) {
        this.telefon = telefon;
    }

    public int getCpr() {
        return cpr;
    }

    public void setCpr(int cpr) {
        this.cpr = cpr;
    }
    
    
}
