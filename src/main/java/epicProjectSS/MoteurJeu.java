package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHero;
import classApi.EpicHeroesLeague;

public class MoteurJeu {

	RestAPI rest = new RestAPI();
	String idEquipe;
	String idPartieCurrent;
	String tour;
	String listePerso;

	String statutPartie;
	boolean accesPartie = false;

	Board board;

	boolean arretPartie = false;

	int nombresTours = 1;

	/* Personnage */
	EpicHeroesLeague joueur1;
	EpicHeroesLeague joueur2;

	EpicHero orc;
	EpicHero pretre;
	EpicHero garde;

	EpicHero orc_ennemi;
	EpicHero pretre_ennemi;
	EpicHero garde_ennemi;

	EpicHero persoEnnemiChoisi;

	EpicHero persoEquipeChoisi;

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
		
		/* idEquipe = rest.idEquipe("The%20Imps", "Rud3=W"); */

		int niveauBot = Integer.parseInt(nivBot);

		if (niveauBot > 0 && niveauBot < 13 || niveauBot > 29 && niveauBot < 33) {

			idPartieCurrent = rest.nouvellePartieBot(nivBot, idEquipe);

			while(idPartieCurrent.equals("NA") && !accesPartie) {
				System.out.println(
						"Erreur : Versus, jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");

				idPartieCurrent = rest.nouvellePartieBot(nivBot, idEquipe);

				if(!idPartieCurrent.equals("NA")) {
					accesPartie = true;
				}


			}

			System.out.println("***************************************************");
			System.out.println("*           Lancement Jeu Contre BOT de niveau: " + nivBot + "*");
			System.out.println("***************************************************");

			deroulementPartie(idPartieCurrent, idEquipe, "Contre Bots");


		} else {
			System.out.println("Error - Moteur Jeu - newPratice, le niveau du bot saisi n'est pas bon.");
		}

	}

	/* Combat contre équipe */
	public void newVersus() throws IOException {

		System.out.println("***********************************************");
		System.out.println("\n Lancement Jeu Contre ÉQUIPE ");
		System.out.println("***********************************************");

		idPartieCurrent = rest.idPartie(idEquipe);

		while (!idPartieCurrent.equals("NA")) {

			deroulementPartie(idPartieCurrent, idEquipe, "Contre Bots");

			/* Implémenter partie suivante */
			System.out.println("Vous allez débuter une nouvelle partie.");
			nextGame("versus");

		}

		if (idPartieCurrent.equals("NA")) {
			System.out.println(
					"Erreur : Versus - jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");
		}
		



	}

	public void nextGame(String typeCombat) throws IOException {
		
		if(rest.idPartie(idEquipe).equals("NA")) {
			System.out.println(
					"Erreur : Versus - jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");
		} else {
			if (typeCombat.equals("versus")) {
				newVersus();
			} else if (typeCombat.equals("pratice")) {
				/*
				 * Appel fonction Pratice, mais trouver un moyen pour a faire appel si le joueur
				 * veut faire un combat contre un bot d'un autre niveau
				 */
			}

		}
	}
	
	public void deroulementPartie(String idPartieCurrent, String idEquipe, String TypePartie) throws IOException {

		System.out.println("\n***************************************************");
		System.out.println("Type de Partie : " + TypePartie);
		System.out.println("ID Equipe : " + idEquipe);
		System.out.println("ID Partie : " + idPartieCurrent);
		System.out.println("***************************************************\n\n\n");

		try {

			/*
			 * Récupération du Statut de la Partie board =
			 * rest.plateauJeuEquipe(idPartieCurrent, idEquipe); nombreToursRestant =
			 * board.getNbrTurnsLeft();
			 */

			int nbTourOfficiel = 1;

			board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

			while (!arretPartie && nbTourOfficiel < 54) {

				boolean changementStatut = false;

				board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

				if (board.getPlayerBoards().get(0).getPlayerName().equals("The Imps")) {
					joueur1 = board.getPlayerBoards().get(0);
					joueur2 = board.getPlayerBoards().get(1);

				} else {
					joueur1 = board.getPlayerBoards().get(1);
					joueur2 = board.getPlayerBoards().get(0);
				}

				statutPartie = rest.tourEquipe(idPartieCurrent, idEquipe);

				/* Chargement des changements du plateau de jeu */

				System.out.println("\n\n*******************************************");
				System.out.println("Affichage Information Tour : " + nbTourOfficiel);

				if (statutPartie.equals("CANTPLAY")) {
					System.out.println("EN ATTENTE - NE PAS JOUER");

					while (statutPartie.equals("CANTPLAY") && changementStatut == false) {
						serverSleep();
						statutPartie = rest.tourEquipe(idPartieCurrent, idEquipe);
						if (statutPartie.equals("CANPLAY")) {
							changementStatut = true;
						}
					}
				}

				if (statutPartie.equals("CANPLAY")) {
					nbTourOfficiel++;
					System.out.println("OK - ON PEUT JOUER");
					canPlay(board);
					System.out.println("*******************************************");
				} else {
					arretPartie = true;
				}
				nombresTours++;
			}

			if (statutPartie.equals("VICTORY")) {
				affichageResultat("VOUS AVEZ GAGNÉ.");
			} else if (statutPartie.equals("DEFEAT")) {
				affichageResultat("VOUS AVEZ PERDU.");
			}else if (statutPartie.equals("DRAW")) {
				affichageResultat("MATCH NUL.");
			}else if (statutPartie.equals("CANCELLED")) {
				affichageResultat("MATCH ANNULÉ.");
			}

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur - Moteur Jeu - Déroulement Partie");
		}

	}

	public void affichageResultat(String resultat) {
		System.out.println("+++++++++++++++++++ RESULTAT ++++++++++++++++++++");
		System.out.println("               " + resultat);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");

	}

	public void canPlay(Board board) throws IOException {

		if (nombresTours <= 4) {
			choixPersonnage();
		} else if (nombresTours >= 4) {
			play();
		}
		
	}

	public void choixPersonnage() throws IOException {

		if (nombresTours == 1) {

			rest.actionJeu(idPartieCurrent, idEquipe, "PRIEST");
			listePerso = "Joueur 1 - Notre Equipe à choisi : PRETRE\n";

		} else if (nombresTours == 2) {

			/* Va chercher le personnage choisi par l'équipe 2 */
			persoEnnemiChoisi = joueur2.getFighters().get(0);

			if (persoEnnemiChoisi.getFighterClass().contentEquals("GUARD")) {
				garde_ennemi = persoEnnemiChoisi;
				rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");

				listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : GARDE\n";

			} else if (persoEnnemiChoisi.getFighterClass().contentEquals("ORC")) {
				orc_ennemi = persoEnnemiChoisi;
				rest.actionJeu(idPartieCurrent, idEquipe, "ORC");

				listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : ORC\n";

			} else if (persoEnnemiChoisi.getFighterClass().contentEquals("PRIEST")) {
				pretre_ennemi = persoEnnemiChoisi;
			}


		} else if (nombresTours == 3) {

			/* Va chercher le personnage que l'on à choisi au 2ème tour */
			persoEquipeChoisi = joueur1.getFighters().get(1);
			/* Va chercher le personnage choisi par l'équipe 2 */
			persoEnnemiChoisi = joueur2.getFighters().get(1);

			System.out.println("DEBUG 2");

			/* Choix du personnage en fonction de leur tirage à partir du 2ème tour */

			if (persoEnnemiChoisi.getFighterClass().contentEquals("GUARD")) {

				if (persoEquipeChoisi.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : GARDE\n";
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : ORC\n";
				}

				garde_ennemi = persoEnnemiChoisi;

			} else if (persoEnnemiChoisi.getFighterClass().contentEquals("ORC")) {

				if (persoEquipeChoisi.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : GARDE\n";
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : ORC\n";
				}

				orc_ennemi = persoEnnemiChoisi;

			} else if (persoEnnemiChoisi.getFighterClass().contentEquals("PRIEST")) {
				if (persoEquipeChoisi.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : GARDE\n";
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
					listePerso = listePerso + "Joueur 1 - Notre Equipe à choisi : ORC\n";
				}

				pretre_ennemi = persoEnnemiChoisi;

			}

		}

		if (nombresTours == 4) {
			System.out.println(listePerso);
			System.out.println("Joueur 2 - Equipe Ennemi à choisi : " + joueur2.getFighters().get(0).getFighterClass());
			System.out.println("Joueur 2 - Equipe Ennemi à choisi : " + joueur2.getFighters().get(1).getFighterClass());
			System.out.println("Joueur 2 - Equipe Ennemi à choisi : " + joueur2.getFighters().get(2).getFighterClass());
		}

	}

	public void play() throws IOException {

		rest.actionJeu(idPartieCurrent, idEquipe, "A1,ATTACK,E1$A2,ATTACK,E2$A3,ATTACK,E3");
		System.out.println("DEBUG 10");

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
