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

/**
 *
 * @author Bobbie
 */
public class Facade {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("TolkeAdministrationPU");
    EntityManager em = emf.createEntityManager();

    public Collection<Opgave> getAllOpgave(){
        return em.createNamedQuery("Opgave.findAll").getResultList();
        }
    
    public synchronized Bruger createBruger(Bruger b) {
        em.getTransaction().begin();
        em.persist(b);
        em.getTransaction().commit();
        return b;
    }
    
    public synchronized Bruger updateBruger(Bruger b){
        em.getTransaction().begin();
        em.refresh(b);
        em.getTransaction().commit();
        return b;
    }
    
    public synchronized Bruger getBrugerById(int i){
        return em.createNamedQuery("Bruger.findByBrugerID", Bruger.class).setParameter("brugerID", i).getSingleResult();
    }
    public static void main(String[] args) {
        Facade f = new Facade();
     /*   Bruger b = new Bruger();
        b.setNavn("test");
        b.setEfternavn("testsen");
        f.createBruger(b);
        System.out.println(f.getBrugerById(1));
        System.out.println(f.getBrugerById(1).getEfternavn());/*
        
        */
        
        for (Opgave opg : f.getAllOpgave()) {
            System.out.println(opg.getTolkCollection().size());
            
        }

        
        
    }
}


