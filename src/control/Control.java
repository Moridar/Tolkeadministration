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
    
    public Collection<Opgave> getAllOpgave(){
        return f.getAllOpgave();
    }
    
    
}
