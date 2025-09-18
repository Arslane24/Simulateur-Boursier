package data;

import java.util.ArrayList;

public class Portfeuille {
	
	private ArrayList<Actif> Actifs;
	private double SoldeActuel;
	private double SoldeInvesti;
	private double Profit;
	private ArrayList<Actif> Historique;
	
	public Portfeuille(ArrayList<Actif> Actifs,double SoldeActuel,double SoldeInvesti,double Profit,ArrayList<Actif> Historique) {
		
		this.Actifs=Actifs;
		this.SoldeActuel=SoldeActuel;
		this.SoldeInvesti=SoldeInvesti;
		this.Profit=Profit;
		this.Historique=Historique;
		
	}
	public ArrayList<Actif> getActifs() {
		return Actifs;
	}

	public void setActifs(ArrayList<Actif> actifs) {
		Actifs = actifs;
	}

	public double getSoldeActuel() {
		return SoldeActuel;
	}

	public void setSoldeActuel(double soldeActuel) {
		SoldeActuel = soldeActuel;
	}

	public double getSoldeInvesti() {
		return SoldeInvesti;
	}

	public void setSoldeInvesti(double soldeInvesti) {
		SoldeInvesti = soldeInvesti;
	}

	public double getProfit() {
		return Profit;
	}

	public void setProfit(double profit) {
		Profit = profit;
	}

	public ArrayList<Actif> getHistorique() {
		return Historique;
	}

	public void setHistorique(ArrayList<Actif> historique) {
		Historique = historique;
	}
	

}
