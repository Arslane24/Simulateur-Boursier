package data;

public abstract class Actif {
	
	private double PrixActu ;
	private Entreprise Entreprise ;
	
	
	public Actif (double PrixActu , Entreprise Entreprise ) {
		this.PrixActu=PrixActu;
		this.Entreprise=Entreprise;
		
		
	}
	public double getPrixActu() {
		return PrixActu;
	}

	public void setPrixActu(double prixActu) {
		PrixActu = prixActu;
	}

	public Entreprise getEntreprise() {
		return Entreprise;
	}

	public void setEntreprise(Entreprise entreprise) {
		Entreprise = entreprise;
	}

	public abstract boolean isAction();
	
	
	

}
