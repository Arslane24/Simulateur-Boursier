package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import data.Action;
import data.Entreprise;
import data.Obligation;


public class TestActif {
    private Entreprise entreprise;
    private Action action;
    private Obligation obligation;

    @Before
    public void prepare() {
        entreprise = new Entreprise("TestCorp", "TC01", 100.0, "Technologie", "Logiciels", "EUR", "Euronext", 1000, 0.02, 5.0, 100.0, 500.0);
        action = new Action(10, entreprise, 100.0);
        obligation = new Obligation(0.03, 5, entreprise, 1000.0);
    }

    @Test
    public void testAction() {
        assertEquals(10, action.getQuantite());
        assertEquals(100.0, action.getPrixActu(), 0);
        assertEquals(entreprise, action.getEntreprise());
        assertTrue(action.isAction());
    }

    @Test
    public void testObligationCreationAndGet() {
        assertEquals(0.03, obligation.getTauxInteret(), 0);
        assertEquals(5, obligation.getEcheance());
        assertEquals(1000.0, obligation.getPrixActu(), 0);
        assertEquals(entreprise, obligation.getEntreprise());
        assertFalse(obligation.isAction());
    }

    @Test
    public void testActionSetters() {
        action.setQuantite(20);
        action.setPrixActu(150.0);
        assertEquals(20, action.getQuantite());
        assertEquals(150.0, action.getPrixActu(), 0);
    }

    @Test
    public void testObligationSetters() {
        obligation.setTauxInteret(0.04);
        obligation.setEcheance(7);
        obligation.setPrixActu(1200.0);
        assertEquals(0.04, obligation.getTauxInteret(), 0);
        assertEquals(7, obligation.getEcheance());
        assertEquals(1200.0, obligation.getPrixActu(), 0);
    }
}