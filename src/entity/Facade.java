/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

/**
 *
 * @author Bobbie
 */
public class Facade {

    EntityManagerFactory emf;
    EntityManager em;

    public Facade() {
        emf = Persistence.createEntityManagerFactory("TolkeAdministrationPU");
        em = emf.createEntityManager();
    }

    public Opgave refreshOpgave(Opgave o){
        em.getTransaction().begin();
        em.refresh(o);
        em.getTransaction().commit();
        return o;
    }
    
    public Collection<Tolk> getAllTolk() {
        return em.createNamedQuery("Tolk.findAll").getResultList();
    }

    public Collection<Bruger> getAllBruger() {

        return em.createNamedQuery("Bruger.findAll").getResultList();
    }

    public Collection<Opgave> getAllOpgave() {
        return em.createNamedQuery("Opgave.findAllOrderByDato").getResultList();
    }
//Ved problem, tilføj det her under Opgave.class @NamedQuery(name = "Opgave.findAllOrderByDato", query = "SELECT o FROM Opgave o ORDER BY o.dato"),

    public Bevilling createBevilling(Bevilling b) {
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        System.out.println("created " + b);
        return b;
    }
    public Bevilling updateBevilling(Bevilling b) {
        em.getTransaction().begin();
        em.merge(b);
        em.getTransaction().commit();
        System.out.println("updated " + b);
        return b;
    }

    public synchronized Bruger createBruger(Bruger b) {
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        System.out.println("created " + b);
        return b;
    }

    public synchronized Bruger updateBruger(Bruger b) {
        em.getTransaction().begin();
        em.merge(b);
        em.getTransaction().commit();
        System.out.println("updated " + b);
        return b;
    }

    public synchronized Bruger getBrugerById(int i) {
        return em.createNamedQuery("Bruger.findByBrugerID", Bruger.class).setParameter("brugerID", i).getSingleResult();
    }

    public synchronized Tolk createTolk(Tolk b) {
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        System.out.println("created " + b);
        return b;
    }

    public synchronized Tolk updateTolk(Tolk b) {
        em.getTransaction().begin();
        em.merge(b);
        em.getTransaction().commit();
        System.out.println("updated " + b);
        return b;
    }

    public synchronized Tolk getTolkById(int i) {
        return em.createNamedQuery("Tolk.findByTolkID", Tolk.class).setParameter("tolkID", i).getSingleResult();
    }

    public Opgave createOpgave(Opgave o) {
        em.getTransaction().begin();
        em.persist(o);
        em.getTransaction().commit();
        return o;
    }

    public Opgave updateOpgave(Opgave o) {
        em.getTransaction().begin();
        em.merge(o);
        em.getTransaction().commit();
        return o;
    }

    public Opgave getOpgaveById(int id) {
        return em.createNamedQuery("Opgave.findByOpgavenummer", Opgave.class).setParameter("opgavenummer", id).getSingleResult();
    }

    /*---------------  TEST DATA BELOW ------------ */
    public static void main(String[] args) {
        Facade f = new Facade();
        Bruger b = new Bruger();
        b.setNavn("test");
        b.setEfternavn("testsen");
        b = f.createBruger(b);
        System.out.println(b.getBrugerID());
        System.out.println(b.getEfternavn());
        b.setEfternavn("lolsen");
        b = f.updateBruger(b);
        System.out.println(b.getBrugerID());
        System.out.println(b.getEfternavn());

        /*
         for (Opgave opg : f.getAllOpgave()) {
         System.out.println(opg.getTolkCollection().size());
            
         }*/
    }
}
