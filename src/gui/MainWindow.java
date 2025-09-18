package gui;

import data.Entreprise;
import gestion.Bourse;
import gestion.GestionPortfeuille;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainWindow extends JFrame {
    
    private Bourse bourse;
    private GestionPortfeuille gestionPortfeuille;
    private ListeActionsPanel listeActionsPanel;
    private PortfeuillePanel portfeuillePanel;
    private GraphiquePanel graphiquePanel;
    private EvenementsPanel evenementsPanel;
    private InfoPanel infoPanel;
    
    private Thread simulationThread;
    private boolean simulationRunning = false;
    private boolean simulationPaused = false;
    private int tourCourant = 0;
    private static final int SIMULATION_SPEED = 1200;
    private JLabel turnLabel;
    private static final String[] SECTEURS = {"Technologie", "Finance", "Énergie", "Santé", "Industrie","Consommation", "Télécommunications", "Matériaux", "Macro"};
    private static final String[] DEVISES = {"EUR", "USD", "GBP", "JPY"};
    private static final String[] BOURSES = {"Euronext", "NYSE", "NASDAQ", "LSE"};

    public MainWindow() {
        super("Simulateur de Bourse");
        bourse = new Bourse();
        gestionPortfeuille = new GestionPortfeuille();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        infoPanel = new InfoPanel(gestionPortfeuille);
        listeActionsPanel = new ListeActionsPanel(bourse, gestionPortfeuille);
        portfeuillePanel = new PortfeuillePanel(gestionPortfeuille, bourse);
        graphiquePanel = new GraphiquePanel(bourse);
        evenementsPanel = new EvenementsPanel(bourse.getActualite());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Liste des Actions", listeActionsPanel);
        tabbedPane.addTab("Mon Portefeuille", portfeuillePanel);
        tabbedPane.addTab("Graphiques", graphiquePanel);
        JPanel controlPanel = createControlPanel();
        add(infoPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(evenementsPanel, BorderLayout.CENTER);
        bottomContainer.add(controlPanel, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);
        setVisible(true);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        turnLabel = new JLabel("Tour: 0");
        JButton nextTurnButton = new JButton("Tour Suivant");
        JToggleButton autoPlayButton = new JToggleButton("Lecture Auto");
        JButton createCompanyButton = new JButton("Créer Entreprise");
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        turnLabel.setFont(buttonFont);
        nextTurnButton.setFont(buttonFont);
        autoPlayButton.setFont(buttonFont);
        createCompanyButton.setFont(buttonFont);
        nextTurnButton.addActionListener(new NextTurnAction());
        autoPlayButton.addActionListener(new AutoPlayAction(autoPlayButton));
        createCompanyButton.addActionListener(new CreateCompanyAction());
        
        panel.add(turnLabel);
        panel.add(nextTurnButton);
        panel.add(autoPlayButton);
        panel.add(createCompanyButton);
        
        return panel;
    }
    
    private void showCreateCompanyDialog() {
        JDialog dialog = new JDialog(this, "Créer une Entreprise", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Nom de l'entreprise :"), gbc);
        gbc.gridx = 1;
        JTextField nomField = new JTextField(20);
        dialog.add(nomField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Secteur :"), gbc);
        gbc.gridx = 1;
        JPanel secteurPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup secteurGroup = new ButtonGroup();
        JRadioButton[] secteurButtons = new JRadioButton[SECTEURS.length];
        for (int i = 0; i < SECTEURS.length; i++) {
            secteurButtons[i] = new JRadioButton(SECTEURS[i]);
            secteurGroup.add(secteurButtons[i]);
            secteurPanel.add(secteurButtons[i]);
        }
        secteurButtons[0].setSelected(true);
        dialog.add(new JScrollPane(secteurPanel), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Sous-secteur :"), gbc);
        gbc.gridx = 1;
        JComboBox<String> sousSecteurComboBox = new JComboBox<>();
        Set<String> sousSecteurs = new HashSet<>();
        for (Entreprise e : bourse.getEntreprises().values()) {
            if (!e.getSousSecteur().isEmpty()) {
                sousSecteurs.add(e.getSousSecteur());
            }
        }
        for (String sousSecteur : sousSecteurs) {
            sousSecteurComboBox.addItem(sousSecteur);
        }
        dialog.add(sousSecteurComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Devise :"), gbc);
        gbc.gridx = 1;
        JComboBox<String> deviseComboBox = new JComboBox<>(DEVISES);
        dialog.add(deviseComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Bourse :"), gbc);
        gbc.gridx = 1;
        JComboBox<String> bourseComboBox = new JComboBox<>(BOURSES);
        dialog.add(bourseComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Capital initial (M€) :"), gbc);
        gbc.gridx = 1;
        JTextField capitalField = new JTextField(20);
        dialog.add(capitalField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        dialog.add(new JLabel("Prix initial par action (€) :"), gbc);
        gbc.gridx = 1;
        JTextField prixActionField = new JTextField(20);
        dialog.add(prixActionField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        dialog.add(new JLabel("Volume moyen:"), gbc);
        gbc.gridx = 1;
        JTextField volumeMoyenField = new JTextField(20);
        dialog.add(volumeMoyenField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        dialog.add(new JLabel("Volatilité (%) :"), gbc);
        gbc.gridx = 1;
        JTextField volatiliteField = new JTextField(20);
        dialog.add(volatiliteField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        dialog.add(new JLabel("Variation initiale (%) :"), gbc);
        gbc.gridx = 1;
        JTextField pourcentageField = new JTextField(20);
        dialog.add(pourcentageField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton validateButton = new JButton("Valider");
        JButton cancelButton = new JButton("Annuler");
        validateButton.addActionListener(new ValidateCompanyAction(dialog, nomField, secteurButtons, sousSecteurComboBox, deviseComboBox, bourseComboBox, capitalField, prixActionField, volumeMoyenField, volatiliteField, pourcentageField));
        cancelButton.addActionListener(new CancelAction(dialog));
        buttonPanel.add(validateButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private class NextTurnAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            nextTurn();
            updateTurnLabel();
        }
    }
    
    private class AutoPlayAction implements ActionListener {
        private final JToggleButton button;
        
        public AutoPlayAction(JToggleButton button) {
            this.button = button;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (button.isSelected()) {
                startAutoSimulation();
                button.setText("Pause");
            } else {
                pauseAutoSimulation();
                button.setText("Lecture Auto");
            }
        }
    }
    
    private class CreateCompanyAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showCreateCompanyDialog();
        }
    }
    
    private class ValidateCompanyAction implements ActionListener {
        private final JDialog dialog;
        private final JTextField nomField;
        private final JRadioButton[] secteurButtons;
        private final JComboBox<String> sousSecteurComboBox;
        private final JComboBox<String> deviseComboBox;
        private final JComboBox<String> bourseComboBox;
        private final JTextField capitalField;
        private final JTextField prixActionField;
        private final JTextField volumeMoyenField;
        private final JTextField volatiliteField;
        private final JTextField pourcentageField;
        public ValidateCompanyAction(JDialog dialog, JTextField nomField, JRadioButton[] secteurButtons,JComboBox<String> sousSecteurComboBox, JComboBox<String> deviseComboBox,JComboBox<String> bourseComboBox, JTextField capitalField,JTextField prixActionField, JTextField volumeMoyenField,JTextField volatiliteField, JTextField pourcentageField) {
            this.dialog = dialog;
            this.nomField = nomField;
            this.secteurButtons = secteurButtons;
            this.sousSecteurComboBox = sousSecteurComboBox;
            this.deviseComboBox = deviseComboBox;
            this.bourseComboBox = bourseComboBox;
            this.capitalField = capitalField;
            this.prixActionField = prixActionField;
            this.volumeMoyenField = volumeMoyenField;
            this.volatiliteField = volatiliteField;
            this.pourcentageField = pourcentageField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String nom = nomField.getText().trim();
                String secteur = null;
                for (JRadioButton button : secteurButtons) {
                    if (button.isSelected()) {
                        secteur = button.getText();
                        break;
                    }
                }
                String sousSecteur = (String) sousSecteurComboBox.getSelectedItem();
                String devise = (String) deviseComboBox.getSelectedItem();
                String bourseEntreprise = (String) bourseComboBox.getSelectedItem();
                double capital = Double.parseDouble(capitalField.getText().trim());
                double prixAction = Double.parseDouble(prixActionField.getText().trim());
                int volumeMoyen = Integer.parseInt(volumeMoyenField.getText().trim());
                double volatilite = Double.parseDouble(volatiliteField.getText().trim());
                double pourcentage = Double.parseDouble(pourcentageField.getText().trim());

                if (nom.isEmpty() || secteur == null || sousSecteur == null) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String id = UUID.randomUUID().toString();
                Entreprise entreprise = new Entreprise(
                    nom, id, prixAction, secteur, sousSecteur,
                    devise, bourseEntreprise, volumeMoyen, volatilite, pourcentage, prixAction, capital
                );
                bourse.ajouterEntreprise(entreprise);
                listeActionsPanel.updateDisplay();
                JOptionPane.showMessageDialog(dialog, "Entreprise " + nom + " créée avec succès !");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Veuillez entrer des valeurs numériques valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private class CancelAction implements ActionListener {
        private final JDialog dialog;
        
        public CancelAction(JDialog dialog) {
            this.dialog = dialog;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }
    
    private void startAutoSimulation() {
        if (simulationThread != null) {
            simulationPaused = false;
            return;
        }
        
        simulationRunning = true;
        simulationPaused = false;
        
        simulationThread = new Thread() {
            public void run() {
                while (simulationRunning) {
                    if (!simulationPaused) {
                        nextTurn();
                        updateTurnLabel();
                        try {
                            Thread.sleep(SIMULATION_SPEED);
                        } catch (InterruptedException e) {
                            return;
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        };
        
        simulationThread.start();
    }

    private void updateTurnLabel() {
        turnLabel.setText("Tour: " + tourCourant);
    }

    private void pauseAutoSimulation() {
        simulationPaused = true;
    }

    private void stopAutoSimulation() {
        simulationRunning = false;
        simulationPaused = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }
    
    private void nextTurn() {
        tourCourant++;
        bourse.Miseajour();
        
        listeActionsPanel.updateDisplay();
        portfeuillePanel.updateDisplay();
        graphiquePanel.updateGraphs();
        evenementsPanel.updateEvents();
        infoPanel.updateInfo();
    }
    
    @Override
    public void dispose() {
        stopAutoSimulation();
        super.dispose();
    }
}