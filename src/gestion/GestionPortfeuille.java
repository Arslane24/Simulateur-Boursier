package gestion;

import data.Portfeuille;
import data.Actif;
import data.Action;
import data.Obligation;
import data.Entreprise;
import data.Transaction;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GestionPortfeuille {
    private static Logger logger = LoggerUtility.getLogger(GestionPortfeuille.class, "html");
    private Portfeuille portfeuille;
    private Map<String, Double> investissementsParSecteur;
    private ArrayList<Transaction> historiqueTransactions;

    public GestionPortfeuille() {
        this.portfeuille = new Portfeuille(new ArrayList<Actif>(), 50000, 0, 0, new ArrayList<Actif>());
        this.investissementsParSecteur = new HashMap<>();
        this.historiqueTransactions = new ArrayList<>();
        logger.info("Initialisation du portefeuille avec un solde initial de 50000");
    }

    public GestionPortfeuille(Portfeuille p) {
        this.portfeuille = p;
        this.investissementsParSecteur = new HashMap<>();
        this.historiqueTransactions = new ArrayList<>();
        logger.info("Initialisation du portefeuille avec un portefeuille existant");
    }

    public Map<String, Double> getInvestissementsParSecteur() {
        return investissementsParSecteur;
    }

    public ArrayList<Transaction> getHistoriqueTransactions() {
        return historiqueTransactions;
    }

    public void acheterAction(Entreprise entreprise) {
        logger.debug("Tentative d'achat d'actions pour " + entreprise.getNom());
        String input = JOptionPane.showInputDialog(null, "Combien d'actions de " + entreprise.getNom() + " voulez-vous acheter ?", "Acheter des actions", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                int quantite = Integer.parseInt(input);
                Action action = new Action(quantite, entreprise, entreprise.getPrixDepart());
                double total = action.getPrixActu() * quantite;
                if (SoldeSuffisant(total)) {
                    Action actionExistante = recherche(entreprise.getID());
                    if (actionExistante != null) {
                        actionExistante.setQuantite(actionExistante.getQuantite() + quantite);
                        logger.debug("Ajout de " + quantite + " actions à une action existante pour " + entreprise.getNom());
                    } else {
                        action.setQuantite(quantite);
                        portfeuille.getActifs().add(action);
                        logger.debug("Nouvelle action ajoutée pour " + entreprise.getNom());
                    }
                    portfeuille.setSoldeActuel(portfeuille.getSoldeActuel() - total);
                    portfeuille.setSoldeInvesti(portfeuille.getSoldeInvesti() + total);
                    enregistrerAchat(action, quantite, total);
                    String secteur = entreprise.getSecteur();
                    investissementsParSecteur.merge(secteur, total, Double::sum);
                    Transaction transaction = new Transaction("Action", "Achat", entreprise, quantite, action.getPrixActu(), total, LocalDateTime.now());
                    historiqueTransactions.add(transaction);
                    logger.info("Achat de " + quantite + " actions de " + entreprise.getNom() + " pour un total de " + total);
                    JOptionPane.showMessageDialog(null, quantite + " actions de " + entreprise.getNom() + " à " + action.getPrixActu() + "$ (Total : " + total + "$) ont été ajoutées à votre portefeuille.");
                } else {
                    logger.warn("Solde insuffisant pour acheter " + quantite + " actions de " + entreprise.getNom());
                    JOptionPane.showMessageDialog(null, "Solde insuffisant !");
                }
            } catch (NumberFormatException ex) {
                logger.error("Entrée invalide pour la quantité d'actions", ex);
                JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void acheterObligation(Obligation obligation) {
        double total = obligation.getPrixActu();
        Entreprise e = obligation.getEntreprise();
        logger.debug("Tentative d'achat d'une obligation pour " + e.getNom());
        if (SoldeSuffisant(total)) {
            portfeuille.getActifs().add(obligation);
            portfeuille.setSoldeActuel(portfeuille.getSoldeActuel() - total);
            portfeuille.setSoldeInvesti(portfeuille.getSoldeInvesti() + total);
            enregistrerAchat(obligation, 1, total);
            String secteur = e.getSecteur();
            investissementsParSecteur.merge(secteur, total, Double::sum);
            Transaction transaction = new Transaction("Obligation", "Achat", e, 1, obligation.getPrixActu(), total, LocalDateTime.now());
            historiqueTransactions.add(transaction);
            logger.info("Achat d'une obligation de " + e.getNom() + " pour " + total);
            JOptionPane.showMessageDialog(null, "Une obligation de " + e.getNom() + " à " + obligation.getPrixActu() + "$ (Taux d'intérêt : " + obligation.getTauxInteret() + "%, Échéance : " + obligation.getEcheance() + " ans) a été ajoutée à votre portefeuille.");
        } else {
            logger.warn("Solde insuffisant pour acheter une obligation de " + e.getNom());
            JOptionPane.showMessageDialog(null, "Solde insuffisant !");
        }
    }

    public void vendreAction(Entreprise entreprise) {
        Action actionExistante = recherche(entreprise.getID());
        if (actionExistante != null) {
            double prixActuel = entreprise.getPrixDepart();
            logger.debug("Tentative de vente d'actions pour " + entreprise.getNom());
            String[] options = {"Vendre tout", "Vendre une partie"};
            int choix = JOptionPane.showOptionDialog(null,"Que voulez-vous faire avec vos " + actionExistante.getQuantite() +" actions de " + entreprise.getNom() + " (Prix actuel: " + prixActuel + "$) ?","Vendre des actions",JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            int quantite;
            if (choix == 0) {
                quantite = actionExistante.getQuantite();
            } else if (choix == 1) {
                String input = JOptionPane.showInputDialog(null,"Combien d'actions de " + entreprise.getNom() + " voulez-vous vendre ?","Vendre des actions",JOptionPane.QUESTION_MESSAGE);     
                if (input == null || input.isEmpty()) {
                    logger.debug("Vente annulée pour " + entreprise.getNom());
                    return;
                }
                try {
                    quantite = Integer.parseInt(input);
                    if (quantite <= 0 || quantite > actionExistante.getQuantite()) {
                        logger.warn("Quantité invalide pour la vente d'actions de " + entreprise.getNom());
                        JOptionPane.showMessageDialog(null, "Quantité invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    logger.error("Entrée invalide pour la quantité à vendre", ex);
                    JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre valide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                logger.debug("Vente annulée pour " + entreprise.getNom());
                return;
            }

            double prixVente = prixActuel * quantite;
            portfeuille.setSoldeActuel(portfeuille.getSoldeActuel() + prixVente);
            actionExistante.setQuantite(actionExistante.getQuantite() - quantite);
            String secteur = entreprise.getSecteur();
            double montantDesinvesti = prixVente;
            investissementsParSecteur.merge(secteur, -montantDesinvesti, (oldValue, newValue) -> Math.max(0, oldValue + newValue));
            Transaction transaction = new Transaction("Action", "Vente", entreprise, quantite, prixActuel, prixVente, LocalDateTime.now());
            historiqueTransactions.add(transaction);
            logger.info("Vente de " + quantite + " actions de " + entreprise.getNom() + " pour " + prixVente);
            if (actionExistante.getQuantite() == 0) {
                portfeuille.getActifs().remove(actionExistante);
                logger.debug("Action supprimée du portefeuille: " + entreprise.getNom());
            }
            JOptionPane.showMessageDialog(null,"Vous avez vendu " + quantite + " actions de " + entreprise.getNom() +" au prix actuel de " + prixActuel + "$ pour un total de " + prixVente + "$.");   
        } else {
            logger.warn("Aucune action trouvée pour " + entreprise.getNom());
            JOptionPane.showMessageDialog(null,"Vous ne possédez pas d'actions de cette entreprise.","Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void VendreObligation(Obligation obligation) {
        ArrayList<Actif> al = portfeuille.getActifs();
        double prixVente = obligation.getPrixActu();
        Entreprise e = obligation.getEntreprise();
        boolean obligationTrouvee = false;
        logger.debug("Tentative de vente d'une obligation pour " + e.getNom());
        for (Actif actif : al) {
            if (!actif.isAction()) {
                Obligation o = (Obligation) actif;
                if (o.getEntreprise().getID().equals(obligation.getEntreprise().getID()) &&
                    o.getPrixActu() == obligation.getPrixActu()) {
                    portfeuille.setSoldeActuel(portfeuille.getSoldeActuel() + prixVente);
                    al.remove(o);
                    obligationTrouvee = true;
                    String secteur = e.getSecteur();
                    investissementsParSecteur.merge(secteur, -prixVente, (oldValue, newValue) -> Math.max(0, oldValue + newValue));
                    Transaction transaction = new Transaction("Obligation", "Vente", e, 1, prixVente, prixVente, LocalDateTime.now());
                    historiqueTransactions.add(transaction);
                    logger.info("Vente d'une obligation de " + e.getNom() + " pour " + prixVente);
                    JOptionPane.showMessageDialog(null, "Vous avez vendu l'obligation de " + e.getNom() + " pour un total de " + prixVente + "$.");
                    break;
                }
            }
        }

        if (!obligationTrouvee) {
            logger.warn("Aucune obligation trouvée pour " + e.getNom());
            JOptionPane.showMessageDialog(null, "Vous ne possédez pas cette obligation.");
        }
    }

    public boolean SoldeSuffisant(double prix) {
        boolean suffisant = portfeuille.getSoldeActuel() >= prix;
        logger.trace("Vérification du solde: " + portfeuille.getSoldeActuel() + " >= " + prix + " -> " + suffisant);
        return suffisant;
    }

    public Action recherche(String IDentreprise) {
        ArrayList<Actif> al = portfeuille.getActifs();
        for (Iterator<Actif> it = al.iterator(); it.hasNext();) {
            Actif a = it.next();
            Entreprise e = a.getEntreprise();
            if (a.isAction() && e.getID().equals(IDentreprise)) {
                logger.debug("Action trouvée pour l'entreprise ID: " + IDentreprise);
                return (Action) a;
            }
        }
        logger.debug("Aucune action trouvée pour l'entreprise ID: " + IDentreprise);
        return null;
    }

    public void enregistrerAchat(Actif a, int quantite, double montantTotal) {
        Entreprise e = a.getEntreprise();
        portfeuille.getHistorique().add(a);
        logger.debug("Enregistrement de l'achat: " + quantite + " unité(s) de " + e.getNom() + " pour " + montantTotal);
    }

    public String infoSoldeActuel() {
        String solde = "Solde actuel : " + String.format("%.2f", portfeuille.getSoldeActuel()) + "$";
        logger.trace("Info solde actuel: " + solde);
        return solde;
    }

    public String infoSoldeInvesti() {
        String investi = "Solde investi : " + String.format("%.2f", portfeuille.getSoldeInvesti()) + "$";
        logger.trace("Info solde investi: " + investi);
        return investi;
    }

    public String getActifsDetenus() {
        StringBuffer sb = new StringBuffer();
        ArrayList<Actif> m = portfeuille.getActifs();
        sb.append("Actifs détenus :\n");
        for (Actif actif : m) {
            String typeActif;
            int quantite;
            if (actif.isAction()) {
                typeActif = "Action";
                Action action = (Action) actif;
                quantite = action.getQuantite();
            } else {
                typeActif = "Obligation";
                quantite = 1;
            }

            sb.append("- ").append(actif.getEntreprise().getNom()).append(" : ").append(typeActif).append(", Quantité : ").append(quantite).append(", Prix unitaire : ").append(actif.getPrixActu()).append("$\n");
        }
        logger.trace("Liste des actifs détenus: " + sb.toString());
        return sb.toString();
    }

    public String getHistoriqueTransactionsAsString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Historique des transactions :\n");
        for (Transaction transaction : historiqueTransactions) {
            String typeActif = transaction.getType();
            String action = transaction.getAction();
            Entreprise e = transaction.getEntreprise();
            int quantite = transaction.getQuantite();
            double prixUnitaire = transaction.getPrixUnitaire();
            double total = transaction.getTotal();
            String date = transaction.getFormattedDate();
            sb.append("- ").append(e.getNom()).append(" : ").append(typeActif).append(", ").append(action).append(", Quantité : ").append(quantite).append(", Prix unitaire : ").append(prixUnitaire).append("$, Total : ").append(total).append("$, Date : ").append(date).append("\n");
        }
        logger.trace("Historique des transactions: " + sb.toString());
        return sb.toString();
    }

    public String calculerBenefice() {
        double valeurActuelleDesActifs = 0.0;

        for (Actif actif : portfeuille.getActifs()) {
            if (actif.isAction()) {
                Action action = (Action) actif;
                valeurActuelleDesActifs += action.getQuantite() * action.getEntreprise().getPrixDepart();
            } else {
                Obligation obligation = (Obligation) actif;
                valeurActuelleDesActifs += obligation.getPrixActu();
            }
        }

        double soldeInitial = 50000.0;
        double benefice = (valeurActuelleDesActifs + portfeuille.getSoldeActuel()) - soldeInitial;
        String resultat = benefice >= 0 ? "Bénéfice : +" + String.format("%.2f", benefice) + "$" : "Perte : " + String.format("%.2f", benefice) + "$";
        logger.info("Calcul du bénéfice: " + resultat);
        return resultat;
    }

    public Portfeuille getPortfeuille() {
        return portfeuille;
    }

    public double getSolde() {
        return portfeuille.getSoldeActuel();
    }

    public double getSoldeInvesti() {
        return portfeuille.getSoldeInvesti();
    }

    public double calculerBeneficeValue() {
        double valeurActuelleDesActifs = 0.0;

        for (Actif actif : portfeuille.getActifs()) {
            if (actif.isAction()) {
                Action action = (Action) actif;
                valeurActuelleDesActifs += action.getQuantite() * action.getEntreprise().getPrixDepart();
            } else {
                valeurActuelleDesActifs += actif.getPrixActu();
            }
        }

        double soldeInitial = 50000.0;
        double benefice = (valeurActuelleDesActifs + portfeuille.getSoldeActuel()) - soldeInitial;
        logger.trace("Valeur du bénéfice calculée: " + benefice);
        return benefice;
    }
}