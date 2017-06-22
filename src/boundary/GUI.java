/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boundary;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import control.Control;
import entity.*;
import java.awt.Component;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bobbie
 */
public class GUI extends javax.swing.JFrame {

    Control c;
    ArrayList<Bruger> brugerList;
    ArrayList<Bevilling> bevillingList;
    ArrayList<Tolk> tolkList;
    ArrayList<Opgave> opgaveList;
    String searchFor;
    Opgave opg; //Holder
    Bevilling currBevilling;

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        c = new Control();
        jTabbedPane1.remove(jPanelBrugerDetail);
        jTabbedPane1.remove(jPanelTolkDetail);
        jTabbedPane1.remove(jPanelBestillingDetail);
        jTabbedPane1.remove(jPanelBevillingDetail);
    }

    private void clearAllFejlMeddelse() {
        jLabelBrugerDetailFejlMeddelelse.setText("");
    }

    private void setUpTableBestilling() {
        Collection<Opgave> opgaver = c.getAllOpgave();
        DefaultTableModel model = (DefaultTableModel) jTableBestillinger.getModel();
        model.setRowCount(0);
        boolean visKunManglendeBevilling = jCheckBoxBestillingerBevilling.isSelected();
        boolean visKunManglendeTolk = jCheckBoxBestillingerTolke.isSelected();
        boolean visKunKommende = jCheckBoxKommende.isSelected();

        for (Opgave opg : opgaver) {
            String dato = "";
            String by = "";
            String lokal = "";
            String tolke = "";
            String ekstra = "";
            String adresse = "";
            int tid = 0;
            int sluttid = 0;

            int nr = opg.getOpgavenummer();
            dato = opg.getDato().getYear() - 100 + "-" + (opg.getDato().getMonth() + 1) + "-" + opg.getDato().getDate();

            if (opg.getStartTid() != null) {
                tid = opg.getStartTid();
            }
            if (opg.getSlutTid() != null) {
                sluttid = opg.getSlutTid();
            }
            adresse = opg.getAdresse();
            if (opg.getPostnr() != null) {
                by = opg.getPostnr().toString();
            }
            lokal = opg.getLokal();
            String bestiller = opg.getBestiller().getNavn() + " " + opg.getBestiller().getEfternavn();
            Boolean bevilling = opg.getBevillingsnummer() != null;

            ekstra = opg.getEkstra();
            int tolkeTilknyttet = opg.getTolkCollection().size();

            boolean filledTolk = false;
            if (opg.getAntaltolk() == null) {
                tolke = tolkeTilknyttet + "";
                if (tolkeTilknyttet != 0) {
                    filledTolk = true;
                }
            } else {
                tolke = tolkeTilknyttet + "/" + opg.getAntaltolk();
                if (tolkeTilknyttet == opg.getAntaltolk()) {
                    filledTolk = true;
                }
                if (opg.getAntaltolk() == 0) {
                    filledTolk = false;
                }
            }

            Date tmw = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
            boolean isDone = opg.getDato().before(tmw);

            Object[] row = {nr, dato, tid, sluttid, bestiller, adresse, by, lokal, ekstra, bevilling, tolke};
            if ((visKunManglendeBevilling == !bevilling || !visKunManglendeBevilling) && (visKunManglendeTolk != filledTolk || !visKunManglendeTolk) && (visKunKommende != isDone || !visKunKommende)) {
                model.addRow(row);
            }
        }

    }

    private void setUpBrugere() {
        Collection<Bruger> brugere = c.getAllBruger();
        DefaultTableModel model = (DefaultTableModel) jTableBruger.getModel();
        model.setRowCount(0); //Clear

        for (Bruger bruger : brugere) {
            int id = bruger.getBrugerID();
            String navn = bruger.getNavn() + " " + bruger.getEfternavn();
            String telefon = bruger.getTelefon();
            String email = bruger.getEmail();
            String adresse = bruger.getAdresse();
            String postnr = bruger.getPostnr();

            Object[] row = {id, navn, telefon, email, adresse, postnr};
            model.addRow(row);
        }
    }

    private void setUpListBrugere() {
        Collection<Bruger> brugere = c.getAllBruger();
        DefaultListModel model = new DefaultListModel();
        String keyWord = jTextFieldBestillingSøg.getText();
        brugerList = new ArrayList<>();
        for (Bruger b : brugere) {
            if (brugerContainsKeyword(b, keyWord) && opg.getBestiller() != b) {
                if (searchFor == "bruger" || (searchFor == "modtagere" && !opg.getBrugerCollection().contains(b))) {
                    Object e = b.getNavn() + " " + b.getEfternavn();
                    model.addElement(e);
                    brugerList.add(b);
                }
            }
        }
        jListBestilling.setModel(model);
    }

    private void setUpListBevilling(JList jList) {
        Collection<Bruger> brugere = new ArrayList<>();
        String keyWord = "";

        if (jList == jListBestilling) {
            if (opg.getBestiller() != null) {
                brugere.add(opg.getBestiller());
            }
            if (opg.getBrugerCollection() != null) {
                brugere.addAll(opg.getBrugerCollection());
            }
            keyWord = jTextFieldBestillingSøg.getText();
        } else if (jList == jListBrugerBevillinger) {
            int brugerID = -1;
            Bruger b = null;
            try {
                brugerID = Integer.parseInt(jTextFieldBrugerDetailID.getText());
                b = c.getBrugerById(brugerID);
            } catch (Exception e) {
            }

            if (b != null) {
                brugere.add(b);
            }
        }
        DefaultListModel model = new DefaultListModel();

        bevillingList = new ArrayList<>();

        for (Bruger br : brugere) {
            for (Bevilling b : br.getBevillingCollection()) {
                if (bevillingContainsKeyword(b, keyWord) && !bevillingList.contains(b)) {
                    Object e = br.getNavn().substring(0, 1) + br.getEfternavn().substring(0, 1) + ": " + b.getNavn() + " " + calcBrugtTimer(b) + "/" + b.getTimer();
                    model.addElement(e);
                    bevillingList.add(b);
                }
            }

        }
        if (bevillingList.isEmpty()) {
            model.addElement("<Ingen bevillinger>");
        }

        jList.setModel(model);
    }

    private void setUpListTolke() {
        Collection<Tolk> tolke = c.getAllTolk();
        DefaultListModel model = new DefaultListModel();

        String keyWord = jTextFieldBestillingSøg.getText();
        tolkList = new ArrayList<>();
        for (Tolk t : tolke) {
            if (tolkContainsKeyword(t, keyWord) && !opg.getTolkCollection().contains(t)) {
                Object e = t.getNavn() + " " + t.getEfternavn();
                model.addElement(e);
                tolkList.add(t);
            }
        }
        jListBestilling.setModel(model);

    }

    private void setUpListTolkeOpgaver() {
        int selectedTolk = -1;
        try {
            selectedTolk = jListBestilling.getSelectedIndex();
        } catch (Exception e) {
            selectedTolk = -1;
        }

        if (selectedTolk >= 0) {
            Tolk t = tolkList.get(selectedTolk);
            jPanelBestillingEkstra.setVisible(true);
            Collection<Opgave> opgaver = t.getOpgaveCollection();
            DefaultListModel model = new DefaultListModel();

            Date opgDate = convertDate(jTextFieldBestillingDato.getText());
            if (!opgaver.isEmpty()) {
                for (Opgave o : opgaver) {
                    if (o.getDato().equals(opgDate)) {
                        Object e = o.getStartTid() + " - " + o.getSlutTid();

                        if (o.getPostnr() != null) {
                            e += ", i " + o.getPostnr();
                        } else {
                            e += ", " + "ukendt lokation";
                        }

                        model.addElement(e);
                    }
                }
            }
            if (model.isEmpty()) {
                model.addElement("<Ingen opgaver denne dag>");
            }

            jListBestilling2.setModel(model);
        } else {
            jPanelBestillingEkstra.setVisible(false);
        }
    }

    private boolean bevillingContainsKeyword(Bevilling b, String keyWord) {
        keyWord = keyWord.toLowerCase();
        if (b.getNavn() != null && b.getNavn().contains(keyWord)) {
            return true;
        }

        return false;
    }

    private boolean brugerContainsKeyword(Bruger b, String keyWord) {
        keyWord = keyWord.toLowerCase();

        if (b.getAdresse() != null && b.getAdresse().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getBrugerID().toString().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getEfternavn().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getNavn().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getEmail() != null && b.getEmail().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getPostnr() != null && b.getPostnr().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (b.getTelefon() != null && b.getTelefon().toLowerCase().contains(keyWord)) {
            return true;
        }
        return false;
    }

    private boolean tolkContainsKeyword(Tolk t, String keyWord) {
        keyWord = keyWord.toLowerCase();

        if (t.getAdresse() != null && t.getAdresse().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getTolkID().toString().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getEfternavn().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getNavn().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getEmail() != null && t.getEmail().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getPostnr() != null && t.getPostnr().toLowerCase().contains(keyWord)) {
            return true;
        }
        if (t.getTelefon() != null && t.getTelefon().toLowerCase().contains(keyWord)) {
            return true;
        }
        return false;
    }

    private void setUpTolke() {
        Collection<Tolk> tolke = c.getAllTolk();
        DefaultTableModel model = (DefaultTableModel) jTableTolk.getModel();
        model.setRowCount(0); //Clear

        for (Tolk tolk : tolke) {
            int id = tolk.getTolkID();
            String navn = tolk.getNavn() + " " + tolk.getEfternavn();
            String telefon = tolk.getTelefon();
            String email = tolk.getEmail();
            String adresse = tolk.getAdresse();
            String postnr = tolk.getPostnr();

            Object[] row = {id, navn, telefon, email, adresse, postnr};
            model.addRow(row);
        }
    }

    private void enableBrugerDetail() {
        jTabbedPane1.add(jPanelBrugerDetail);
        jTabbedPane1.setSelectedComponent(jPanelBrugerDetail);
        jTabbedPane1.setTitleAt(jTabbedPane1.getSelectedIndex(), "Bruger detajler");
    }

    private void enableTolkDetail() {
        jTabbedPane1.add(jPanelTolkDetail);
        jTabbedPane1.setSelectedComponent(jPanelTolkDetail);
        jTabbedPane1.setTitleAt(jTabbedPane1.getSelectedIndex(), "Tolk detajler");
    }

    private void enableBestilling() {
        jTabbedPane1.add(jPanelBestillingDetail);
        jTabbedPane1.setSelectedComponent(jPanelBestillingDetail);
        jTabbedPane1.setTitleAt(jTabbedPane1.getSelectedIndex(), "Bestilling detajler");
    }

    private void enableBevillingDetail() {
        jTabbedPane1.add(jPanelBevillingDetail);
        jTabbedPane1.setSelectedComponent(jPanelBevillingDetail);
        jTabbedPane1.setTitleAt(jTabbedPane1.getSelectedIndex(), "Bevilling detajler");
    }

    private void disableBevillingDetail() {
        jTabbedPane1.setSelectedComponent(jPanelBrugerDetail);
        jTabbedPane1.remove(jPanelBevillingDetail);
    }

    private void disableBrugerDetail() {
        jTabbedPane1.setSelectedComponent(jPanelBrugere);
        jTabbedPane1.remove(jPanelBrugerDetail);
    }

    private void disableTolkDetail() {
        jTabbedPane1.setSelectedComponent(jPanelTolke);
        jTabbedPane1.remove(jPanelTolkDetail);
    }

    private void disableBestilling() {
        jTabbedPane1.setSelectedComponent(jPanelBestillinger);
        jTabbedPane1.remove(jPanelBestillingDetail);
    }

    private void visBestilling() {
        int bestillingID;
        try {
            bestillingID = Integer.parseInt(jTableBestillinger.getModel().getValueAt(jTableBestillinger.getSelectedRow(), 0).toString());
        } catch (Exception e) {
            bestillingID = -1;
        }

        if (bestillingID >= 0) {
            opg = c.getOpgaveById(bestillingID);
            enableBestilling();
            updateBestillingDetajler();
        }

    }

    private void redigerBevilling(Bevilling b) {
        currBevilling = b;
        if (b.getBevillingsnummer() == null) { // NY
            jTextFieldBevillingID.setText("Ny");
            jTextFieldBevillingModtager.setText(b.getBruger().getNavn() + " " + b.getBruger().getEfternavn());
            jTextFieldBevillingGiver.setText("");
            jTextFieldBevillingOpgave.setText("");
            jTextFieldBevillingAntalTimer.setText("0");
            jTextFieldBevillingBrugtTimer.setText("0");
        } else {
            jTextFieldBevillingID.setText(b.getBevillingsnummer().toString());
            jTextFieldBevillingModtager.setText(b.getBruger().getNavn() + " " + b.getBruger().getEfternavn());
            jTextFieldBevillingGiver.setText(b.getNavn());
            jTextFieldBevillingOpgave.setText(b.getOpgave());
            jTextFieldBevillingAntalTimer.setText("" + b.getTimer());
            jTextFieldBevillingBrugtTimer.setText("" + calcBrugtTimer(b));
        }
        jLabelBevillingDetailFejlMeddelelse1.setText("");
        enableBevillingDetail();
    }

    private void redigerBruger(int brugerID) {
        if (brugerID != -1) {
            Bruger b = c.getBrugerById(brugerID);
            jTextFieldBrugerDetailID.setText(b.getBrugerID().toString());
            jTextFieldBrugerDetailNavn.setText(b.getNavn());
            jTextFieldBrugerDetailEfternavn.setText(b.getEfternavn());
            jTextFieldBrugerDetailTelefon.setText(b.getTelefon());
            jTextFieldBrugerDetailEmail.setText(b.getEmail());
            jTextFieldBrugerDetailAdresse.setText(b.getAdresse());
            jTextFieldBrugerDetailPostNR.setText(b.getPostnr());
            jPanelBrugerBevilling.setVisible(true);
        } else {
            jTextFieldBrugerDetailID.setText("Ny");
            jTextFieldBrugerDetailNavn.setText("");
            jTextFieldBrugerDetailEfternavn.setText("");
            jTextFieldBrugerDetailTelefon.setText("");
            jTextFieldBrugerDetailEmail.setText("");
            jTextFieldBrugerDetailAdresse.setText("");
            jTextFieldBrugerDetailPostNR.setText("");
            jPanelBrugerBevilling.setVisible(false);
        }
        enableBrugerDetail();
        setUpListBevilling(jListBrugerBevillinger);
    }

    private void redigerTolk() {
        int tolkID;
        try {
            tolkID = Integer.parseInt(jTableTolk.getModel().getValueAt(jTableTolk.getSelectedRow(), 0).toString());
        } catch (Exception e) {
            tolkID = -1;
        }

        if (tolkID != -1) {
            enableTolkDetail();
            Tolk b = c.getTolkById(tolkID);
            jTextFieldTolkDetailID.setText(b.getTolkID().toString());
            jTextFieldTolkDetailNavn.setText(b.getNavn());
            jTextFieldTolkDetailEfternavn.setText(b.getEfternavn());
            jTextFieldTolkDetailTelefon.setText(b.getTelefon());
            jTextFieldTolkDetailEmail.setText(b.getEmail());
            jTextFieldTolkDetailAdresse.setText(b.getAdresse());
            jTextFieldTolkDetailPostNR.setText(b.getPostnr());
        }
    }

    private void disableBestillingSearch() {
        jPanelBestillingList.setVisible(false);
        jPanelBestillingEkstra.setVisible(false);

    }

    private String convertDate(Date date) {
        return date.getYear() + 1900 + "-" + (date.getMonth() + 1) + "-" + date.getDate();
    }

    private Date convertDate(String string) {
        String[] strings = string.split("-");

        try {
            int year = Integer.parseInt(strings[0]) - 1900;
            int month = Integer.parseInt(strings[1]) - 1;
            int day = Integer.parseInt(strings[2]);
            return new Date(year, month, day);
        } catch (Exception e) {
            return new Date();
        }

    }

    private void updateBestillingDetajler() {
        jTextFieldBestillingAdresse.setText("");
        jTextFieldBestillingBestiller.setText("");
        jTextFieldBestillingBevilling.setText("");
        jTextFieldBestillingExtra.setText("");
        jTextFieldBestillingID.setText("Ny");
        jTextFieldBestillingLokal.setText("");
        jTextFieldBestillingModtagere.setText("");
        jTextFieldBestillingPostNR.setText("");
        jTextFieldBestillingSlutTid.setText("");
        jTextFieldBestillingStartTid.setText("");
        jTextFieldBestillingTolke.setText("");
        jTextFieldBestillingType.setText("");

        if (opg.getOpgavenummer() != null) {
            jTextFieldBestillingID.setText(opg.getOpgavenummer().toString());
            opg = c.getOpgaveById(opg.getOpgavenummer());
        }
        if (opg.getBestiller() != null) {
            jTextFieldBestillingBestiller.setText(opg.getBestiller().getNavn() + " " + opg.getBestiller().getEfternavn());
        }
        //Modtagere (ved flere)
        if (opg.getBrugerCollection() != null) {
            int i = 0;
            String output = "";
            for (Bruger b : opg.getBrugerCollection()) {
                if (i++ != 0) {
                    output += ", ";
                }
                output += b.getNavn() + " " + b.getEfternavn();
            }
            jTextFieldBestillingModtagere.setText(output);
        }
        if (opg.getBevillingsnummer() != null) {
            jTextFieldBestillingBevilling.setText(opg.getBevillingsnummer().getNavn());
        }
        if (opg.getType() != null) {
            jTextFieldBestillingType.setText(opg.getType());
        }
        if (opg.getAdresse() != null) {
            jTextFieldBestillingAdresse.setText(opg.getAdresse());
        }
        if (opg.getPostnr() != null) {
            jTextFieldBestillingPostNR.setText(opg.getPostnr().toString());
        }
        if (opg.getLokal() != null) {
            jTextFieldBestillingLokal.setText(opg.getLokal());
        }

        if (opg.getDato() == null) {
            opg.setDato((new Date()));
        }
        if (opg.getDato() != null) {
            jTextFieldBestillingDato.setText(convertDate(opg.getDato()));
        }
        if (opg.getStartTid() != null) {
            jTextFieldBestillingStartTid.setText(opg.getStartTid().toString());
        }
        if (opg.getSlutTid() != null) {
            jTextFieldBestillingSlutTid.setText(opg.getSlutTid().toString());
        }
        if (opg.getAntaltolk() != null) {
            jSpinnerAntalTolke.setValue(opg.getAntaltolk());
        }
        if (opg.getEkstra() != null) {
            jTextFieldBestillingExtra.setText(opg.getEkstra());
        }
        if (opg.getTolkCollection() != null) {
            int i = 0;
            String output = "";
            for (Tolk t : opg.getTolkCollection()) {
                if (i++ != 0) {
                    output += ", ";
                }
                output += t.getNavn() + " " + t.getEfternavn();
            }
            jTextFieldBestillingTolke.setText(output);
        }
    }

    private boolean checkTolkNumber() {
        System.out.println(opg.getTolkCollection().size());
        if (opg.getAntaltolk() == null) {
            opg.setAntaltolk(1);
        }
        if (opg.getAntaltolk() < opg.getTolkCollection().size()) {
            int i = 0;
            ArrayList<Tolk> tolkeToRemove = new ArrayList<>();
            
            for (Tolk t : opg.getTolkCollection()) {
                if (opg.getAntaltolk() <= i++) {
                    t.getOpgaveCollection().remove(opg);
                    //tolkeToRemove.add(t);
                    c.SetTolk(t);
                }
            }
            
            //opg.getTolkCollection().removeAll(tolkeToRemove);
            opg = c.refreshOpgave(opg);
            updateBestillingDetajler();
        }
        return (opg.getAntaltolk() == opg.getTolkCollection().size());
        //True if filled, false if need more.
    }

    private void enableTolkeList() {
        jPanelBestillingList.setVisible(true);
        searchFor = "tolke";
        jTextFieldBestillingSøg.setText("");
        setUpListTolke();
        jButtonBestillingTilføj.setText("Tilføj");
        if (checkTolkNumber()) {
            jButtonBestillingTilføj.setEnabled(false);
        } else {
            jButtonBestillingTilføj.setEnabled(true);
        }

    }

    private void resetBestillingList() {
        jPanelBestillingList.setVisible(false);
        jPanelBestillingEkstra.setVisible(false);
    }

    private float calcBrugtTimer(Bevilling b) {
        float brugtTimer = 0;
        for (Opgave opg : b.getOpgaveCollection()) {
            if (opg.getStartTid() != null && opg.getSlutTid() != null) {
                int startHour = opg.getStartTid() / 100;
                int startMin = opg.getStartTid() % 100;
                int slutHour = opg.getSlutTid() / 100;
                int slutMin = opg.getSlutTid() % 100;

                System.out.println(slutHour + "-" + startHour + "+ (" + slutMin + "-" + startMin + ")");
                float opglength = (float) (slutHour - startHour + ((slutMin - startMin) / 60.0));
                int antalTolke = 1;
                antalTolke = opg.getAntaltolk();
                brugtTimer += opglength * antalTolke;
            }
        }
        return brugtTimer;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanelBestillinger = new javax.swing.JPanel();
        jScrollPaneBestillinger = new javax.swing.JScrollPane();
        jTableBestillinger = new javax.swing.JTable();
        jLabelBestillingerSearch = new javax.swing.JLabel();
        jTextFieldBestillingerSearch = new javax.swing.JTextField();
        jButtonBestillingerOpretNyt = new javax.swing.JButton();
        jButtonBestillingerVis = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jCheckBoxBestillingerBevilling = new javax.swing.JCheckBox();
        jCheckBoxBestillingerTolke = new javax.swing.JCheckBox();
        jCheckBoxKommende = new javax.swing.JCheckBox();
        jPanelBrugere = new javax.swing.JPanel();
        jLabelBrugerSearch = new javax.swing.JLabel();
        jTextFieldBrugerSearch = new javax.swing.JTextField();
        jScrollPaneBruger = new javax.swing.JScrollPane();
        jTableBruger = new javax.swing.JTable();
        jButtonBrugerOpretNyt = new javax.swing.JButton();
        jButtonBrugerRediger = new javax.swing.JButton();
        jPanelTolke = new javax.swing.JPanel();
        jLabelTolkSearch = new javax.swing.JLabel();
        jTextFieldTolkSearch = new javax.swing.JTextField();
        jScrollPaneTolk = new javax.swing.JScrollPane();
        jTableTolk = new javax.swing.JTable();
        jButtonTolkOpretNyt = new javax.swing.JButton();
        jButtonTolkRediger = new javax.swing.JButton();
        jPanelTolkDetail = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldTolkDetailNavn = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldTolkDetailEfternavn = new javax.swing.JTextField();
        jTextFieldTolkDetailTelefon = new javax.swing.JTextField();
        jTextFieldTolkDetailEmail = new javax.swing.JTextField();
        jTextFieldTolkDetailAdresse = new javax.swing.JTextField();
        jTextFieldTolkDetailPostNR = new javax.swing.JTextField();
        jButtonTolkDetailGem = new javax.swing.JButton();
        jButtonTolkDetailFortryd = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldTolkDetailID = new javax.swing.JTextField();
        jLabelTolkDetailFejlMeddelelse = new javax.swing.JLabel();
        jPanelBestillingDetail = new javax.swing.JPanel();
        jLabelBestillingBestiller = new javax.swing.JLabel();
        jLabelBestillingBevilling = new javax.swing.JLabel();
        jTextFieldBestillingBestiller = new javax.swing.JTextField();
        jLabelBestillingType = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldBestillingBevilling = new javax.swing.JTextField();
        jTextFieldBestillingType = new javax.swing.JTextField();
        jTextFieldBestillingAdresse = new javax.swing.JTextField();
        jTextFieldBestillingPostNR = new javax.swing.JTextField();
        jTextFieldBestillingDato = new javax.swing.JTextField();
        jButtonBestillingGem = new javax.swing.JButton();
        jButtonBestillingLuk = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jTextFieldBestillingID = new javax.swing.JTextField();
        jLabelBestillingFejlMed = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextFieldBestillingStartTid = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextFieldBestillingSlutTid = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabelBestillingModtagere = new javax.swing.JLabel();
        jTextFieldBestillingModtagere = new javax.swing.JTextField();
        jButtonBestillingHentAndenBestilling = new javax.swing.JButton();
        jPanelBestillingList = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListBestilling = new javax.swing.JList();
        jLabelSearch = new javax.swing.JLabel();
        jTextFieldBestillingSøg = new javax.swing.JTextField();
        jButtonBestillingTilføj = new javax.swing.JButton();
        jPanelBestillingEkstra = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListBestilling2 = new javax.swing.JList();
        jLabelList = new javax.swing.JLabel();
        jSpinnerAntalTolke = new javax.swing.JSpinner();
        jTextFieldBestillingTolke = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jTextFieldBestillingLokal = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextFieldBestillingExtra = new javax.swing.JTextField();
        jPanelBrugerDetail = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldBrugerDetailNavn = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldBrugerDetailEfternavn = new javax.swing.JTextField();
        jTextFieldBrugerDetailTelefon = new javax.swing.JTextField();
        jTextFieldBrugerDetailEmail = new javax.swing.JTextField();
        jTextFieldBrugerDetailAdresse = new javax.swing.JTextField();
        jTextFieldBrugerDetailPostNR = new javax.swing.JTextField();
        jButtonBrugerDetailGem = new javax.swing.JButton();
        jButtonBrugerDetailFortryd = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldBrugerDetailID = new javax.swing.JTextField();
        jLabelBrugerDetailFejlMeddelelse = new javax.swing.JLabel();
        jPanelBrugerBevilling = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPaneBrugerBevilling = new javax.swing.JScrollPane();
        jListBrugerBevillinger = new javax.swing.JList();
        jButtonBrugerBevillingOpret = new javax.swing.JButton();
        jButtonBrugerBevillingVis = new javax.swing.JButton();
        jButtonBrugerBevillingSlet = new javax.swing.JButton();
        jPanelBevillingDetail = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldBevillingModtager = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jTextFieldBevillingGiver = new javax.swing.JTextField();
        jTextFieldBevillingOpgave = new javax.swing.JTextField();
        jTextFieldBevillingAntalTimer = new javax.swing.JTextField();
        jTextFieldBevillingBrugtTimer = new javax.swing.JTextField();
        jButtonBevillingGem = new javax.swing.JButton();
        jButtonBevillingAnnuler = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jTextFieldBevillingID = new javax.swing.JTextField();
        jLabelBevillingDetailFejlMeddelelse1 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jButtonBevillingUploadPDF = new javax.swing.JButton();
        jButtonBevillingHentPDF = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLabel1.setText("Tolke Administration");
        jLabel1.setToolTipText("");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel2.setText("Velkommen");

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(268, 268, 268)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(358, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(450, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Hovedside", jPanelMain);

        jTableBestillinger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nr", "Dato", "Tid", "Sluttid", "Bestiller", "Adresse", "By", "Lokal", "Ekstra info", "Bevilling", "Tolke"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableBestillinger.getTableHeader().setReorderingAllowed(false);
        jScrollPaneBestillinger.setViewportView(jTableBestillinger);
        if (jTableBestillinger.getColumnModel().getColumnCount() > 0) {
            jTableBestillinger.getColumnModel().getColumn(0).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTableBestillinger.getColumnModel().getColumn(1).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTableBestillinger.getColumnModel().getColumn(2).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(2).setPreferredWidth(40);
            jTableBestillinger.getColumnModel().getColumn(3).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(3).setPreferredWidth(40);
            jTableBestillinger.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableBestillinger.getColumnModel().getColumn(9).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(9).setPreferredWidth(57);
            jTableBestillinger.getColumnModel().getColumn(10).setResizable(false);
            jTableBestillinger.getColumnModel().getColumn(10).setPreferredWidth(47);
        }

        jLabelBestillingerSearch.setText("Søg:");

        jTextFieldBestillingerSearch.setText("jTextField1");

        jButtonBestillingerOpretNyt.setText("Opret ny");
        jButtonBestillingerOpretNyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingerOpretNytActionPerformed(evt);
            }
        });

        jButtonBestillingerVis.setText("Vis");
        jButtonBestillingerVis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingerVisActionPerformed(evt);
            }
        });

        jLabel3.setText("Vis kun:");

        jCheckBoxBestillingerBevilling.setText("Manglende bevilling");
        jCheckBoxBestillingerBevilling.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxBestillingerBevillingStateChanged(evt);
            }
        });
        jCheckBoxBestillingerBevilling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBestillingerBevillingActionPerformed(evt);
            }
        });

        jCheckBoxBestillingerTolke.setText("Manglende tolke");
        jCheckBoxBestillingerTolke.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxBestillingerTolkeStateChanged(evt);
            }
        });
        jCheckBoxBestillingerTolke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBestillingerTolkeActionPerformed(evt);
            }
        });

        jCheckBoxKommende.setText("Kommende");
        jCheckBoxKommende.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxKommendeStateChanged(evt);
            }
        });
        jCheckBoxKommende.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxKommendeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBestillingerLayout = new javax.swing.GroupLayout(jPanelBestillinger);
        jPanelBestillinger.setLayout(jPanelBestillingerLayout);
        jPanelBestillingerLayout.setHorizontalGroup(
            jPanelBestillingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneBestillinger, javax.swing.GroupLayout.DEFAULT_SIZE, 731, Short.MAX_VALUE)
            .addGroup(jPanelBestillingerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBestillingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBestillingerLayout.createSequentialGroup()
                        .addComponent(jLabelBestillingerSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBestillingerSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBestillingerOpretNyt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBestillingerVis))
                    .addGroup(jPanelBestillingerLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxKommende)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxBestillingerBevilling)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxBestillingerTolke)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelBestillingerLayout.setVerticalGroup(
            jPanelBestillingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBestillingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBestillingerSearch)
                    .addComponent(jTextFieldBestillingerSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBestillingerOpretNyt)
                    .addComponent(jButtonBestillingerVis))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBestillingerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jCheckBoxBestillingerBevilling)
                    .addComponent(jCheckBoxBestillingerTolke)
                    .addComponent(jCheckBoxKommende))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneBestillinger, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bestillinger", jPanelBestillinger);

        jLabelBrugerSearch.setText("Søg:");

        jTextFieldBrugerSearch.setText("jTextField1");

        jTableBruger.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Navn", "Telefon", "Email", "Adresse", "Postnr"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableBruger.getTableHeader().setReorderingAllowed(false);
        jScrollPaneBruger.setViewportView(jTableBruger);
        if (jTableBruger.getColumnModel().getColumnCount() > 0) {
            jTableBruger.getColumnModel().getColumn(0).setPreferredWidth(1);
            jTableBruger.getColumnModel().getColumn(2).setResizable(false);
        }

        jButtonBrugerOpretNyt.setText("Opret ny");
        jButtonBrugerOpretNyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerOpretNytActionPerformed(evt);
            }
        });

        jButtonBrugerRediger.setText("Vis");
        jButtonBrugerRediger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerRedigerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBrugereLayout = new javax.swing.GroupLayout(jPanelBrugere);
        jPanelBrugere.setLayout(jPanelBrugereLayout);
        jPanelBrugereLayout.setHorizontalGroup(
            jPanelBrugereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugereLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBrugereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneBruger)
                    .addGroup(jPanelBrugereLayout.createSequentialGroup()
                        .addComponent(jLabelBrugerSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBrugerSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBrugerOpretNyt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBrugerRediger)
                        .addGap(0, 209, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelBrugereLayout.setVerticalGroup(
            jPanelBrugereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugereLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBrugereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBrugerSearch)
                    .addComponent(jTextFieldBrugerSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrugerOpretNyt)
                    .addComponent(jButtonBrugerRediger))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneBruger, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Brugere", jPanelBrugere);

        jLabelTolkSearch.setText("Søg:");

        jTextFieldTolkSearch.setText("jTextField1");

        jTableTolk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Navn", "Telefon", "Email", "Adresse", "Postnr"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableTolk.getTableHeader().setReorderingAllowed(false);
        jScrollPaneTolk.setViewportView(jTableTolk);
        if (jTableTolk.getColumnModel().getColumnCount() > 0) {
            jTableTolk.getColumnModel().getColumn(0).setPreferredWidth(1);
            jTableTolk.getColumnModel().getColumn(2).setResizable(false);
        }

        jButtonTolkOpretNyt.setText("Opret ny");
        jButtonTolkOpretNyt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTolkOpretNytActionPerformed(evt);
            }
        });

        jButtonTolkRediger.setText("Vis");
        jButtonTolkRediger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTolkRedigerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTolkeLayout = new javax.swing.GroupLayout(jPanelTolke);
        jPanelTolke.setLayout(jPanelTolkeLayout);
        jPanelTolkeLayout.setHorizontalGroup(
            jPanelTolkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTolkeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTolkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTolk)
                    .addGroup(jPanelTolkeLayout.createSequentialGroup()
                        .addComponent(jLabelTolkSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTolkSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTolkOpretNyt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTolkRediger)
                        .addGap(0, 209, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTolkeLayout.setVerticalGroup(
            jPanelTolkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTolkeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTolkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTolkSearch)
                    .addComponent(jTextFieldTolkSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTolkOpretNyt)
                    .addComponent(jButtonTolkRediger))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneTolk, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tolke", jPanelTolke);

        jLabel11.setText("Navn:");

        jLabel12.setText("Efternavn:");

        jTextFieldTolkDetailNavn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTolkDetailNavnActionPerformed(evt);
            }
        });

        jLabel13.setText("Telefon:");

        jLabel14.setText("Email:");

        jLabel15.setText("Adresse:");

        jLabel16.setText("Post nr:");

        jTextFieldTolkDetailEfternavn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTolkDetailEfternavnActionPerformed(evt);
            }
        });

        jTextFieldTolkDetailEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTolkDetailEmailActionPerformed(evt);
            }
        });

        jButtonTolkDetailGem.setText("Gem");
        jButtonTolkDetailGem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTolkDetailGemActionPerformed(evt);
            }
        });

        jButtonTolkDetailFortryd.setText("Luk");
        jButtonTolkDetailFortryd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTolkDetailFortrydActionPerformed(evt);
            }
        });

        jLabel17.setText("ID:");

        jTextFieldTolkDetailID.setEditable(false);
        jTextFieldTolkDetailID.setText("Ny");

        jLabelTolkDetailFejlMeddelelse.setText("Fejlmeddelse");

        javax.swing.GroupLayout jPanelTolkDetailLayout = new javax.swing.GroupLayout(jPanelTolkDetail);
        jPanelTolkDetail.setLayout(jPanelTolkDetailLayout);
        jPanelTolkDetailLayout.setHorizontalGroup(
            jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTolkDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTolkDetailLayout.createSequentialGroup()
                        .addComponent(jButtonTolkDetailGem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTolkDetailFortryd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTolkDetailFejlMeddelelse, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addGap(347, 347, 347))
                    .addGroup(jPanelTolkDetailLayout.createSequentialGroup()
                        .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel11))
                                .addComponent(jLabel15)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldTolkDetailID, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldTolkDetailEfternavn, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addComponent(jTextFieldTolkDetailTelefon)
                                .addComponent(jTextFieldTolkDetailEmail)
                                .addComponent(jTextFieldTolkDetailAdresse)
                                .addComponent(jTextFieldTolkDetailPostNR)
                                .addComponent(jTextFieldTolkDetailNavn)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanelTolkDetailLayout.setVerticalGroup(
            jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTolkDetailLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextFieldTolkDetailID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextFieldTolkDetailNavn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTolkDetailEfternavn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldTolkDetailTelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextFieldTolkDetailEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextFieldTolkDetailAdresse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextFieldTolkDetailPostNR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTolkDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTolkDetailGem)
                    .addComponent(jButtonTolkDetailFortryd)
                    .addComponent(jLabelTolkDetailFejlMeddelelse))
                .addContainerGap(241, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tolk detajler", jPanelTolkDetail);

        jPanelBestillingDetail.setPreferredSize(new java.awt.Dimension(669, 495));

        jLabelBestillingBestiller.setText("Bestiller:");

        jLabelBestillingBevilling.setText("Bevilling:");

        jTextFieldBestillingBestiller.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingBestillerFocusGained(evt);
            }
        });
        jTextFieldBestillingBestiller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingBestillerActionPerformed(evt);
            }
        });

        jLabelBestillingType.setText("Type:");

        jLabel21.setText("Adresse");

        jLabel22.setText("Post NR");

        jLabel23.setText("Dato:");

        jTextFieldBestillingBevilling.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingBevillingFocusGained(evt);
            }
        });
        jTextFieldBestillingBevilling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingBevillingActionPerformed(evt);
            }
        });

        jTextFieldBestillingType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingTypeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingTypeFocusLost(evt);
            }
        });

        jTextFieldBestillingAdresse.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingAdresseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingAdresseFocusLost(evt);
            }
        });
        jTextFieldBestillingAdresse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingAdresseActionPerformed(evt);
            }
        });

        jTextFieldBestillingPostNR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingPostNRFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingPostNRFocusLost(evt);
            }
        });

        jTextFieldBestillingDato.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingDatoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingDatoFocusLost(evt);
            }
        });
        jTextFieldBestillingDato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingDatoActionPerformed(evt);
            }
        });

        jButtonBestillingGem.setText("Gem");
        jButtonBestillingGem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingGemActionPerformed(evt);
            }
        });

        jButtonBestillingLuk.setText("Luk");
        jButtonBestillingLuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingLukActionPerformed(evt);
            }
        });

        jLabel24.setText("ID:");

        jTextFieldBestillingID.setEditable(false);
        jTextFieldBestillingID.setText("Ny");
        jTextFieldBestillingID.setEnabled(false);

        jLabelBestillingFejlMed.setText("Fejlmeddelse");

        jLabel25.setText("Starttid:");

        jTextFieldBestillingStartTid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingStartTidFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingStartTidFocusLost(evt);
            }
        });

        jLabel26.setText("Sluttid:");

        jTextFieldBestillingSlutTid.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingSlutTidFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingSlutTidFocusLost(evt);
            }
        });
        jTextFieldBestillingSlutTid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingSlutTidActionPerformed(evt);
            }
        });

        jLabel27.setText("Tolke:");

        jLabelBestillingModtagere.setText("evt. flere:");

        jTextFieldBestillingModtagere.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingModtagereFocusGained(evt);
            }
        });

        jButtonBestillingHentAndenBestilling.setText("Kopiér denne til ny bestilling");
        jButtonBestillingHentAndenBestilling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingHentAndenBestillingActionPerformed(evt);
            }
        });

        jPanelBestillingList.setPreferredSize(new java.awt.Dimension(669, 495));

        jListBestilling.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListBestilling.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListBestillingValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jListBestilling);

        jLabelSearch.setText("Søg:");

        jTextFieldBestillingSøg.setText("jTextField1");
        jTextFieldBestillingSøg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingSøgActionPerformed(evt);
            }
        });
        jTextFieldBestillingSøg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldBestillingSøgKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBestillingSøgKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldBestillingSøgKeyTyped(evt);
            }
        });

        jButtonBestillingTilføj.setText("Tilføj");
        jButtonBestillingTilføj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBestillingTilføjActionPerformed(evt);
            }
        });

        jListBestilling2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jListBestilling2);

        javax.swing.GroupLayout jPanelBestillingEkstraLayout = new javax.swing.GroupLayout(jPanelBestillingEkstra);
        jPanelBestillingEkstra.setLayout(jPanelBestillingEkstraLayout);
        jPanelBestillingEkstraLayout.setHorizontalGroup(
            jPanelBestillingEkstraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingEkstraLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelBestillingEkstraLayout.setVerticalGroup(
            jPanelBestillingEkstraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingEkstraLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jLabelList.setText("Tilføj bestiller");

        javax.swing.GroupLayout jPanelBestillingListLayout = new javax.swing.GroupLayout(jPanelBestillingList);
        jPanelBestillingList.setLayout(jPanelBestillingListLayout);
        jPanelBestillingListLayout.setHorizontalGroup(
            jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                        .addGroup(jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jButtonBestillingTilføj)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelBestillingEkstra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                        .addGroup(jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                                .addComponent(jLabelSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldBestillingSøg, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelList))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelBestillingListLayout.setVerticalGroup(
            jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabelList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearch)
                    .addComponent(jTextFieldBestillingSøg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelBestillingListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBestillingListLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBestillingTilføj))
                    .addComponent(jPanelBestillingEkstra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jSpinnerAntalTolke.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerAntalTolkeStateChanged(evt);
            }
        });
        jSpinnerAntalTolke.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jSpinnerAntalTolkeFocusGained(evt);
            }
        });

        jTextFieldBestillingTolke.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingTolkeFocusGained(evt);
            }
        });
        jTextFieldBestillingTolke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBestillingTolkeActionPerformed(evt);
            }
        });

        jLabel29.setText("Lokal");

        jTextFieldBestillingLokal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingLokalFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingLokalFocusLost(evt);
            }
        });

        jLabel30.setText("Ekstra info:");

        jTextFieldBestillingExtra.setText("jTextField1");
        jTextFieldBestillingExtra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingExtraFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldBestillingExtraFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanelBestillingDetailLayout = new javax.swing.GroupLayout(jPanelBestillingDetail);
        jPanelBestillingDetail.setLayout(jPanelBestillingDetailLayout);
        jPanelBestillingDetailLayout.setHorizontalGroup(
            jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelBestillingModtagere)
                            .addComponent(jLabelBestillingBevilling)
                            .addComponent(jLabelBestillingType)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))
                        .addGap(10, 10, 10)
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldBestillingModtagere)
                            .addComponent(jTextFieldBestillingBevilling)
                            .addComponent(jTextFieldBestillingType)
                            .addComponent(jTextFieldBestillingAdresse)
                            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                                .addComponent(jTextFieldBestillingPostNR, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldBestillingLokal, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                            .addComponent(jTextFieldBestillingDato)))
                    .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelBestillingBestiller)
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldBestillingID, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldBestillingBestiller, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonBestillingHentAndenBestilling)
                    .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel25))
                        .addGap(19, 19, 19)
                        .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                                .addComponent(jTextFieldBestillingStartTid, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldBestillingSlutTid, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                                .addComponent(jSpinnerAntalTolke, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldBestillingTolke, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel23)
                    .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                        .addComponent(jButtonBestillingGem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBestillingLuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelBestillingFejlMed, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBestillingExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelBestillingList, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
        );
        jPanelBestillingDetailLayout.setVerticalGroup(
            jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldBestillingID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBestillingBestiller)
                    .addComponent(jTextFieldBestillingBestiller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBestillingModtagere)
                    .addComponent(jTextFieldBestillingModtagere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBestillingBevilling, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldBestillingBevilling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBestillingType)
                    .addComponent(jTextFieldBestillingType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldBestillingAdresse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTextFieldBestillingPostNR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jTextFieldBestillingLokal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextFieldBestillingDato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextFieldBestillingStartTid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jTextFieldBestillingSlutTid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jSpinnerAntalTolke, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldBestillingTolke, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jTextFieldBestillingExtra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBestillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBestillingGem)
                    .addComponent(jButtonBestillingLuk)
                    .addComponent(jLabelBestillingFejlMed))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonBestillingHentAndenBestilling)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelBestillingDetailLayout.createSequentialGroup()
                .addComponent(jPanelBestillingList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bestilling detajler", jPanelBestillingDetail);

        jLabel5.setText("Navn*:");

        jLabel6.setText("Efternavn*:");

        jLabel7.setText("Telefon:");

        jLabel8.setText("Email:");

        jLabel9.setText("Adresse:");

        jLabel10.setText("Post nr:");

        jTextFieldBrugerDetailEfternavn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBrugerDetailEfternavnActionPerformed(evt);
            }
        });

        jTextFieldBrugerDetailEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBrugerDetailEmailActionPerformed(evt);
            }
        });

        jButtonBrugerDetailGem.setText("Gem");
        jButtonBrugerDetailGem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerDetailGemActionPerformed(evt);
            }
        });

        jButtonBrugerDetailFortryd.setText("Luk");
        jButtonBrugerDetailFortryd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerDetailFortrydActionPerformed(evt);
            }
        });

        jLabel4.setText("ID:");

        jTextFieldBrugerDetailID.setEditable(false);
        jTextFieldBrugerDetailID.setText("Ny");

        jLabelBrugerDetailFejlMeddelelse.setText("Fejlmeddelse");

        jLabel18.setText("Bevillinger:");

        jListBrugerBevillinger.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPaneBrugerBevilling.setViewportView(jListBrugerBevillinger);

        jButtonBrugerBevillingOpret.setText("Opret nyt");
        jButtonBrugerBevillingOpret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerBevillingOpretActionPerformed(evt);
            }
        });

        jButtonBrugerBevillingVis.setText("Vis");
        jButtonBrugerBevillingVis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrugerBevillingVisActionPerformed(evt);
            }
        });

        jButtonBrugerBevillingSlet.setText("Slet");

        javax.swing.GroupLayout jPanelBrugerBevillingLayout = new javax.swing.GroupLayout(jPanelBrugerBevilling);
        jPanelBrugerBevilling.setLayout(jPanelBrugerBevillingLayout);
        jPanelBrugerBevillingLayout.setHorizontalGroup(
            jPanelBrugerBevillingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugerBevillingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBrugerBevillingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBrugerBevillingLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButtonBrugerBevillingOpret)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBrugerBevillingVis)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBrugerBevillingSlet)
                        .addContainerGap(27, Short.MAX_VALUE))
                    .addGroup(jPanelBrugerBevillingLayout.createSequentialGroup()
                        .addGroup(jPanelBrugerBevillingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneBrugerBevilling)
                            .addGroup(jPanelBrugerBevillingLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanelBrugerBevillingLayout.setVerticalGroup(
            jPanelBrugerBevillingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugerBevillingLayout.createSequentialGroup()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneBrugerBevilling, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanelBrugerBevillingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBrugerBevillingOpret)
                    .addComponent(jButtonBrugerBevillingVis)
                    .addComponent(jButtonBrugerBevillingSlet)))
        );

        javax.swing.GroupLayout jPanelBrugerDetailLayout = new javax.swing.GroupLayout(jPanelBrugerDetail);
        jPanelBrugerDetail.setLayout(jPanelBrugerDetailLayout);
        jPanelBrugerDetailLayout.setHorizontalGroup(
            jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                            .addComponent(jButtonBrugerDetailGem)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButtonBrugerDetailFortryd)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabelBrugerDetailFejlMeddelelse, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelBrugerDetailLayout.createSequentialGroup()
                            .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBrugerDetailLayout.createSequentialGroup()
                                    .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel8)
                                        .addComponent(jLabel10)
                                        .addComponent(jLabel5))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addGap(47, 47, 47)))
                            .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextFieldBrugerDetailID, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldBrugerDetailAdresse, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextFieldBrugerDetailEfternavn)
                                        .addComponent(jTextFieldBrugerDetailTelefon)
                                        .addComponent(jTextFieldBrugerDetailEmail)
                                        .addComponent(jTextFieldBrugerDetailPostNR, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(jTextFieldBrugerDetailNavn))))))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelBrugerBevilling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(159, Short.MAX_VALUE))
        );
        jPanelBrugerDetailLayout.setVerticalGroup(
            jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextFieldBrugerDetailID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextFieldBrugerDetailNavn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldBrugerDetailEfternavn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextFieldBrugerDetailTelefon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextFieldBrugerDetailEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldBrugerDetailAdresse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTextFieldBrugerDetailPostNR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelBrugerDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonBrugerDetailGem)
                            .addComponent(jButtonBrugerDetailFortryd)
                            .addComponent(jLabelBrugerDetailFejlMeddelelse)))
                    .addGroup(jPanelBrugerDetailLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelBrugerBevilling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(241, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bruger detajler", jPanelBrugerDetail);

        jLabel19.setText("Modtager");

        jLabel20.setText("Giver:");

        jTextFieldBevillingModtager.setEditable(false);
        jTextFieldBevillingModtager.setEnabled(false);

        jLabel28.setText("Opgave:");

        jLabel31.setText("Antal timer:");

        jLabel32.setText("Brugt timer:");

        jTextFieldBevillingGiver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBevillingGiverActionPerformed(evt);
            }
        });

        jTextFieldBevillingAntalTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBevillingAntalTimerActionPerformed(evt);
            }
        });

        jTextFieldBevillingBrugtTimer.setEditable(false);
        jTextFieldBevillingBrugtTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBevillingBrugtTimerActionPerformed(evt);
            }
        });

        jButtonBevillingGem.setText("Gem");
        jButtonBevillingGem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBevillingGemActionPerformed(evt);
            }
        });

        jButtonBevillingAnnuler.setText("Luk");
        jButtonBevillingAnnuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBevillingAnnulerActionPerformed(evt);
            }
        });

        jLabel34.setText("ID:");

        jTextFieldBevillingID.setEditable(false);
        jTextFieldBevillingID.setText("Ny");

        jLabelBevillingDetailFejlMeddelelse1.setText("Fejlmeddelse");

        jLabel33.setText("PDF:");

        jButtonBevillingUploadPDF.setText("Upload");
        jButtonBevillingUploadPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBevillingUploadPDFActionPerformed(evt);
            }
        });

        jButtonBevillingHentPDF.setText("Hent");
        jButtonBevillingHentPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBevillingHentPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelBevillingDetailLayout = new javax.swing.GroupLayout(jPanelBevillingDetail);
        jPanelBevillingDetail.setLayout(jPanelBevillingDetailLayout);
        jPanelBevillingDetailLayout.setHorizontalGroup(
            jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBevillingDetailLayout.createSequentialGroup()
                        .addComponent(jButtonBevillingGem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBevillingAnnuler)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelBevillingDetailFejlMeddelelse1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                        .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelBevillingDetailLayout.createSequentialGroup()
                                    .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel28)
                                        .addComponent(jLabel31)
                                        .addComponent(jLabel19))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                                    .addComponent(jLabel34)
                                    .addGap(47, 47, 47)))
                            .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(43, 43, 43)))
                        .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                                .addComponent(jButtonBevillingUploadPDF)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonBevillingHentPDF))
                            .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldBevillingID, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldBevillingGiver)
                                .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                                    .addComponent(jTextFieldBevillingAntalTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel32)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextFieldBevillingBrugtTimer, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                                .addComponent(jTextFieldBevillingOpgave)
                                .addComponent(jTextFieldBevillingModtager)))
                        .addGap(30, 30, 30)))
                .addContainerGap(391, Short.MAX_VALUE))
        );
        jPanelBevillingDetailLayout.setVerticalGroup(
            jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBevillingDetailLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextFieldBevillingID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTextFieldBevillingModtager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldBevillingGiver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextFieldBevillingOpgave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jTextFieldBevillingAntalTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jTextFieldBevillingBrugtTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jButtonBevillingUploadPDF)
                    .addComponent(jButtonBevillingHentPDF))
                .addGap(43, 43, 43)
                .addGroup(jPanelBevillingDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBevillingGem)
                    .addComponent(jButtonBevillingAnnuler)
                    .addComponent(jLabelBevillingDetailFejlMeddelelse1))
                .addContainerGap(237, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bevilling detajler", jPanelBevillingDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxBestillingerBevillingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxBestillingerBevillingStateChanged
        //setUpTableBestilling();
    }//GEN-LAST:event_jCheckBoxBestillingerBevillingStateChanged

    private void jCheckBoxBestillingerTolkeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxBestillingerTolkeStateChanged
        //setUpTableBestilling();
    }//GEN-LAST:event_jCheckBoxBestillingerTolkeStateChanged

    private void jCheckBoxKommendeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxKommendeStateChanged

    }//GEN-LAST:event_jCheckBoxKommendeStateChanged

    private void jTextFieldBrugerDetailEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBrugerDetailEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBrugerDetailEmailActionPerformed

    private void jTextFieldBrugerDetailEfternavnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBrugerDetailEfternavnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBrugerDetailEfternavnActionPerformed

    private void jTextFieldTolkDetailEfternavnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTolkDetailEfternavnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTolkDetailEfternavnActionPerformed

    private void jTextFieldTolkDetailEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTolkDetailEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTolkDetailEmailActionPerformed

    private void jButtonBrugerDetailGemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerDetailGemActionPerformed
        Bruger b;

        if (jTextFieldBrugerDetailID.getText().equals("Ny")) {
            b = new Bruger();
        } else {
            b = c.getBrugerById(Integer.parseInt(jTextFieldBrugerDetailID.getText()));
        }
        b.setNavn(jTextFieldBrugerDetailNavn.getText());
        b.setEfternavn(jTextFieldBrugerDetailEfternavn.getText());
        b.setTelefon(jTextFieldBrugerDetailTelefon.getText());
        b.setEmail(jTextFieldBrugerDetailEmail.getText());
        b.setAdresse(jTextFieldBrugerDetailAdresse.getText());
        b.setPostnr(jTextFieldBrugerDetailPostNR.getText());

        if (b.getNavn().equals("")) {
            jLabelBrugerDetailFejlMeddelelse.setText("Mangler navn!");
        } else if (b.getEfternavn().equals("")) {
            jLabelBrugerDetailFejlMeddelelse.setText("Mangler efternavn!");
        } else {
            c.SetBruger(b);
            jLabelBrugerDetailFejlMeddelelse.setText("Gemt!");
            redigerBruger(b.getBrugerID());
        }

    }//GEN-LAST:event_jButtonBrugerDetailGemActionPerformed

    private void jButtonBrugerOpretNytActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerOpretNytActionPerformed
        redigerBruger(-1);

    }//GEN-LAST:event_jButtonBrugerOpretNytActionPerformed

    private void jButtonBrugerDetailFortrydActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerDetailFortrydActionPerformed
        disableBrugerDetail();
    }//GEN-LAST:event_jButtonBrugerDetailFortrydActionPerformed

    private void jButtonBrugerRedigerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerRedigerActionPerformed
        try {
            redigerBruger(Integer.parseInt(jTableBruger.getModel().getValueAt(jTableBruger.getSelectedRow(), 0).toString()));
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButtonBrugerRedigerActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelBrugerDetail)) {
            jLabelBrugerDetailFejlMeddelelse.setText("");
            setUpListBevilling(jListBrugerBevillinger);
        } else if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelTolkDetail)) {
            jLabelTolkDetailFejlMeddelelse.setText("");
        } else if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelBestillinger)) {
            setUpTableBestilling();
        } else if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelBrugere)) {
            setUpBrugere();
        } else if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelTolke)) {
            setUpTolke();
        } else if (jTabbedPane1.getComponentAt(jTabbedPane1.getModel().getSelectedIndex()).equals(jPanelBestillingDetail)) {
            disableBestillingSearch();
            jLabelBestillingFejlMed.setText("");

        }


        /*
         switch(jTabbedPane1.getModel().getSelectedIndex()){
         case 0: break;
         case 1: setUpTableBestilling(); break;     
         case 2: setUpBrugere(); break;
         case 3: setUpTolke(); break;
         case 4: if(jTabbedPane1.getComponentAt(4).equals(jPanelBrugerDetail)) jLabelBrugerDetailFejlMeddelelse.setText("");
         }*/

    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jTextFieldTolkDetailNavnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTolkDetailNavnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTolkDetailNavnActionPerformed

    private void jButtonTolkOpretNytActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTolkOpretNytActionPerformed
        enableTolkDetail();
        jTextFieldTolkDetailID.setText("Ny");
        jTextFieldTolkDetailNavn.setText("");
        jTextFieldTolkDetailEfternavn.setText("");
        jTextFieldTolkDetailTelefon.setText("");
        jTextFieldTolkDetailEmail.setText("");
        jTextFieldTolkDetailAdresse.setText("");
        jTextFieldTolkDetailPostNR.setText("");
    }//GEN-LAST:event_jButtonTolkOpretNytActionPerformed

    private void jButtonTolkRedigerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTolkRedigerActionPerformed
        redigerTolk();
    }//GEN-LAST:event_jButtonTolkRedigerActionPerformed

    private void jButtonTolkDetailGemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTolkDetailGemActionPerformed
        Tolk b;
        if (jTextFieldTolkDetailID.getText().equals("Ny")) {
            b = new Tolk();
        } else {
            b = c.getTolkById(Integer.parseInt(jTextFieldTolkDetailID.getText()));
        }
        b.setNavn(jTextFieldTolkDetailNavn.getText());
        b.setEfternavn(jTextFieldTolkDetailEfternavn.getText());
        b.setTelefon(jTextFieldTolkDetailTelefon.getText());
        b.setEmail(jTextFieldTolkDetailEmail.getText());
        b.setAdresse(jTextFieldTolkDetailAdresse.getText());
        b.setPostnr(jTextFieldTolkDetailPostNR.getText());

        if (b.getNavn().equals("")) {
            jLabelTolkDetailFejlMeddelelse.setText("Mangler navn!");
        } else if (b.getEfternavn().equals("")) {
            jLabelTolkDetailFejlMeddelelse.setText("Mangler efternavn!");
        } else {
            c.SetTolk(b);
            jLabelTolkDetailFejlMeddelelse.setText("Gemt!");
            jTextFieldTolkDetailID.setText(b.getTolkID().toString());
        }
    }//GEN-LAST:event_jButtonTolkDetailGemActionPerformed

    private void jButtonTolkDetailFortrydActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTolkDetailFortrydActionPerformed
        disableTolkDetail();
    }//GEN-LAST:event_jButtonTolkDetailFortrydActionPerformed

    private void jTextFieldBestillingBevillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingBevillingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingBevillingActionPerformed

    private void jTextFieldBestillingAdresseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingAdresseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingAdresseActionPerformed

    private void jButtonBestillingGemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingGemActionPerformed
        if (opg.getBestiller() != null && opg.getDato() != null) {
            opg = c.setOpgave(opg);
            jLabelBestillingFejlMed.setText("Gemt");
        } else {
            jLabelBestillingFejlMed.setText("Vælg venligst en bestiller");
        }
        updateBestillingDetajler();
    }//GEN-LAST:event_jButtonBestillingGemActionPerformed

    private void jButtonBestillingLukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingLukActionPerformed
        disableBestilling();
    }//GEN-LAST:event_jButtonBestillingLukActionPerformed

    private void jTextFieldBestillingSlutTidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSlutTidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingSlutTidActionPerformed

    private void jTextFieldBestillingDatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingDatoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingDatoActionPerformed

    private void jTextFieldBestillingBestillerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingBestillerActionPerformed

    }//GEN-LAST:event_jTextFieldBestillingBestillerActionPerformed

    private void jTextFieldBestillingBestillerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingBestillerFocusGained
        if (jTextFieldBestillingBestiller.getText().equals("")) {
            jTextFieldBestillingBestiller.setText("<Vælg fra listen>");
        }
        jPanelBestillingList.setVisible(true);
        searchFor = "bruger";
        jTextFieldBestillingSøg.setText("");
        setUpListBrugere();
        jButtonBestillingTilføj.setEnabled(true);
        if (opg.getBestiller() != null) {
            jButtonBestillingTilføj.setText("Ændr");
        } else {
            jButtonBestillingTilføj.setText("Tilføj");
        }

        jTextFieldBestillingSøg.requestFocus();
        //jTextFieldBestillingHentFraSøg.setVisible(true);
        //jListHentFra.setVisible(true);
    }//GEN-LAST:event_jTextFieldBestillingBestillerFocusGained

    private void jTextFieldBestillingSøgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSøgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingSøgActionPerformed

    private void jTextFieldBestillingSøgKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSøgKeyTyped

    }//GEN-LAST:event_jTextFieldBestillingSøgKeyTyped

    private void jTextFieldBestillingSøgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSøgKeyPressed

    }//GEN-LAST:event_jTextFieldBestillingSøgKeyPressed

    private void jTextFieldBestillingSøgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSøgKeyReleased
        if (searchFor.equals("bruger")) {
            setUpListBrugere();
        }
    }//GEN-LAST:event_jTextFieldBestillingSøgKeyReleased

    private void jButtonBestillingTilføjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingTilføjActionPerformed
        jPanelBestillingList.setVisible(false);
        if (searchFor.equals("bruger")) {
            if (jListBestilling.getSelectedIndex() != -1) {
                opg.setBestiller(brugerList.get(jListBestilling.getSelectedIndex()));
            }
            updateBestillingDetajler();
            //jTextFieldBestillingModtagere.requestFocus();

        } else if (searchFor.equals("modtagere")) {
            if (jListBestilling.getSelectedIndex() != -1) {
                opg.getBrugerCollection().add(brugerList.get(jListBestilling.getSelectedIndex()));
            }
            updateBestillingDetajler();

        } else if (searchFor.equals("bevilling")) {
            if (jListBestilling.getSelectedIndex() != -1) {
                opg.setBevillingsnummer(bevillingList.get(jListBestilling.getSelectedIndex()));
            }
            updateBestillingDetajler();
        } else if (searchFor.equals("tolke")) {
            if (jListBestilling.getSelectedIndex() != -1) {
                Tolk t = tolkList.get(jListBestilling.getSelectedIndex());
                t.getOpgaveCollection().add(opg);
                c.SetTolk(t);          
            }
            opg = c.refreshOpgave(opg);
            updateBestillingDetajler();
            //jTextFieldBestillingModtagere.requestFocus();

        }
    }//GEN-LAST:event_jButtonBestillingTilføjActionPerformed

    private void jButtonBestillingerOpretNytActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingerOpretNytActionPerformed
        enableBestilling();
        opg = new Opgave();
        opg.setAntaltolk(1);
        opg.setBrugerCollection(new ArrayList<Bruger>());
        opg.setTolkCollection(new ArrayList<>());
        updateBestillingDetajler();
    }//GEN-LAST:event_jButtonBestillingerOpretNytActionPerformed

    private void jTextFieldBestillingModtagereFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingModtagereFocusGained
        resetBestillingList();
        jPanelBestillingList.setVisible(true);
        searchFor = "modtagere";
        jTextFieldBestillingSøg.setText("");
        setUpListBrugere();
        jButtonBestillingTilføj.setText("Tilføj");

        jTextFieldBestillingSøg.requestFocus();
    }//GEN-LAST:event_jTextFieldBestillingModtagereFocusGained

    private void jTextFieldBestillingBevillingFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingBevillingFocusGained
        resetBestillingList();
        jPanelBestillingList.setVisible(true);
        searchFor = "bevilling";
        setUpListBevilling(jListBestilling);
        jTextFieldBestillingSøg.setText("");
        jButtonBestillingTilføj.setText("Tilføj");
        jButtonBestillingTilføj.setEnabled(true);

    }//GEN-LAST:event_jTextFieldBestillingBevillingFocusGained

    private void jTextFieldBestillingTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingTypeFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingTypeFocusGained

    private void jTextFieldBestillingAdresseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingAdresseFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingAdresseFocusGained

    private void jTextFieldBestillingPostNRFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingPostNRFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingPostNRFocusGained

    private void jTextFieldBestillingLokalFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingLokalFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingLokalFocusGained

    private void jTextFieldBestillingDatoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingDatoFocusGained
        resetBestillingList();
    }//GEN-LAST:event_jTextFieldBestillingDatoFocusGained

    private void jTextFieldBestillingStartTidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingStartTidFocusGained
        resetBestillingList();
    }//GEN-LAST:event_jTextFieldBestillingStartTidFocusGained

    private void jTextFieldBestillingSlutTidFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSlutTidFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingSlutTidFocusGained

    private void jTextFieldBestillingExtraFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingExtraFocusGained
        resetBestillingList();    }//GEN-LAST:event_jTextFieldBestillingExtraFocusGained

    private void jTextFieldBestillingTolkeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingTolkeFocusGained
        resetBestillingList();
        enableTolkeList();
        jTextFieldBestillingSøg.requestFocus();

    }//GEN-LAST:event_jTextFieldBestillingTolkeFocusGained

    private void jSpinnerAntalTolkeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSpinnerAntalTolkeFocusGained
        resetBestillingList();
        jTextFieldBestillingTolkeFocusGained(evt);
    }//GEN-LAST:event_jSpinnerAntalTolkeFocusGained

    private void jSpinnerAntalTolkeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerAntalTolkeStateChanged
        int behov = (int) jSpinnerAntalTolke.getModel().getValue();
        if (behov < 0) {
            behov = 0;
            jSpinnerAntalTolke.getModel().setValue(0);
        }
        opg.setAntaltolk(behov);
        if (checkTolkNumber()) {
            jButtonBestillingTilføj.setEnabled(false);
        } else {
            jButtonBestillingTilføj.setEnabled(true);
        }
        enableTolkeList();
    }//GEN-LAST:event_jSpinnerAntalTolkeStateChanged

    private void jListBestillingValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListBestillingValueChanged
        if (searchFor == "tolke") {
            setUpListTolkeOpgaver();
        }
    }//GEN-LAST:event_jListBestillingValueChanged

    private void jTextFieldBestillingTypeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingTypeFocusLost
        opg.setType(jTextFieldBestillingType.getText());
    }//GEN-LAST:event_jTextFieldBestillingTypeFocusLost

    private void jTextFieldBestillingAdresseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingAdresseFocusLost
        opg.setAdresse(jTextFieldBestillingAdresse.getText());
    }//GEN-LAST:event_jTextFieldBestillingAdresseFocusLost

    private void jTextFieldBestillingPostNRFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingPostNRFocusLost
        try {
            opg.setPostnr(Integer.parseInt(jTextFieldBestillingPostNR.getText()));
        } catch (Exception e) {
        }

    }//GEN-LAST:event_jTextFieldBestillingPostNRFocusLost

    private void jTextFieldBestillingLokalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingLokalFocusLost
        opg.setLokal(jTextFieldBestillingLokal.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingLokalFocusLost

    private void jTextFieldBestillingDatoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingDatoFocusLost
        opg.setDato(convertDate(jTextFieldBestillingDato.getText()));
    }//GEN-LAST:event_jTextFieldBestillingDatoFocusLost

    private void jTextFieldBestillingStartTidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingStartTidFocusLost
        try {
            opg.setStartTid(Integer.parseInt(jTextFieldBestillingStartTid.getText()));
        } catch (Exception e) {
        }

    }//GEN-LAST:event_jTextFieldBestillingStartTidFocusLost

    private void jTextFieldBestillingSlutTidFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingSlutTidFocusLost
        try {
            opg.setSlutTid(Integer.parseInt(jTextFieldBestillingSlutTid.getText()));
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTextFieldBestillingSlutTidFocusLost

    private void jTextFieldBestillingTolkeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBestillingTolkeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBestillingTolkeActionPerformed

    private void jTextFieldBestillingExtraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldBestillingExtraFocusLost
        opg.setEkstra(jTextFieldBestillingExtra.getText());
    }//GEN-LAST:event_jTextFieldBestillingExtraFocusLost

    private void jButtonBestillingerVisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingerVisActionPerformed
        visBestilling();
    }//GEN-LAST:event_jButtonBestillingerVisActionPerformed

    private void jCheckBoxBestillingerTolkeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBestillingerTolkeActionPerformed
        setUpTableBestilling();        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxBestillingerTolkeActionPerformed

    private void jCheckBoxBestillingerBevillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBestillingerBevillingActionPerformed
        setUpTableBestilling();
    }//GEN-LAST:event_jCheckBoxBestillingerBevillingActionPerformed

    private void jCheckBoxKommendeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxKommendeActionPerformed
        setUpTableBestilling();        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxKommendeActionPerformed

    private void jTextFieldBevillingGiverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBevillingGiverActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBevillingGiverActionPerformed

    private void jTextFieldBevillingAntalTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBevillingAntalTimerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBevillingAntalTimerActionPerformed

    private void jButtonBevillingGemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBevillingGemActionPerformed
        Bevilling b = currBevilling;
        b.setNavn(jTextFieldBevillingGiver.getText());
        b.setOpgave(jTextFieldBevillingOpgave.getText());
        b.setTimer(Integer.parseInt(jTextFieldBevillingAntalTimer.getText()));

        b.getBruger().getBevillingCollection().add(b);
        c.SetBevilling(b);
        jTextFieldBevillingID.setText(b.getBevillingsnummer() + "");
        jLabelBevillingDetailFejlMeddelelse1.setText("Gemt");
    }//GEN-LAST:event_jButtonBevillingGemActionPerformed

    private void jButtonBevillingAnnulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBevillingAnnulerActionPerformed
        disableBevillingDetail();
    }//GEN-LAST:event_jButtonBevillingAnnulerActionPerformed

    private void jButtonBrugerBevillingOpretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerBevillingOpretActionPerformed
        Bevilling b = new Bevilling();
        b.setBruger(c.getBrugerById(Integer.parseInt(jTextFieldBrugerDetailID.getText())));
        redigerBevilling(b);
    }//GEN-LAST:event_jButtonBrugerBevillingOpretActionPerformed

    private void jButtonBrugerBevillingVisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrugerBevillingVisActionPerformed
        if (jListBrugerBevillinger.getSelectedIndex() >= 0) {
            redigerBevilling(bevillingList.get(jListBrugerBevillinger.getSelectedIndex()));
        }
    }//GEN-LAST:event_jButtonBrugerBevillingVisActionPerformed

    private void jTextFieldBevillingBrugtTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBevillingBrugtTimerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBevillingBrugtTimerActionPerformed

    private void jButtonBevillingUploadPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBevillingUploadPDFActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File pdfFile = chooser.getSelectedFile();

        if (pdfFile.getPath().endsWith("pdf")) {
            //File pdfFile = new File("C:/Blank Flowchart.pdf");
            byte[] pdfData = new byte[(int) pdfFile.length()];
            DataInputStream dis;
            try {
                dis = new DataInputStream(new FileInputStream(pdfFile));
                dis.readFully(pdfData);  // read from file into byte[] array
                dis.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            currBevilling.setPdf(pdfData);
            jButtonBevillingGemActionPerformed(evt);
        }
    }//GEN-LAST:event_jButtonBevillingUploadPDFActionPerformed

    private void jButtonBevillingHentPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBevillingHentPDFActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Vælg mappe");
        File f = new File("PDF");
        if (!f.exists()) {
            f.mkdir();
        }

        chooser.setCurrentDirectory(f);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.showOpenDialog(null);
        try {
            String filename = "Bevilling" + currBevilling.getBevillingsnummer() + currBevilling.getBruger().getNavn() + currBevilling.getBruger().getEfternavn();
            OutputStream targetFile = new FileOutputStream(
                    chooser.getSelectedFile() + "//" + filename + ".pdf");
            targetFile.write(currBevilling.getPdf());
            targetFile.close();
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jButtonBevillingHentPDFActionPerformed

    private void jButtonBestillingHentAndenBestillingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBestillingHentAndenBestillingActionPerformed
        Opgave oldopg = opg;
        opg = new Opgave();
        opg.setBestiller(oldopg.getBestiller());
        opg.setBrugerCollection(oldopg.getBrugerCollection());
        opg.setBevillingsnummer(oldopg.getBevillingsnummer());
        opg.setType(oldopg.getType());
        opg.setAdresse(oldopg.getAdresse());
        opg.setPostnr(oldopg.getPostnr());
        opg.setLokal(oldopg.getLokal());
        opg.setAntaltolk(oldopg.getAntaltolk());
        opg.setEkstra(oldopg.getEkstra());
        opg.setTolkCollection(new ArrayList<Tolk>());
        updateBestillingDetajler();
        jLabelBestillingFejlMed.setText("Kopieret til nyt!");
    }//GEN-LAST:event_jButtonBestillingHentAndenBestillingActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBestillingGem;
    private javax.swing.JButton jButtonBestillingHentAndenBestilling;
    private javax.swing.JButton jButtonBestillingLuk;
    private javax.swing.JButton jButtonBestillingTilføj;
    private javax.swing.JButton jButtonBestillingerOpretNyt;
    private javax.swing.JButton jButtonBestillingerVis;
    private javax.swing.JButton jButtonBevillingAnnuler;
    private javax.swing.JButton jButtonBevillingGem;
    private javax.swing.JButton jButtonBevillingHentPDF;
    private javax.swing.JButton jButtonBevillingUploadPDF;
    private javax.swing.JButton jButtonBrugerBevillingOpret;
    private javax.swing.JButton jButtonBrugerBevillingSlet;
    private javax.swing.JButton jButtonBrugerBevillingVis;
    private javax.swing.JButton jButtonBrugerDetailFortryd;
    private javax.swing.JButton jButtonBrugerDetailGem;
    private javax.swing.JButton jButtonBrugerOpretNyt;
    private javax.swing.JButton jButtonBrugerRediger;
    private javax.swing.JButton jButtonTolkDetailFortryd;
    private javax.swing.JButton jButtonTolkDetailGem;
    private javax.swing.JButton jButtonTolkOpretNyt;
    private javax.swing.JButton jButtonTolkRediger;
    private javax.swing.JCheckBox jCheckBoxBestillingerBevilling;
    private javax.swing.JCheckBox jCheckBoxBestillingerTolke;
    private javax.swing.JCheckBox jCheckBoxKommende;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBestillingBestiller;
    private javax.swing.JLabel jLabelBestillingBevilling;
    private javax.swing.JLabel jLabelBestillingFejlMed;
    private javax.swing.JLabel jLabelBestillingModtagere;
    private javax.swing.JLabel jLabelBestillingType;
    private javax.swing.JLabel jLabelBestillingerSearch;
    private javax.swing.JLabel jLabelBevillingDetailFejlMeddelelse1;
    private javax.swing.JLabel jLabelBrugerDetailFejlMeddelelse;
    private javax.swing.JLabel jLabelBrugerSearch;
    private javax.swing.JLabel jLabelList;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JLabel jLabelTolkDetailFejlMeddelelse;
    private javax.swing.JLabel jLabelTolkSearch;
    private javax.swing.JList jListBestilling;
    private javax.swing.JList jListBestilling2;
    private javax.swing.JList jListBrugerBevillinger;
    private javax.swing.JPanel jPanelBestillingDetail;
    private javax.swing.JPanel jPanelBestillingEkstra;
    private javax.swing.JPanel jPanelBestillingList;
    private javax.swing.JPanel jPanelBestillinger;
    private javax.swing.JPanel jPanelBevillingDetail;
    private javax.swing.JPanel jPanelBrugerBevilling;
    private javax.swing.JPanel jPanelBrugerDetail;
    private javax.swing.JPanel jPanelBrugere;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelTolkDetail;
    private javax.swing.JPanel jPanelTolke;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneBestillinger;
    private javax.swing.JScrollPane jScrollPaneBruger;
    private javax.swing.JScrollPane jScrollPaneBrugerBevilling;
    private javax.swing.JScrollPane jScrollPaneTolk;
    private javax.swing.JSpinner jSpinnerAntalTolke;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableBestillinger;
    private javax.swing.JTable jTableBruger;
    private javax.swing.JTable jTableTolk;
    private javax.swing.JTextField jTextFieldBestillingAdresse;
    private javax.swing.JTextField jTextFieldBestillingBestiller;
    private javax.swing.JTextField jTextFieldBestillingBevilling;
    private javax.swing.JTextField jTextFieldBestillingDato;
    private javax.swing.JTextField jTextFieldBestillingExtra;
    private javax.swing.JTextField jTextFieldBestillingID;
    private javax.swing.JTextField jTextFieldBestillingLokal;
    private javax.swing.JTextField jTextFieldBestillingModtagere;
    private javax.swing.JTextField jTextFieldBestillingPostNR;
    private javax.swing.JTextField jTextFieldBestillingSlutTid;
    private javax.swing.JTextField jTextFieldBestillingStartTid;
    private javax.swing.JTextField jTextFieldBestillingSøg;
    private javax.swing.JTextField jTextFieldBestillingTolke;
    private javax.swing.JTextField jTextFieldBestillingType;
    private javax.swing.JTextField jTextFieldBestillingerSearch;
    private javax.swing.JTextField jTextFieldBevillingAntalTimer;
    private javax.swing.JTextField jTextFieldBevillingBrugtTimer;
    private javax.swing.JTextField jTextFieldBevillingGiver;
    private javax.swing.JTextField jTextFieldBevillingID;
    private javax.swing.JTextField jTextFieldBevillingModtager;
    private javax.swing.JTextField jTextFieldBevillingOpgave;
    private javax.swing.JTextField jTextFieldBrugerDetailAdresse;
    private javax.swing.JTextField jTextFieldBrugerDetailEfternavn;
    private javax.swing.JTextField jTextFieldBrugerDetailEmail;
    private javax.swing.JTextField jTextFieldBrugerDetailID;
    private javax.swing.JTextField jTextFieldBrugerDetailNavn;
    private javax.swing.JTextField jTextFieldBrugerDetailPostNR;
    private javax.swing.JTextField jTextFieldBrugerDetailTelefon;
    private javax.swing.JTextField jTextFieldBrugerSearch;
    private javax.swing.JTextField jTextFieldTolkDetailAdresse;
    private javax.swing.JTextField jTextFieldTolkDetailEfternavn;
    private javax.swing.JTextField jTextFieldTolkDetailEmail;
    private javax.swing.JTextField jTextFieldTolkDetailID;
    private javax.swing.JTextField jTextFieldTolkDetailNavn;
    private javax.swing.JTextField jTextFieldTolkDetailPostNR;
    private javax.swing.JTextField jTextFieldTolkDetailTelefon;
    private javax.swing.JTextField jTextFieldTolkSearch;
    // End of variables declaration//GEN-END:variables
}
