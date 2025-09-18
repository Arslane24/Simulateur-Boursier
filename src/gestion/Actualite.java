package gestion;

import data.Evenement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Actualite {
    private List<String> notifications;
    private HashMap<String, Evenement> evenements;

    public Actualite() {
        this.notifications = new ArrayList<>();
        this.evenements = genererlistevenements(1); 
    }

    public HashMap<String, Evenement> Miseajour() {
        List<String> idsEvenements = new ArrayList<>(evenements.keySet());
        List<String> idsASupprimer = new ArrayList<>();    
        for (String idEvenement : idsEvenements) {
            Evenement evenement = evenements.get(idEvenement);
            int nouvelleDuree = evenement.getDuree() - 1;
            evenement.setDuree(nouvelleDuree);
            if (nouvelleDuree <= 0) {
                idsASupprimer.add(idEvenement);
            }
        } 
        for (String idASupprimer : idsASupprimer) {
            evenements.remove(idASupprimer);
        }
        if (evenements.isEmpty()) {
            HashMap<String, Evenement> nouveauxEvenements = genererlistevenements(1);
            Evenement nouvelEvenement = nouveauxEvenements.values().iterator().next();
            evenements.put(nouvelEvenement.getId(), nouvelEvenement);
            String messageNouveau = nouvelEvenement.getNom() + " (Secteur: " + nouvelEvenement.getSecteur() + (nouvelEvenement.getSousSecteurCible() != null ? ", Sous-secteur: " + nouvelEvenement.getSousSecteurCible() : "")+ ", Catégorie: " + nouvelEvenement.getCategorie()+ ", Impact: " + nouvelEvenement.getDegres() + ")";
            ajouterNotification(messageNouveau);
        }
        return evenements;
    }

    public HashMap<String, Evenement> genererlistevenements(int nombreEvenements) {
        HashMap<String, Evenement> resultat = new HashMap<>();
        List<String> lignesFichier = new ArrayList<>();
        List<String> lignesPositives = new ArrayList<>();
        String cheminFichier = "src/events.csv";
        try (BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier))) {
            lecteur.readLine(); 
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
            if (!ligne.trim().isEmpty()) {
               lignesFichier.add(ligne);
               String[] parties = ligne.split(",", -1);
               if (parties.length >= 3) {
               try {
               int degres = Integer.parseInt(parties[2].trim());
               if (degres > 0) {
               lignesPositives.add(ligne);
               }
               } catch (NumberFormatException ignored) {
            	   
                  }
               }
              }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<Integer> indicesAleatoires = new HashSet<>();
        Random generateur = new Random();
        int nombreLignes = lignesFichier.size();

        
        for (int i = 0; i < nombreEvenements; i++) {
            if (generateur.nextDouble() < 0.6 && !lignesPositives.isEmpty()) {
                int index = generateur.nextInt(lignesPositives.size());
                String ligne = lignesPositives.get(index);
                String[] parties = ligne.split(",", -1);
                if (parties.length >= 9) {
                    String id = parties[0].trim();
                    String nom = parties[1].trim();
                    int degres = Integer.parseInt(parties[2].trim());
                    String secteur = parties[3].trim();
                    String sousSecteurCible;
                    String trimmed = parties[4].trim();
                    if (trimmed.isEmpty()) {
                        sousSecteurCible = null;
                    } else {
                        sousSecteurCible = trimmed;
                    }
                    int duree = Integer.parseInt(parties[5].trim());
                    int vie = Integer.parseInt(parties[6].trim());
                    String categorie = parties[7].trim();
                    String description = parties[8].trim();
                    Evenement nouvelEvenement = new Evenement(id, nom, degres, secteur, sousSecteurCible, duree, vie, categorie, description);
                    resultat.put(id, nouvelEvenement);
                }
            } else {
                int index = generateur.nextInt(nombreLignes);
                String ligne = lignesFichier.get(index);
                String[] parties = ligne.split(",", -1);
                if (parties.length >= 9) {
                    String id = parties[0].trim();
                    String nom = parties[1].trim();
                    int degres = Integer.parseInt(parties[2].trim());
                    String secteur = parties[3].trim();
                    
                    String sousSecteurCible;
                    String trimmed = parties[4].trim();
                    if (trimmed.isEmpty()) {
                        sousSecteurCible = null;
                    } else {
                        sousSecteurCible = trimmed;
                    }
                    
                    int duree = Integer.parseInt(parties[5].trim());
                    int vie = Integer.parseInt(parties[6].trim());
                    String categorie = parties[7].trim();
                    String description = parties[8].trim();
                    Evenement nouvelEvenement = new Evenement(id, nom, degres, secteur, sousSecteurCible, duree, vie, categorie, description);
                    resultat.put(id, nouvelEvenement);
                }
            }
        }

        return resultat;
    }

    public void ajouterNotification(String message) {
        this.notifications.add(message);
    }

    public String[] getNotification() {
        String[] tableauNotifications = new String[notifications.size()];
        for (int i = 0; i < notifications.size(); i++) {
            tableauNotifications[i] = notifications.get(i);
        }
        notifications.clear();
        return tableauNotifications;
    }

    public HashMap<String, Evenement> getEvenements() {
        return evenements;
    }
}