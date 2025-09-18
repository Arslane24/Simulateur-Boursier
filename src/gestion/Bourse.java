package gestion;

import data.Entreprise;
import data.Evenement;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Bourse {
    private static Logger logger = LoggerUtility.getLogger(Bourse.class, "html"); 
    private HashMap<String, Entreprise> entreprises = new HashMap<>();
    private Actualite a;
    public int level = 2;
    private Map<String, Double> secteurCorrelation;

    public Bourse() {
        logger.info("Initialisation de la bourse");
        chargerEntreprises();
        this.a = new Actualite();
        this.secteurCorrelation = new HashMap<>();
        for (Map.Entry<String, Evenement> entry : a.getEvenements().entrySet()) {
            Evenement e = entry.getValue();
            logger.debug("Événement initial: " + e.getNom());
        }
    }
    public void chargerEntreprises() {
        logger.debug("Chargement des entreprises depuis entreprises_bourse.csv");
        String fichierCSV = "src/entreprises_bourse.csv";
        List<String> lignes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fichierCSV))) {
            String ligne;
            br.readLine();
            while ((ligne = br.readLine()) != null) {
                if (!ligne.trim().isEmpty()) {
                    lignes.add(ligne);
                }
            }
            logger.info("Nombre de lignes lues dans entreprises_bourse.csv: " + lignes.size());
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture de entreprises_bourse.csv", e);
        }
        int nombreEntreprises = Math.min(25, lignes.size());
        Set<Integer> indicesSelectionnes = genererEnsembleUnique(nombreEntreprises, lignes.size());
        for (int index : indicesSelectionnes) {
            String line = lignes.get(index);
            String[] data = line.split(",", -1);
            if (data.length >= 12) {
                Entreprise e = new Entreprise(
                    data[1], data[0], Double.parseDouble(data[2]), data[3], data[4],
                    data[5], data[6], Integer.parseInt(data[7]), Double.parseDouble(data[8]),
                    Double.parseDouble(data[9]), Double.parseDouble(data[10]), Double.parseDouble(data[11])
                );
                entreprises.put(e.getID(), e);
                logger.debug("Entreprise ajoutée: " + e.getNom());
            } else {
                logger.warn("Ligne invalide dans entreprises_bourse.csv: " + line);
            }
        }
        logger.info("Chargement des entreprises terminé. Total: " + entreprises.size());
    }

    public HashMap<String, Entreprise> Miseajour() {
        logger.info("Mise à jour des prix des entreprises");
        HashMap<String, Evenement> evenements = a.Miseajour();
        Random rand = new Random();
        double tendanceMacro = rand.nextGaussian() * 0.003;
        logger.debug("Tendance macro: " + tendanceMacro);
        Map<String, Double> secteurShock = new HashMap<>();
        for (Entreprise e : entreprises.values()) {
            String secteur = e.getSecteur();
            if (!secteurShock.containsKey(secteur)) {
                secteurShock.put(secteur, rand.nextGaussian() * 0.008);
            }
        }
        
        for (Map.Entry<String, Entreprise> entry : entreprises.entrySet()) {
            Entreprise entreprise = entry.getValue();
            double prixInitial = entreprise.getPrixDepart();
            double volatiliteBase = entreprise.getVolatilite();
            double deltaT = 1.0;

            double muSecteur = calculerTendanceSecteur(entreprise.getSousSecteur());
            double muEvenement = 0.0;
            double volatiliteEvenement = 0.0;
            for (Evenement event : evenements.values()) {
                double impactBase = event.getDegres() / 600.0;
                double facteurAleatoire = 0.8 + rand.nextDouble() * 0.4;
                double impact = impactBase * facteurAleatoire;
                if (event.getDegres() > 0) {
                    impact *= 1.2;
                }

                if (event.getCategorie().equals("Catastrophe") && entreprise.getSecteur().equals("Énergie")) {
                    impact *= 1.3;
                    volatiliteEvenement += 0.08;
                } else if (event.getCategorie().equals("Politique") && entreprise.getSecteur().equals("Finance")) {
                    impact *= 1.2;
                    volatiliteEvenement += 0.04;
                }
                if (event.getSecteur().equals(entreprise.getSecteur()) || event.getSecteur().equals("Macro")) {
                    muEvenement += impact * (event.getSecteur().equals("Macro") ? 0.6 : 1.0);
                    if (Math.abs(event.getDegres()) >= 3) {
                        volatiliteEvenement += 0.08;
                    }
                } else if (event.getSousSecteurCible() != null && event.getSousSecteurCible().equals(entreprise.getSousSecteur())) {
                    muEvenement += impact * 1.3;
                    volatiliteEvenement += 0.12;
                }
            }

            double muMomentum = entreprise.calculerMomentum();
            double mu = 0.25 * muSecteur + 0.35 * muEvenement + 0.15 * muMomentum + 0.25 * tendanceMacro;
            double volatilite = volatiliteBase + volatiliteEvenement * 0.8;
            double termeDrift = (mu - (volatilite * volatilite) / 2) * deltaT;

            double secteurEffect = secteurShock.getOrDefault(entreprise.getSecteur(), 0.0);
            double termeStochastique = (0.65 * volatilite * Math.sqrt(deltaT) * rand.nextGaussian() + 0.35 * secteurEffect) * 0.85;
            double prixMoyen = entreprise.getPrixMoyen();
            double meanReversion = 0.03 * Math.log(prixMoyen / prixInitial) * deltaT;

            double nouveauPrix = prixInitial * Math.exp(termeDrift + termeStochastique + meanReversion);

            double variationPourcentage = ((nouveauPrix - prixInitial) / prixInitial) * 100;
            if (Math.abs(variationPourcentage) > 8) {
                double facteurLissage = 8 / Math.abs(variationPourcentage);
                nouveauPrix = prixInitial * (1 + Math.signum(variationPourcentage) * 0.08 * facteurLissage);
            }

            entreprise.setPrixDepart(nouveauPrix);
            entreprise.setPourcentage(((nouveauPrix - prixInitial) / prixInitial) * 100);
            entreprise.updatePrixMoyen(nouveauPrix);

            double capitalInitial = entreprise.getCapital();
            double ratioPrix = nouveauPrix / prixMoyen;
            entreprise.setCapital(capitalInitial * ratioPrix);

            logger.trace("Entreprise mise à jour: " + entreprise.getNom() + ", Nouveau prix: " + nouveauPrix + ", Variation: " + entreprise.getPourcentage() + "%");
        }

        logger.info("Mise à jour des entreprises terminée");
        return entreprises;
    }

    private double calculerTendanceSecteur(String sousSecteur) {
        double sommeVariations = 0.0;
        int count = 0;
        for (Entreprise e : entreprises.values()) {
            if (e.getSousSecteur().equals(sousSecteur)) {
                sommeVariations += e.getPourcentage() / 100.0;
                count++;
            }
        }
        double tendance = count > 0 ? sommeVariations / count : 0.0;
        logger.debug("Tendance secteur (" + sousSecteur + "): " + tendance);
        return tendance;
    }

    public static Set<Integer> genererEnsembleUnique(int taille, int maxIndex) {
        if (taille > maxIndex) {
            throw new IllegalArgumentException("La taille demandée (" + taille + ") est supérieure au nombre d'éléments disponibles (" + maxIndex + ") !");
        }

        Set<Integer> nombres = new HashSet<>();
        Random rand = new Random();

        while (nombres.size() < taille) {
            int nombre = rand.nextInt(maxIndex);
            nombres.add(nombre);
        }

        logger.debug("Génération d'ensemble unique: " + nombres);
        return nombres;
    }

    public double calculerSpread(Entreprise entreprise) {
        double spread = entreprise.getPrixDepart() * (0.01 / Math.sqrt(entreprise.getVolumeMoyen() / 1000000));
        logger.trace("Spread calculé pour " + entreprise.getNom() + ": " + spread);
        return spread;
    }

    public HashMap<String, Entreprise> getEntreprises() {
        return entreprises;
    }

    public void setEntreprises(HashMap<String, Entreprise> entreprises) {
        this.entreprises = entreprises;
        logger.info("Entreprises mises à jour dans la bourse");
    }

    public Actualite getActualite() {
        return a;
    }

    public void ajouterEntreprise(Entreprise entreprise) {
        entreprises.put(entreprise.getID(), entreprise);
        logger.info("Nouvelle entreprise ajoutée: " + entreprise.getNom());
    }
}