package jeu;

public class Personnage {

	int pointsVie;
	int pointsAction = 1;
	String nomPersonnage;

	public Personnage(String nom, int ptsVie) {
		nomPersonnage = nom;
		pointsVie = ptsVie;
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

}
