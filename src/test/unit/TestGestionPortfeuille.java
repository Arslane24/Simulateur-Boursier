package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import data.Actif;
import data.Action;
import data.Entreprise;
import data.Obligation;
import data.Portfeuille;
import gestion.GestionPortfeuille;

import java.util.ArrayList;

public class TestGestionPortfeuille {
    private GestionPortfeuille gestion;
    private Entreprise entreprise;
    private Portfeuille portefeuille;

    @Before
    public void preparegestionportfeuille() {
        entreprise = new Entreprise("TestCorp", "TC01", 100.0, "Technologie", "Logiciels", 
                                  "EUR", "Euronext", 1000, 0.02, 5.0, 100.0, 500.0);
        portefeuille = new Portfeuille(new ArrayList<Actif>(), 50000.0, 0.0, 0.0, new ArrayList<Actif>());
        gestion = new GestionPortfeuille(portefeuille);
    }

    @Test
    public void testAcheterAction() {
        Action action = new Action(10, entreprise, entreprise.getPrixDepart());
        gestion.getPortfeuille().getActifs().add(action);
        double montantTotal = action.getQuantite() * action.getPrixActu();
        gestion.getPortfeuille().setSoldeActuel(gestion.getPortfeuille().getSoldeActuel() - montantTotal);
        gestion.getPortfeuille().setSoldeInvesti(gestion.getPortfeuille().getSoldeInvesti() + montantTotal);
        Action actionRecherchee = gestion.recherche("TC01");
        assertNotNull(actionRecherchee);
        assertEquals(10, actionRecherchee.getQuantite());
        assertEquals(50000.0 - (10 * 100.0), portefeuille.getSoldeActuel(), 0);
        assertEquals(10 * 100.0, portefeuille.getSoldeInvesti(), 0);
    }

    @Test
    public void testVendreAction() {
        Action action = new Action(5, entreprise, entreprise.getPrixDepart());
        gestion.getPortfeuille().getActifs().add(action);
        double montantInvesti = action.getQuantite() * action.getPrixActu();
        gestion.getPortfeuille().setSoldeActuel(50000.0 - montantInvesti);
        gestion.getPortfeuille().setSoldeInvesti(montantInvesti);
        gestion.getPortfeuille().getActifs().remove(action);
        double montantRecu = action.getQuantite() * entreprise.getPrixDepart();
        gestion.getPortfeuille().setSoldeActuel(gestion.getPortfeuille().getSoldeActuel() + montantRecu);
        gestion.getPortfeuille().setSoldeInvesti(gestion.getPortfeuille().getSoldeInvesti() - montantInvesti);
        Action actionRecherchee = gestion.recherche("TC01");
        assertNull(actionRecherchee);
        assertEquals(50000.0, portefeuille.getSoldeActuel(), 0);
        assertEquals(0.0, portefeuille.getSoldeInvesti(), 0);
    }

    @Test
    public void testAcheterObligation() {
        Obligation obligation = new Obligation(0.03, 5, entreprise, 1000.0);
        gestion.getPortfeuille().getActifs().add(obligation);
        gestion.getPortfeuille().setSoldeActuel(gestion.getPortfeuille().getSoldeActuel() - obligation.getPrixActu());
        gestion.getPortfeuille().setSoldeInvesti(gestion.getPortfeuille().getSoldeInvesti() + obligation.getPrixActu());
        assertEquals(1, portefeuille.getActifs().size());
        assertEquals(50000.0 - 1000.0, portefeuille.getSoldeActuel(), 0);
        assertEquals(1000.0, portefeuille.getSoldeInvesti(), 0);
        assertFalse(portefeuille.getActifs().get(0).isAction());
    }

    @Test
    public void testCalculerBenefice() {
        Action action = new Action(10, entreprise, 100.0); 
        gestion.getPortfeuille().getActifs().add(action);
        gestion.getPortfeuille().setSoldeActuel(50000.0 - (10 * 100.0));
        gestion.getPortfeuille().setSoldeInvesti(10 * 100.0);
        entreprise.setPrixDepart(120.0); 
        double benefice = gestion.calculerBeneficeValue();
        assertEquals(200.0, benefice, 0);
    }

    @Test
    public void testSoldeSuffisant() {
        assertTrue(gestion.SoldeSuffisant(10000.0));
        assertFalse(gestion.SoldeSuffisant(60000.0));
    }
}