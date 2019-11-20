package jeu;

public class Personnage {

	String nomPersonnage;

	int maxPA = 4;
	int minPA = 0;
	int pointsAction;

	int maxPV;
	int minPV = 0;
	int pointsVie;


	public Personnage() {

	}


	public int getPointsVie() {
		return pointsVie;
	}

	public void setPointsVie(int pointsVie) {
		this.pointsVie = pointsVie;
	}

	public int getPointsAction() {
		return pointsAction;
	}

	public void setPointsAction(int pointsAction) {
		this.pointsAction = pointsAction;
	}

	public String getNomPersonnage() {
		return nomPersonnage;
	}

	public void setNomPersonnage(String nomPersonnage) {
		this.nomPersonnage = nomPersonnage;
	}

	public int getMaxPV() {
		return maxPV;
	}

	public void setMaxPV(int maxPV) {
		this.maxPV = maxPV;
	}
	
	public void attaqueDeBase() {
		
	}

}
