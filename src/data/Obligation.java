package data;

public class Obligation extends Actif {
    private double TauxInteret;
    private int echeance;

    public Obligation(double TauxInteret, int echeance, Entreprise e, double prix) {
        super(prix, e);
        this.TauxInteret = TauxInteret;
        this.echeance = echeance;
    }

    public double getTauxInteret() {
        return TauxInteret;
    }

    public void setTauxInteret(double tauxInteret) {
        TauxInteret = tauxInteret;
    }

    public int getEcheance() {
        return echeance;
    }

    public void setEcheance(int echeance) {
        this.echeance = echeance;
    }

    
    public boolean isAction() {
        return false;  
    }
}