package gui;

import data.Actif;
import data.Action;
import data.Entreprise;
import data.Obligation;
import data.Transaction;
import gestion.Bourse;
import gestion.GestionPortfeuille;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class PortfeuillePanel extends JPanel {
    
    private GestionPortfeuille gestionPortfeuille;
    private Bourse bourse;
    private JTable holdingsTable;
    private TableauPortefeuille tableModel;
    private JPopupMenu popupMenu;
    private JLabel summaryLabel;
    private ChartPanel pieChartPanel;
    private JTable historyTable;
    private TableauHistorique historyTableModel;
    private Map<String, Double> historiqueInvestissements;
    
    private static final Map<String, Color> SECTOR_COLORS = new HashMap<>();
    private static final List<String> LISTE_SECTEURS = Arrays.asList("Technologie","Finance","Énergie","Santé","Industrie","Consommation","Télécommunications","Matériaux","Macro","Transport","Aucun investissement");
    static {
        SECTOR_COLORS.put("Technologie", new Color(0, 128, 255));
        SECTOR_COLORS.put("Finance", new Color(0, 204, 0));
        SECTOR_COLORS.put("Énergie", new Color(255, 0, 0));
        SECTOR_COLORS.put("Santé", new Color(255, 215, 0));
        SECTOR_COLORS.put("Industrie", new Color(128, 0, 128));
        SECTOR_COLORS.put("Consommation", new Color(255, 165, 0));
        SECTOR_COLORS.put("Télécommunications", new Color(0, 255, 255));
        SECTOR_COLORS.put("Matériaux", new Color(139, 69, 19));
        SECTOR_COLORS.put("Macro", new Color(128, 128, 128));
        SECTOR_COLORS.put("Transport", new Color(100, 100, 100));
        SECTOR_COLORS.put("Aucun investissement", Color.LIGHT_GRAY);
    }
    
    public PortfeuillePanel(GestionPortfeuille gestionPortfeuille, Bourse bourse) {
        this.gestionPortfeuille = gestionPortfeuille;
        this.bourse = bourse;
        this.historiqueInvestissements = new HashMap<>();
        setLayout(new BorderLayout());
        initComponents();
        setupTable();
        updateDisplay();
    }
    
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel actifsPanel = new JPanel(new BorderLayout());
        actifsPanel.setBorder(BorderFactory.createTitledBorder("Actifs Détenus"));
        String[] columnNames = {"Type", "Entreprise", "Quantité", "Prix d'achat (€)", "Prix actuel (€)", "Variation (%)", "Valeur totale (€)"};
        tableModel = new TableauPortefeuille(columnNames, 0);       
        holdingsTable = new JTable(tableModel);
        holdingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        holdingsTable.setRowHeight(25);        
        PersoTableauPortfeuille perso = new PersoTableauPortfeuille();
        holdingsTable.getColumnModel().getColumn(3).setCellRenderer(perso);
        holdingsTable.getColumnModel().getColumn(4).setCellRenderer(perso);
        holdingsTable.getColumnModel().getColumn(5).setCellRenderer(perso);
        holdingsTable.getColumnModel().getColumn(6).setCellRenderer(perso);        
        int[] widths = {80, 150, 70, 100, 100, 80, 100};
        for (int i = 0; i < widths.length; i++) {
            holdingsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }       
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryLabel = new JLabel();
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);       
        JLabel titleLabel = new JLabel("Mon Portefeuille", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        actifsPanel.add(new JScrollPane(holdingsTable), BorderLayout.CENTER);
        actifsPanel.add(summaryPanel, BorderLayout.SOUTH);
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        pieChartPanel = createPieChartPanel();
        pieChartPanel.setPreferredSize(new Dimension(400, 300));
        statsPanel.add(pieChartPanel, BorderLayout.CENTER);
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Historique des Transactions"));
        String[] historyColumnNames = {"Type", "Action", "Entreprise", "Quantité", "Prix (€)", "Total (€)", "Date"};
        historyTableModel = new TableauHistorique(historyColumnNames, 0);
        historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(25);
        historyTable.getColumnModel().getColumn(4).setCellRenderer(perso);
        historyTable.getColumnModel().getColumn(5).setCellRenderer(perso);
        int[] historyWidths = {80, 80, 150, 70, 100, 100, 120};
        for (int i = 0; i < historyWidths.length; i++) {
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(historyWidths[i]);
        }
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        tabbedPane.addTab("Actifs Détenus", actifsPanel);
        tabbedPane.addTab("Statistiques", statsPanel);
        tabbedPane.addTab("Historique", historyPanel);
        add(titleLabel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupTable() {
        holdingsTable.addMouseListener(new TableMouseHandler());
    }

    private ChartPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Aucun investissement", 100);
        JFreeChart chart = ChartFactory.createPieChart("Investissements historiques par secteur",dataset,true,true,false);
        PiePlot plot = (PiePlot) chart.getPlot();
        setFixedSectorColors(plot);
        chart.setBackgroundPaint(getBackground());
        return new ChartPanel(chart);
    }
    private void setFixedSectorColors(PiePlot plot) {
        for (Map.Entry<String, Color> entry : SECTOR_COLORS.entrySet()) {
            plot.setSectionPaint(entry.getKey(), entry.getValue());
        }
    }

    private void updatePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        historiqueInvestissements.clear();
        for (Transaction transaction : gestionPortfeuille.getHistoriqueTransactions()) {
            if (transaction.getAction().equals("Achat")) {
                String secteur = transaction.getEntreprise().getSecteur();
                double montant = transaction.getTotal();
                historiqueInvestissements.put(secteur, historiqueInvestissements.getOrDefault(secteur, 0.0) + montant);
            }
        }
        double totalInvestiHistorique = historiqueInvestissements.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalInvestiHistorique > 0) {
            for (String secteur : LISTE_SECTEURS) {
                if (!secteur.equals("Aucun investissement") && historiqueInvestissements.containsKey(secteur)) {
                    double montant = historiqueInvestissements.get(secteur);
                    double pourcentage = (montant / totalInvestiHistorique) * 100;
                    if (pourcentage > 0) {
                        dataset.setValue(secteur + " (" + String.format("%.1f%%", pourcentage) + ")", pourcentage);
                    }
                }
            }
        }
        if (dataset.getItemCount() == 0) {
            dataset.setValue("Aucun investissement", 100.0);
        }

        JFreeChart chart = pieChartPanel.getChart();
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setDataset(dataset);
        setFixedSectorColors(plot);
    }

    private class TableMouseHandler implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                handleVendreAction();
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
        	
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        	
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
        	
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
        	
        }
    }
    
    private void handleVendreAction() {
        int row = holdingsTable.getSelectedRow();
        if (row >= 0) {
            String type = (String) tableModel.getValueAt(row, 0);
            String nomEntreprise = (String) tableModel.getValueAt(row, 1);
            
            Entreprise entreprise = null;
            for (Entreprise ent : bourse.getEntreprises().values()) {
                if (ent.getNom().equals(nomEntreprise)) {
                    entreprise = ent;
                    break;
                }
            }
            
            if (entreprise != null) {
                if ("Action".equals(type)) {
                    gestionPortfeuille.vendreAction(entreprise);
                } else if ("Obligation".equals(type)) {
                    ArrayList<Actif> actifs = gestionPortfeuille.getPortfeuille().getActifs();
                    for (Actif actif : actifs) {
                        if (!actif.isAction() && actif.getEntreprise().equals(entreprise)) {
                            gestionPortfeuille.VendreObligation((Obligation) actif);
                            break;
                        }
                    }
                }
                updateDisplay();
            }
        }
    }
    
    public void updateDisplay() {
        tableModel.setRowCount(0);
        ArrayList<Actif> actifs = gestionPortfeuille.getPortfeuille().getActifs();
        
        for (Actif actif : actifs) {
            Entreprise e = actif.getEntreprise();
            
            if (actif.isAction()) {
                Action action = (Action) actif;
                int quantite = action.getQuantite();
                double prixAchat = action.getPrixActu();
                double prixActuel = e.getPrixDepart();
                double variation = ((prixActuel - prixAchat) / prixAchat) * 100;
                double valeurTotale = quantite * prixActuel;              
                tableModel.addRow(new Object[]{"Action",e.getNom(),quantite,prixAchat,prixActuel,variation,valeurTotale});
            } else {
                Obligation obligation = (Obligation) actif;
                double prixAchat = obligation.getPrixActu();
                double prixActuel = prixAchat;
                tableModel.addRow(new Object[]{"Obligation",e.getNom(), 1,prixAchat, prixActuel,0.0,prixActuel});
            }
        }
        historyTableModel.setRowCount(0);
        for (Transaction transaction : gestionPortfeuille.getHistoriqueTransactions()) {
            historyTableModel.addRow(new Object[]{
                transaction.getType(),
                transaction.getAction(),
                transaction.getEntreprise().getNom(),
                transaction.getQuantite(),
                transaction.getPrixUnitaire(),
                transaction.getTotal(),
                transaction.getFormattedDate()
            });
        }
        
        String soldeActuel = gestionPortfeuille.infoSoldeActuel();
        String soldeInvesti = gestionPortfeuille.infoSoldeInvesti();
        String benefice = gestionPortfeuille.calculerBenefice();      
        summaryLabel.setText("<html><b>Résumé du portefeuille:</b><br>" + soldeActuel + "<br>" +soldeInvesti + "<br>" + benefice + "</html>");
        updatePieChart();
    }
}