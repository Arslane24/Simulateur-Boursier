package gui;

import gestion.Actualite;
import data.Evenement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EvenementsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Actualite actualite;
    private JPanel eventsContainer;
    private List<Evenement> evenementDetails;

    public EvenementsPanel(Actualite actualite) {
        this.actualite = actualite;
        this.eventsContainer = new JPanel();
        this.evenementDetails = new ArrayList<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Événements"));
        setPreferredSize(new Dimension(250, 200));
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(eventsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        updateEvents();
    }

    public void updateEvents() {
        String[] newEvents = actualite.getNotification();
        if (newEvents != null && newEvents.length > 0) {
            String latestEvent = newEvents[newEvents.length - 1];
            if (latestEvent != null && !latestEvent.isEmpty()) {
                Evenement evenement = null;
                for (Evenement e : actualite.getEvenements().values()) {
                    String message = e.getNom()+ " (Secteur: " + e.getSecteur()+ (e.getSousSecteurCible() != null ? ", Sous-secteur: " + e.getSousSecteurCible() : "")+ ", Catégorie: " + e.getCategorie()+ ", Impact: " + e.getDegres() + ")";
                    if (message.equals(latestEvent)) {
                        evenement = e;
                        break;
                    }
                }

                if (evenement != null) {
                    JButton eventButton = new JButton("<html>" + evenement.getNom().replace(" ", "&nbsp;") + "</html>");
                    eventButton.setFont(new Font("Arial", Font.PLAIN, 12));
                    eventButton.setHorizontalAlignment(SwingConstants.LEFT);
                    eventButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    eventButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    if (evenement.getDegres() > 0) {
                        eventButton.setBackground(new Color(0, 150, 0)); 
                    } else if (evenement.getDegres() < 0) {
                        eventButton.setBackground(Color.RED);
                    } else {
                        eventButton.setBackground(Color.LIGHT_GRAY);
                    }
                    eventButton.setOpaque(true);
                    eventButton.setBorderPainted(false);
                    Evenement finalEvenement = evenement;
                    eventButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showEventDetails(finalEvenement);
                        }
                    });
                    eventsContainer.add(eventButton);
                    eventsContainer.add(Box.createVerticalStrut(5));
                    evenementDetails.add(evenement);
                    eventsContainer.revalidate();
                    eventsContainer.repaint();
                    JScrollBar vertical = ((JScrollPane) eventsContainer.getParent().getParent()).getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                }
            }
        }
    }

    private void showEventDetails(Evenement evenement) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setPreferredSize(new Dimension(400, 250));
        detailsPanel.add(new JLabel("Événement: " + evenement.getNom()));
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(new JLabel("Catégorie: " + evenement.getCategorie()));
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(new JLabel("Secteur: " + evenement.getSecteur()));
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(new JLabel("Sous-secteur: " + (evenement.getSousSecteurCible() != null ? evenement.getSousSecteurCible() : "N/A")));
        detailsPanel.add(Box.createVerticalStrut(10));
        JTextArea descriptionArea = new JTextArea("Description: " + evenement.getDescription());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true); 
        descriptionArea.setWrapStyleWord(true); 
        descriptionArea.setEditable(false); 
        descriptionArea.setBackground(detailsPanel.getBackground()); 
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(380, 100));
        descriptionScroll.setBorder(BorderFactory.createEmptyBorder());
        detailsPanel.add(descriptionScroll);
        JOptionPane.showMessageDialog(this, detailsPanel, "Détails de l'événement", JOptionPane.INFORMATION_MESSAGE);
    }
}