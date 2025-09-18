package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import data.Evenement;
import gestion.Actualite;


public class TestActualite {
    private Actualite actualite;

    @Before
    public void prepareActualite() {
        actualite = new Actualite();
    }

    @Test
    public void testInitialEventGeneration() {
        HashMap<String, Evenement> evenements = actualite.getEvenements();
        assertNotNull(evenements);
        assertEquals(1, evenements.size());
    }

    @Test
    public void testMiseajour() {
        actualite.Miseajour();
        HashMap<String, Evenement> evenements = actualite.getEvenements();
        assertNotNull(evenements);
        assertTrue(evenements.size() <= 1); 
    }

    @Test
    public void testNotifications() {
        String[] notifications = actualite.getNotification();
        assertNotNull(notifications);
        actualite.ajouterNotification("Test notification");
        notifications = actualite.getNotification();
        assertEquals(1, notifications.length);
        assertEquals("Test notification", notifications[0]);
    }
}