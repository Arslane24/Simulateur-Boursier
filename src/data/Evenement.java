package data;

public class Evenement {
    private String id;
    private String nom;
    private int degres;
    private String secteur;
    private String sousSecteurCible;
    private int duree;
    private int vie;
    private String categorie;
    private String description;

    public Evenement(String id, String nom, int degres, String secteur, String sousSecteurCible, int duree, int vie, String categorie, String description) {
        this.id = id;
        this.nom = nom;
        this.degres = degres;
        this.secteur = secteur;
        this.sousSecteurCible = sousSecteurCible;
        this.duree = duree;
        this.vie = vie;
        this.categorie = categorie;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getDegres() {
        return degres;
    }

    public void setDegres(int degres) {
        this.degres = degres;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getSousSecteurCible() {
        return sousSecteurCible;
    }

    public void setSousSecteurCible(String sousSecteurCible) {
        this.sousSecteurCible = sousSecteurCible;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public int getVie() {
        return vie;
    }

    public void setVie(int vie) {
        this.vie = vie;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String afficherEvenement() {
        return "ID: " + id + "\n" +
               "Nom: " + nom + "\n" +
               "Degré: " + degres + "\n" +
               "Secteur: " + secteur + "\n" +
               "Sous-secteur: " + (sousSecteurCible != null ? sousSecteurCible : "N/A") + "\n" +
               "Catégorie: " + categorie + "\n" +
               "Durée: " + duree + "\n" +
               "Vie: " + vie + "\n" +
               "Description: " + description;
    }
}