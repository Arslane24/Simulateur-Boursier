package data;

public class Utilisateur {
	private String Nom;
	private String ID;
	
	public Utilisateur(String Nom,String ID) {
		this.Nom=Nom;
		this.ID=ID;
	}
	public String getNom() {
		return Nom;
	}

	public void setNom(String nom) {
		Nom = nom;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

}