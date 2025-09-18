package gui;

import gestion.GestionPortfeuille;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class InfoPanel extends JPanel {
    
    private GestionPortfeuille gestionPortfeuille;
    private JLabel soldeLabel;
    private JLabel investiLabel;
    private JLabel beneficeLabel;
    private JProgressBar healthBar;
    
    public InfoPanel(GestionPortfeuille gestionPortfeuille) {
        this.gestionPortfeuille = gestionPortfeuille;
        
        setLayout(new GridLayout(1, 4, 10, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        JPanel soldePanel = createInfoPanel("Solde Disponible");
        soldeLabel = new JLabel();
        soldeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        soldePanel.add(soldeLabel);
        JPanel investiPanel = createInfoPanel("Montant Investi");
        investiLabel = new JLabel();
        investiLabel.setFont(new Font("Arial", Font.BOLD, 16));
        investiPanel.add(investiLabel);
        JPanel beneficePanel = createInfoPanel("Bénéfice/Perte");
        beneficeLabel = new JLabel();
        beneficeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        beneficePanel.add(beneficeLabel);
        JPanel healthPanel = createInfoPanel("Santé du Portefeuille");
        healthBar = new JProgressBar(0, 100);
        healthBar.setStringPainted(true);
        healthBar.setBorderPainted(true);
        healthBar.setFont(new Font("Arial", Font.BOLD, 12));
        healthPanel.add(healthBar);
        add(soldePanel);
        add(investiPanel);
        add(beneficePanel);
        add(healthPanel);
        updateInfo();
    }
    
    private JPanel createInfoPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
    
    public void updateInfo() {
        double solde = gestionPortfeuille.getSolde();
        double soldeInvesti = gestionPortfeuille.getSoldeInvesti();
        double benefice = gestionPortfeuille.calculerBeneficeValue();
        
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        soldeLabel.setText(currencyFormatter.format(solde));
        investiLabel.setText(currencyFormatter.format(soldeInvesti));
        String beneficeStr = currencyFormatter.format(benefice);
        if (benefice > 0) {
            beneficeLabel.setForeground(new Color(0, 150, 0)); 
            beneficeLabel.setText("+" + beneficeStr);
        } else if (benefice < 0) {
            beneficeLabel.setForeground(Color.RED);
            beneficeLabel.setText(beneficeStr);
        } else {
            beneficeLabel.setForeground(Color.BLACK);
            beneficeLabel.setText(beneficeStr);
        }
        int health = calculatePortfolioHealth(solde, soldeInvesti, benefice);
        healthBar.setValue(health);
        if (health < 30) {
            healthBar.setForeground(Color.RED);
        } else if (health < 70) {
            healthBar.setForeground(Color.ORANGE);
        } else {
            healthBar.setForeground(new Color(0, 150, 0)); 
        }
    }
    
    private int calculatePortfolioHealth(double solde, double soldeInvesti, double benefice) {
        if (soldeInvesti == 0) {
            return 50;
        }
        double profitPercentage = (benefice / soldeInvesti) * 100;
        int health = 50 + (int)(profitPercentage * 1.2);
        return Math.max(0, Math.min(100, health));
    }
}