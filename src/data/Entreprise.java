package data;

public class Entreprise {
    private String nom;
    private String ID;
    private double prixDepart;
    private String secteur;
    private String sousSecteur;
    private String devise;
    private String bourse;
    private int volumeMoyen;
    private double volatilite;
    private double pourcentage;
    private double prixMoyen;
    private int toursPourMoyen;
    private double capital; 

    public Entreprise(String nom, String ID, double prixDepart, String secteur, String sousSecteur, String devise, String bourse, int volumeMoyen, double volatilite, double pourcentage, double prixMoyen, double capital) {
        this.nom = nom;
        this.ID = ID;
        this.prixDepart = prixDepart;
        this.secteur = secteur;
        this.sousSecteur = sousSecteur;
        this.devise = devise;
        this.bourse = bourse;
        this.volumeMoyen = volumeMoyen;
        this.volatilite = volatilite;
        this.pourcentage = pourcentage;
        this.prixMoyen = prixMoyen;
        this.toursPourMoyen = 1;
        this.capital = capital;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getPrixMoyen() {
        return prixMoyen;
    }

    public void updatePrixMoyen(double nouveauPrix) {
        prixMoyen = (prixMoyen * toursPourMoyen + nouveauPrix) / (toursPourMoyen + 1);
        toursPourMoyen++;
    }

    
    public String getNom() {
    	return nom; 
    	}
    public void setNom(String nom) { 
    	this.nom = nom; 
    	}
    public String getID() { 
    	return ID; 
    	}
    public void setID(String ID) {
    	this.ID = ID; 
    	}
    public double getPrixDepart() { 
    	return prixDepart; 
    	}
    public void setPrixDepart(double prixDepart) { 
    	this.prixDepart = prixDepart; 
    	}
    public String getSecteur() {
    	return secteur; 
    	}
    public void setSecteur(String secteur) { 
    	this.secteur = secteur;
    	}
    public String getSousSecteur() {
    	return sousSecteur; 
    	}
    public void setSousSecteur(String sousSecteur) { 
    	this.sousSecteur = sousSecteur; 
    	}
    public String getDevise() { 
    	return devise; 
    	}
    public void setDevise(String devise) { 
    	this.devise = devise; 
    	}
    public String getBourse() {
    	return bourse;
    	}
    public void setBourse(String bourse) {
    	this.bourse = bourse; 
    	}
    public int getVolumeMoyen() {
    	return volumeMoyen; 
    	}
    public void setVolumeMoyen(int volumeMoyen) {
    	this.volumeMoyen = volumeMoyen; 
    	}
    public double getVolatilite() {
    	return volatilite; 
    	}
    public void setVolatilite(double volatilite) { 
    	this.volatilite = volatilite; 
    	}
    public double getPourcentage() {
    	return pourcentage; 
    	}
    public void setPourcentage(double pourcentage) { 
    	this.pourcentage = pourcentage; 
    	}
    public double calculerMomentum() {
    	return pourcentage / 100.0; 
    	}
}