package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import data.Actif;
import data.Entreprise;
import data.Portfeuille;
import data.Action;


public class TestPortfeuille {
    private Portfeuille portefeuille;
    private Entreprise entreprise;
    private Action action;

    @Before
    public void prepareportfeuille() {
        entreprise = new Entreprise("TestCorp", "TC01", 100.0, "Technologie", "Logiciels", "EUR", "Euronext", 1000, 0.02, 5.0, 100.0, 500.0);
        action = new Action(10, entreprise, 100.0);
        ArrayList<Actif> actifs = new ArrayList<>();
        actifs.add(action);
        portefeuille = new Portfeuille(actifs, 50000.0, 1000.0, 0.0, new ArrayList<Actif>());
    }

    @Test
    public void testPortfeuilleCreation() {
        assertNotNull(portefeuille.getActifs());
        assertEquals(1, portefeuille.getActifs().size());
        assertEquals(50000.0, portefeuille.getSoldeActuel(), 0);
        assertEquals(1000.0, portefeuille.getSoldeInvesti(), 0);
        assertEquals(0.0, portefeuille.getProfit(), 0);
        assertNotNull(portefeuille.getHistorique());
    }

    @Test
    public void testGettersAndSetters() {
        portefeuille.setSoldeActuel(60000.0);
        portefeuille.setSoldeInvesti(2000.0);
        portefeuille.setProfit(500.0);
        assertEquals(60000.0, portefeuille.getSoldeActuel(), 0);
        assertEquals(2000.0, portefeuille.getSoldeInvesti(), 0);
        assertEquals(500.0, portefeuille.getProfit(), 0);
        ArrayList<Actif> newActifs = new ArrayList<>();
        portefeuille.setActifs(newActifs);
        assertEquals(newActifs, portefeuille.getActifs());
        ArrayList<Actif> newHistorique = new ArrayList<>();
        portefeuille.setHistorique(newHistorique);
        assertEquals(newHistorique, portefeuille.getHistorique());
    }
}