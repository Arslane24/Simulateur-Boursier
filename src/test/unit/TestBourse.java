package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import data.Entreprise;
import gestion.Bourse;
import java.util.HashMap;
import java.util.Map;

public class TestBourse {
    private Bourse bourse;
    private static final double TOLERANCE = 0.01; 

    @Before
    public void prepareBourse() {
        bourse = new Bourse();
    }

    @Test
    public void testChargementEntreprises() {
        Map<String, Entreprise> entreprises = bourse.getEntreprises();
        assertNotNull("La liste des entreprises ne devrait pas être null", entreprises);
        assertFalse("La liste des entreprises ne devrait pas être vide", entreprises.isEmpty());
        assertTrue("Devrait charger entre 1 et 25 entreprises", 
                entreprises.size() >= 1 && entreprises.size() <= 25);
        for (Entreprise entreprise : entreprises.values()) {
            assertNotNull("L'ID de l'entreprise ne devrait pas être null", entreprise.getID());
            assertNotNull("Le nom de l'entreprise ne devrait pas être null", entreprise.getNom());
            assertTrue("Le prix de départ devrait être positif", entreprise.getPrixDepart() > 0);
            assertNotNull("Le secteur ne devrait pas être null", entreprise.getSecteur());
        }
    }

    @Test
    public void testMiseajourModifieLesPrix() {
        Map<String, Double> prixInitiaux = new HashMap<>();
        bourse.getEntreprises().forEach((id, entreprise) -> {
            prixInitiaux.put(id, entreprise.getPrixDepart());
        });
        Map<String, Entreprise> entreprisesApres = bourse.Miseajour();
        assertEquals("Le nombre d'entreprises ne devrait pas changer", prixInitiaux.size(), entreprisesApres.size());                
        boolean auMoinsUnPrixAChange = false;
        boolean toutesVariationsValides = true;     
        for (Entreprise entreprise : entreprisesApres.values()) {
            double ancienPrix = prixInitiaux.get(entreprise.getID());
            double nouveauPrix = entreprise.getPrixDepart();
            double variation = Math.abs(nouveauPrix - ancienPrix);
           
            if (variation > TOLERANCE) {
                auMoinsUnPrixAChange = true;
            }

            if (nouveauPrix <= 0) {
                toutesVariationsValides = false;
                break;
            }
        }
        
        assertTrue("Au moins un prix devrait avoir significativement changé", auMoinsUnPrixAChange);
        assertTrue("Tous les prix devraient rester positifs", toutesVariationsValides);
    }

    @Test
    public void testMiseajourMetAJourPourcentage() {
        bourse.Miseajour();
        for (Entreprise entreprise : bourse.getEntreprises().values()) {
            assertNotNull("Le pourcentage ne devrait pas être null", entreprise.getPourcentage());
        }
    }

    @Test
    public void testMiseajourMetAJourPrixMoyen() {
        bourse.Miseajour();
        for (Entreprise entreprise : bourse.getEntreprises().values()) {
            assertTrue("Le prix moyen devrait être > 0", entreprise.getPrixMoyen() > 0);
        }
    }

    @Test
    public void testGenererObligations() {
        for (Entreprise entreprise : bourse.getEntreprises().values()) {
            assertNotNull("L'entreprise devrait pouvoir avoir des obligations", entreprise);
            assertTrue("Le prix de départ devrait être positif", entreprise.getPrixDepart() > 0);
        }
    }
    
    @Test
    public void testActualiteApresMiseajour() {
        int nbEvenementsAvant = bourse.getActualite().getEvenements().size();
        bourse.Miseajour();
        int nbEvenementsApres = bourse.getActualite().getEvenements().size();
        assertTrue("Le nombre d'événements devrait changer", nbEvenementsApres >= nbEvenementsAvant - 1 && nbEvenementsApres <= nbEvenementsAvant + 1);                               
    }
}