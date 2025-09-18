package test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import data.Entreprise;

/**
 * Unit tests for the Entreprise class.
 */
public class TestEntreprise {
    private Entreprise entreprise;

    @Before
    public void prepareentreprise() {
        entreprise = new Entreprise("TestCorp", "TC01", 100.0, "Technologie", "Logiciels", "EUR", "Euronext", 1000, 0.02, 5.0, 100.0, 500.0);
    }

    @Test
    public void testEntrepriseCreationAndGetters() {
        assertEquals("TestCorp", entreprise.getNom());
        assertEquals("TC01", entreprise.getID());
        assertEquals(100.0, entreprise.getPrixDepart(), 0);
        assertEquals("Technologie", entreprise.getSecteur());
        assertEquals("Logiciels", entreprise.getSousSecteur());
        assertEquals("EUR", entreprise.getDevise());
        assertEquals("Euronext", entreprise.getBourse());
        assertEquals(1000, entreprise.getVolumeMoyen());
        assertEquals(0.02, entreprise.getVolatilite(), 0);
        assertEquals(5.0, entreprise.getPourcentage(), 0);
        assertEquals(100.0, entreprise.getPrixMoyen(), 0);
        assertEquals(500.0, entreprise.getCapital(), 0);
    }

    @Test
    public void testCalculerMomentum() {
        assertEquals(0.05, entreprise.calculerMomentum(), 0);
    }

    @Test
    public void testUpdatePrixMoyen() {
        entreprise.updatePrixMoyen(150.0);
        assertEquals(125.0, entreprise.getPrixMoyen(), 0); 
        entreprise.updatePrixMoyen(200.0);
        assertEquals(150.0, entreprise.getPrixMoyen(), 0); 
    }

    @Test
    public void testSetters() {
        entreprise.setPrixDepart(200.0);
        entreprise.setCapital(1000.0);
        assertEquals(200.0, entreprise.getPrixDepart(), 0);
        assertEquals(1000.0, entreprise.getCapital(), 0);
    }
}