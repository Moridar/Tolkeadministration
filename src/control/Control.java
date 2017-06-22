/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import entity.*;
import java.util.Collection;

/**
 *
 * @author Bobbie
 */
public class Control {

    Facade f;

    public Control() {
        f = new Facade();
    }

    public Opgave refreshOpgave(Opgave o){
        return f.refreshOpgave(o);
    }
    
    public Bevilling SetBevilling(Bevilling b) {
        if (b.getNavn() != null) {
            if (b.getBevillingsnummer()== null) {
                f.createBevilling(b);
            } else {
                f.updateBevilling(b);
            }
        }
        return b;
    }
    public Opgave setOpgave(Opgave o) {
        if (o.getOpgavenummer() == null) {
            f.createOpgave(o);
        } else {
            f.updateOpgave(o);
        }
        return o;
    }
    
    public Opgave getOpgaveById (int id){
        return f.getOpgaveById(id);
    }

    public Bruger SetBruger(Bruger b) {
        if (b.getNavn() != null && b.getEfternavn() != null) {
            if (b.getBrugerID() == null) {
                f.createBruger(b);
            } else {
                f.updateBruger(b);
            }
        }
        return b;
    }

    public Bruger getBrugerById(int id) {
        return f.getBrugerById(id);
    }

    public Tolk SetTolk(Tolk b) {
        if (b.getNavn() != null && b.getEfternavn() != null) {
            if (b.getTolkID() == null) {
                f.createTolk(b);
            } else {
                f.updateTolk(b);
            }
        }
        return b;
    }

    public Tolk getTolkById(int id) {
        return f.getTolkById(id);
    }

    public Collection<Tolk> getAllTolk() {
        return f.getAllTolk();
    }

    public Collection<Bruger> getAllBruger() {
        return new Facade().getAllBruger();
    }

    public Collection<Opgave> getAllOpgave() {
        return f.getAllOpgave();
    }

}
