package gui;

import data.Entreprise;
import data.Obligation;
import gestion.Bourse;
import gestion.GestionPortfeuille;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ListeActionsPanel extends JPanel {
    
    private Bourse bourse;
    private GestionPortfeuille gestionPortfeuille;
    private JTable actionsTable;
    private JTable obligationsTable;
    private Tableaudedonnes actionsTableModel;
    private Tableaudedonnes obligationsTableModel;
    private Map<String, Obligation> obligations;
    
    public ListeActionsPanel(Bourse bourse, GestionPortfeuille gestionPortfeuille) {
        this.bourse = bourse;
        this.gestionPortfeuille = gestionPortfeuille;
        this.obligations = genererObligations();
        
        initComponents();
        setupTables();
        setupPopupMenus();
        setupSearchAndFilter();
        updateDisplay();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        String[] actionsColumnNames = {"ID", "Nom", "Prix (€)", "Variation (%)", "Secteur", "Capital (M€)"};
        actionsTableModel = new Tableaudedonnes(actionsColumnNames, 0);
        actionsTable = new JTable(actionsTableModel);
        actionsTable.setRowHeight(25);
        PersonalisationTable perso = new PersonalisationTable();
        actionsTable.getColumnModel().getColumn(2).setCellRenderer(perso);
        actionsTable.getColumnModel().getColumn(3).setCellRenderer(perso);
        int[] actionsWidths = {50, 150, 80, 80, 100, 100};
        for (int i = 0; i < actionsWidths.length; i++) {
            actionsTable.getColumnModel().getColumn(i).setPreferredWidth(actionsWidths[i]);
        }
        actionsPanel.add(new JScrollPane(actionsTable), BorderLayout.CENTER);
        JPanel obligationsPanel = new JPanel(new BorderLayout());
        obligationsPanel.setBorder(BorderFactory.createTitledBorder("Obligations"));
        
        String[] obligationsColumnNames = {"ID", "Nom", "Prix (€)", "Taux d’intérêt (%)", "Échéance (ans)", "Secteur", "Capital (M€)"};
        obligationsTableModel = new Tableaudedonnes(obligationsColumnNames, 0);
        
        obligationsTable = new JTable(obligationsTableModel);
        obligationsTable.setRowHeight(25);
        
        obligationsTable.getColumnModel().getColumn(2).setCellRenderer(perso);
        obligationsTable.getColumnModel().getColumn(3).setCellRenderer(perso);
        
        int[] obligationsWidths = {50, 150, 80, 100, 80, 100, 100};
        for (int i = 0; i < obligationsWidths.length; i++) {
            obligationsTable.getColumnModel().getColumn(i).setPreferredWidth(obligationsWidths[i]);
        }
        
        obligationsPanel.add(new JScrollPane(obligationsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Actions", actionsPanel);
        tabbedPane.addTab("Obligations", obligationsPanel);
        add(new JLabel("Marché", JLabel.CENTER) {{
            setFont(new Font("Arial", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        }}, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupTables() {
        actionsTable.addMouseListener(new TableMouseHandler(true));
        obligationsTable.addMouseListener(new TableMouseHandler(false));
    }
    
    private void setupPopupMenus() {
        JPopupMenu actionsPopupMenu = new JPopupMenu();
        JMenuItem acheterActionMenuItem = new JMenuItem("Acheter");
        acheterActionMenuItem.addActionListener(new AcheterAction(true));
        actionsPopupMenu.add(acheterActionMenuItem);
        JPopupMenu obligationsPopupMenu = new JPopupMenu();
        JMenuItem acheterObligationMenuItem = new JMenuItem("Acheter");
        acheterObligationMenuItem.addActionListener(new AcheterAction(false));
        obligationsPopupMenu.add(acheterObligationMenuItem);
        
        actionsTable.setComponentPopupMenu(actionsPopupMenu);
        obligationsTable.setComponentPopupMenu(obligationsPopupMenu);
    }
    
    private void setupSearchAndFilter() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Rechercher une entreprise...");
        searchField.addActionListener(new SearchAction(searchField));
        searchPanel.add(new JLabel("Rechercher:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> sectorComboBox = new JComboBox<>();
        sectorComboBox.addItem("Tous");
        ArrayList<String> secteurs = new ArrayList<>();
        for (Entreprise entreprise : bourse.getEntreprises().values()) {
            if (!secteurs.contains(entreprise.getSecteur())) {
                secteurs.add(entreprise.getSecteur());
            }
        }
        for (String secteur : secteurs) {
            sectorComboBox.addItem(secteur);
        }
        sectorComboBox.addActionListener(new SectorFilterAction(sectorComboBox));
        filterPanel.add(new JLabel("Secteur:"));
        filterPanel.add(sectorComboBox);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.SOUTH);
    }
    private Map<String, Obligation> genererObligations() {
        Map<String, Obligation> obligationsMap = new HashMap<>();
        Random random = new Random();
        for (Entreprise entreprise : bourse.getEntreprises().values()) {
            double tauxInteret = 2.0 + random.nextDouble() * 3.0; 
            int echeance = 3 + random.nextInt(8); 
            double prix = entreprise.getPrixDepart();
            Obligation obligation = new Obligation(tauxInteret, echeance, entreprise, prix);
            obligationsMap.put(entreprise.getID(), obligation);
        }
        return obligationsMap;
    }
    private class AcheterAction implements ActionListener {
        private boolean isAction;
        
        public AcheterAction(boolean isAction) {
            this.isAction = isAction;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isAction) {
                handleAchatAction();
            } else {
                handleAchatObligation();
            }
        }
    }
    
    private class SearchAction implements ActionListener {
        private JTextField searchField;
        
        public SearchAction(JTextField searchField) {
            this.searchField = searchField;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            applySearchFilter(searchField.getText());
        }
    }
    
    private class SectorFilterAction implements ActionListener {
        private JComboBox<String> sectorComboBox;
        
        public SectorFilterAction(JComboBox<String> sectorComboBox) {
            this.sectorComboBox = sectorComboBox;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            applySectorFilter((String) sectorComboBox.getSelectedItem());
        }
    }
    
    private class TableMouseHandler implements MouseListener {
        private boolean isAction; 
        
        public TableMouseHandler(boolean isAction) {
            this.isAction = isAction;
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                if (isAction) {
                    handleAchatAction();
                } else {
                    handleAchatObligation();
                }
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
    
    private void handleAchatAction() {
        int row = actionsTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) actionsTableModel.getValueAt(row, 0);
            Entreprise entreprise = bourse.getEntreprises().get(id);
            gestionPortfeuille.acheterAction(entreprise);
        }
    }
    
    private void handleAchatObligation() {
        int row = obligationsTable.getSelectedRow();
        if (row >= 0) {
            String id = (String) obligationsTableModel.getValueAt(row, 0);
            Obligation obligation = obligations.get(id);
            gestionPortfeuille.acheterObligation(obligation);
        }
    }
    
    private void applySearchFilter(String searchTerm) {
        TableRowSorter<DefaultTableModel> actionsSorter = new TableRowSorter<>(actionsTableModel);
        actionsTable.setRowSorter(actionsSorter);
        if (searchTerm.isEmpty()) {
            actionsSorter.setRowFilter(null);
        } else {
            actionsSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm, 1, 4)); 
        }
        TableRowSorter<DefaultTableModel> obligationsSorter = new TableRowSorter<>(obligationsTableModel);
        obligationsTable.setRowSorter(obligationsSorter);
        if (searchTerm.isEmpty()) {
            obligationsSorter.setRowFilter(null);
        } else {
            obligationsSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm, 1, 5));
        }
    }
    
    private void applySectorFilter(String selectedSector) {
        TableRowSorter<DefaultTableModel> actionsSorter = new TableRowSorter<>(actionsTableModel);
        actionsTable.setRowSorter(actionsSorter);
        if ("Tous".equals(selectedSector)) {
            actionsSorter.setRowFilter(null);
        } else {
            actionsSorter.setRowFilter(RowFilter.regexFilter("^" + selectedSector + "$", 4));
        }
        TableRowSorter<DefaultTableModel> obligationsSorter = new TableRowSorter<>(obligationsTableModel);
        obligationsTable.setRowSorter(obligationsSorter);
        if ("Tous".equals(selectedSector)) {
            obligationsSorter.setRowFilter(null);
        } else {
            obligationsSorter.setRowFilter(RowFilter.regexFilter("^" + selectedSector + "$", 5));
        }
    }
    
    public void updateDisplay() {
        Map<String, Entreprise> entreprises = bourse.getEntreprises();
        for (String id : entreprises.keySet()) {
            Entreprise entreprise = entreprises.get(id);
            boolean entrepriseTrouvee = false;
            for (int i = 0; i < actionsTableModel.getRowCount(); i++) {
                String currentId = (String) actionsTableModel.getValueAt(i, 0);
                if (currentId.equals(id)) {
                    actionsTableModel.setValueAt(entreprise.getPrixDepart(), i, 2);
                    actionsTableModel.setValueAt(entreprise.getPourcentage(), i, 3);
                    entrepriseTrouvee = true;
                    break;
                }
            }
            if (!entrepriseTrouvee) {
                actionsTableModel.addRow(new Object[]{id,entreprise.getNom(),entreprise.getPrixDepart(),entreprise.getPourcentage(),entreprise.getSecteur(),entreprise.getCapital()});
            }
        }
        for (String id : obligations.keySet()) {
            Obligation obligation = obligations.get(id);
            Entreprise entreprise = obligation.getEntreprise();
            boolean obligationTrouvee = false;
            for (int i = 0; i < obligationsTableModel.getRowCount(); i++) {
                String currentId = (String) obligationsTableModel.getValueAt(i, 0);
                if (currentId.equals(id)) {
                    obligationsTableModel.setValueAt(obligation.getPrixActu(), i, 2);
                    obligationsTableModel.setValueAt(obligation.getTauxInteret(), i, 3);
                    obligationsTableModel.setValueAt(obligation.getEcheance(), i, 4);
                    obligationTrouvee = true;
                    break;
                }
            }
            if (!obligationTrouvee) {
                obligationsTableModel.addRow(new Object[]{id,entreprise.getNom(),obligation.getPrixActu(),obligation.getTauxInteret(),obligation.getEcheance(),entreprise.getSecteur(),entreprise.getCapital()});   
            }
        }
        if (actionsTable.getRowSorter() != null) {
            TableRowSorter<DefaultTableModel> actionsSorter = (TableRowSorter<DefaultTableModel>) actionsTable.getRowSorter();
            actionsSorter.sort();
        }
        if (obligationsTable.getRowSorter() != null) {
            TableRowSorter<DefaultTableModel> obligationsSorter = (TableRowSorter<DefaultTableModel>) obligationsTable.getRowSorter();
            obligationsSorter.sort();
        }
    }
}