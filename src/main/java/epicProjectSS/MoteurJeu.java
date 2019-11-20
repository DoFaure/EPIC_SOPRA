package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHeroesLeague;

public class MoteurJeu {

	RestAPI rest = new RestAPI();
	String idEquipe;
	String idPartieCurrent;
	String tour;

	public MoteurJeu() {
		try {
			idEquipe = rest.idEquipe("The%20Imps", "Rud3=W");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur - Moteur Jeu - S'en doute l'IdEquipe non présent");
		}
	}

	/* Combat contre bots */
	public void newPratice(String nivBot) throws IOException {
		
		int niveauBot = Integer.parseInt(nivBot);

		if (niveauBot > 0 && niveauBot < 13 || niveauBot > 29 && niveauBot < 33) {

			idPartieCurrent = newPartieBots(nivBot, idEquipe);

			if (idPartieCurrent.equals("NA")) {
				System.out.println(
						"Erreur : Versus, jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");
			} else {

				deroulementPartie(idPartieCurrent, idEquipe, "Contre Bots");

			}

		} else {
			System.out.println("Error - Moteur Jeu - newPratice, le niveau du bot saisi n'est pas bon.");
		}

	}

	/* Combat contre équipe */
	public void newVersus() throws IOException {

		idPartieCurrent = rest.idPartie(idEquipe);

		if (idPartieCurrent.equals("NA")) {
			System.out.println(
					"Erreur : Versus, jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");
		} else {

			deroulementPartie(idPartieCurrent, idEquipe, "Contre Bots");
		}

	}

	public void deroulementPartie(String idPartieCurrent, String idEquipe, String TypePartie) {

		System.out.println(TypePartie);
		String tour = "";

		try {

			Board board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);
			EpicHeroesLeague orc = board.getPlayerBoards().get(0);
			EpicHeroesLeague pretre = board.getPlayerBoards().get(1);
			EpicHeroesLeague garde = board.getPlayerBoards().get(2);

			while (rest.tourEquipe(idPartieCurrent, idEquipe).equals("CANTPLAY") || rest.tourEquipe(idPartieCurrent, idEquipe).equals("CANTPLAY")) {

				tour = rest.tourEquipe(idPartieCurrent, idEquipe);
				
				if (tour.equals("CANTPLAY")) {
					System.out.println("Déroulement Partie - On ne peut pas jouer");
					serverSleep();

				} else if (tour.equals("CANPLAY")) {
					System.out.println("Déroulement Partie - On jouer");



				}

			}
			
			if (tour.equals("VICTORY")) {
				System.out.println("Vous avez GAGNÉ.");

			} else if (tour.equals("DEFEAT")) {
				System.out.println("Vous avez PERDU.");

			} else if (tour.equals("DRAW")) {
				System.out.println("Match NUL.");

			} else if (tour.equals("CANCELLED")) {
				System.out.println("La Partie est ANNULÉ");
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur - Moteur Jeu - Déroulement Partie");
		}

	}

	public String newPartieBots(String niveauBot, String idPartie) {
		try {
			return rest.nouvellePartieBot(niveauBot, idPartie);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur - Moteur Jeu - Méthode newPartieBots");
			return null;
		}
	}


	public void serverSleep()
	{
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur time serveur");
		}
	}


}
