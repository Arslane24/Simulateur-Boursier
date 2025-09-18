package data;

public class Action extends Actif {
    private int quantite;

    public Action(int quantite, Entreprise e, double prix) {
        super(prix, e);
        this.quantite = quantite;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public boolean isAction() {
        return true;  
    }
}