package test.unit;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import data.Entreprise;
import data.Transaction;


public class TestTransaction {
    private Transaction transaction;
    private Entreprise Arslane;

    @Before
    public void preparetransaction() {
        Arslane = new Entreprise("Arslane", "TC01", 100.0, "Technologie", "Logiciels", "EUR", "Euronext", 1000, 0.02, 5.0, 100.0, 500.0);
        transaction = new Transaction("Action", "Achat", Arslane, 10, 100.0, 1000.0, LocalDateTime.of(2025, 4, 25, 10, 0));
    }

    @Test
    public void testTransactionCreationAndGetters() {
        assertEquals("Action", transaction.getType());
        assertEquals("Achat", transaction.getAction());
        assertEquals(Arslane, transaction.getEntreprise());
        assertEquals(10, transaction.getQuantite());
        assertEquals(100.0, transaction.getPrixUnitaire(), 0);
        assertEquals(1000.0, transaction.getTotal(), 0);
        assertEquals("25/04/2025 10:00:00", transaction.getFormattedDate());
    }
}