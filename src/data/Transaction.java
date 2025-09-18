package data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type; 
    private String action; 
    private Entreprise entreprise;
    private int quantite;
    private double prixUnitaire;
    private double total;
    private LocalDateTime date;

    public Transaction(String type, String action, Entreprise entreprise, int quantite, double prixUnitaire, double total, LocalDateTime date) {
        this.type = type;
        this.action = action;
        this.entreprise = entreprise;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.total = total;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return date.format(formatter);
    }
}