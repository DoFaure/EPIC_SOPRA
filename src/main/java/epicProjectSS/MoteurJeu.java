package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHero;
import classApi.EpicHeroesLeague;

public class MoteurJeu {

	RestAPI rest = new RestAPI();
	Board board;

	boolean fin = false, finInitialisation = false, noEffectsEnnemies = false, paladinAjoue = false;

	/*
	 * EpicHeroesLeague player1 = null, player2 = null;
	 * 
	 * EpicHero fighter1 = null, fighter2 = null, fighter3 = null, enemyFighter1 =
	 * null, enemyFighter2 = null, enemyFighter3 = null; EpicHero scaredEnemy =
	 * null, currentFighter = null, needHealFighter = null, lowerLifeFighter = null,
	 * targuetEnemy = null; EpicHero guardEnemy = null, scaredAlly = null, burntAlly
	 * = null, healAlly = null, healEnemy = null, archerEnemy = null, chamanEnemy =
	 * null;
	 * 
	 */

	String idEquipe, idPartieCurrent;

	String listePerso;
	String statutPartie;

	boolean arretPartie = false;
	boolean accesPartie = false;
	boolean changementStatut = false;

	int nombresTours = 1, b, lifeDif;
	int nbTourOfficiel = 1;

	EpicHeroesLeague joueur1, joueur2;

	/* Personnage */

	EpicHero orc, pretre, garde;

	EpicHero orc_ennemi, pretre_ennemi, garde_ennemi;

	EpicHero persoEnnemiChoisi1 = null, persoEnnemiChoisi2 = null, persoEnnemiChoisi3 = null;

	EpicHero persoEquipeChoisi1 = null, persoEquipeChoisi2 = null, persoEquipeChoisi3 = null;

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

			while (idPartieCurrent.equals("NA") && !accesPartie) {
				System.out.println(
						"Erreur : Versus, jouer contre une équipe - Il n'y'a pas de partie ouverte pour le moment.");

				idPartieCurrent = rest.nouvellePartieBot(nivBot, idEquipe);

				if (!idPartieCurrent.equals("NA")) {
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

		if (rest.idPartie(idEquipe).equals("NA")) {
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

			/* Choix des personnages */
			while (nbTourOfficiel <= 3) {

				System.out.println("\n\n*******************************************");
				System.out.println("Affichage Information Tour : " + nbTourOfficiel);

				changementStatut = false;
				board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);
				joueur1 = board.getPlayerBoards().get(0);
				joueur2 = board.getPlayerBoards().get(1);
				statutPartie = rest.tourEquipe(idPartieCurrent, idEquipe);

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
					System.out.println("OK - ON PEUT JOUER");
					choixPersonnage();
					System.out.println("Choix Personnage " + nbTourOfficiel);
					nbTourOfficiel++;
				}

			}

			while (!arretPartie && nbTourOfficiel < 55) {

				changementStatut = false;

				board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

				joueur1 = board.getPlayerBoards().get(0);
				joueur2 = board.getPlayerBoards().get(1);

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

					System.out.println("OK - ON PEUT JOUER");

					if (nbTourOfficiel == 4) {
						canPlay();
					}

					play();

					nbTourOfficiel++;

					System.out.println("*******************************************");

				} else {
					arretPartie = true;
				}

			}

			if (statutPartie.equals("VICTORY")) {
				affichageResultat("VOUS AVEZ GAGNÉ.");
			} else if (statutPartie.equals("DEFEAT")) {
				affichageResultat("VOUS AVEZ PERDU.");
			} else if (statutPartie.equals("DRAW")) {
				affichageResultat("MATCH NUL.");
			} else if (statutPartie.equals("CANCELLED")) {
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

	public void canPlay() throws IOException {

		board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

		joueur1 = board.getPlayerBoards().get(0);
		joueur2 = board.getPlayerBoards().get(1);

		System.out.println("\n______________________________________");
		System.out.println("***** EQUIPE 1 *****");

		persoEquipeChoisi1 = joueur1.getFighters().get(0);
		persoEquipeChoisi2 = joueur1.getFighters().get(1);
		persoEquipeChoisi3 = joueur1.getFighters().get(2);

		System.out.println(persoEquipeChoisi1.getFighterClass());
		System.out.println(persoEquipeChoisi2.getFighterClass());
		System.out.println(persoEquipeChoisi3.getFighterClass());

		System.out.println("\n***** EQUIPE 2 *****");

		persoEnnemiChoisi1 = joueur2.getFighters().get(0);
		persoEnnemiChoisi2 = joueur2.getFighters().get(1);
		persoEnnemiChoisi3 = joueur2.getFighters().get(2);

		System.out.println(persoEnnemiChoisi1.getFighterClass());
		System.out.println(persoEnnemiChoisi2.getFighterClass());
		System.out.println(persoEnnemiChoisi3.getFighterClass());
		System.out.println("______________________________________\n");

		/* Partie Notre Equipe assimilié des get(x) à un personnage */
		if (persoEquipeChoisi1.getFighterClass().equals("ORC")) {
			orc = persoEquipeChoisi1;
		} else if (persoEnnemiChoisi1.getFighterClass().equals("GUARD")) {
			garde = persoEquipeChoisi1;
		} else if (persoEnnemiChoisi1.getFighterClass().equals("PRIEST")) {
			pretre = persoEquipeChoisi1;
		}

		if (persoEquipeChoisi2.getFighterClass().equals("ORC")) {
			orc = persoEquipeChoisi2;
		} else if (persoEnnemiChoisi2.getFighterClass().equals("GUARD")) {
			garde = persoEquipeChoisi2;
		} else if (persoEnnemiChoisi2.getFighterClass().equals("PRIEST")) {
			pretre = persoEquipeChoisi2;
		}

		if (persoEquipeChoisi3.getFighterClass().equals("ORC")) {
			orc = persoEquipeChoisi3;
		} else if (persoEnnemiChoisi3.getFighterClass().equals("GUARD")) {
			garde = persoEquipeChoisi3;
		} else if (persoEnnemiChoisi3.getFighterClass().equals("PRIEST")) {
			pretre = persoEquipeChoisi3;
		}

		/* Partie Equipe Ennemi assimilié des get(x) à un personnage */
		if (persoEnnemiChoisi1.getFighterClass().equals("ORC")) {
			orc_ennemi = persoEnnemiChoisi1;
		} else if (persoEnnemiChoisi1.getFighterClass().equals("GUARD")) {
			garde_ennemi = persoEnnemiChoisi1;
		} else if (persoEnnemiChoisi1.getFighterClass().equals("PRIEST")) {
			pretre_ennemi = persoEnnemiChoisi1;
		}

		if (persoEnnemiChoisi2.getFighterClass().equals("ORC")) {
			orc_ennemi = persoEnnemiChoisi2;
		} else if (persoEnnemiChoisi2.getFighterClass().equals("GUARD")) {
			garde_ennemi = persoEnnemiChoisi2;
		} else if (persoEnnemiChoisi2.getFighterClass().equals("PRIEST")) {
			pretre_ennemi = persoEnnemiChoisi2;
		}

		if (persoEnnemiChoisi3.getFighterClass().equals("ORC")) {
			orc_ennemi = persoEnnemiChoisi3;
		} else if (persoEnnemiChoisi3.getFighterClass().equals("GUARD")) {
			garde_ennemi = persoEnnemiChoisi3;
		} else if (persoEnnemiChoisi3.getFighterClass().equals("PRIEST")) {
			pretre_ennemi = persoEnnemiChoisi3;
		}

	}

	public void choixPersonnage() throws IOException {

		board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

		joueur1 = board.getPlayerBoards().get(0);
		joueur2 = board.getPlayerBoards().get(1);

		if (nbTourOfficiel == 1) { /* 1er tour, choix personnage 1 par défaut, == get(0) dans json */

			rest.actionJeu(idPartieCurrent, idEquipe, "PRIEST");

		} else if (nbTourOfficiel == 2) {
			/*
			 * == get(1)pour nous et mais get(0) pour ennemi afin de retourner le 1er
			 * personnage choisi par l'équipe adverse
			 */

			persoEnnemiChoisi1 = joueur2.getFighters().get(0);

			if (persoEnnemiChoisi1.getFighterClass().contentEquals("GUARD")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("ORC")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "ORC");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("PRIEST")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
			}

		} else if (nbTourOfficiel == 3) {

			/*
			 * == get(2)pour nous et mais get(1) pour ennemi afin de retourner le 1er
			 * personnage choisi par l'équipe adverse et du coup get(1) aussi pour nous afin
			 * de savoir qui on a choisi
			 */

			/* rest.actionJeu(idPartieCurrent, idEquipe, "GUARD"); */

			persoEquipeChoisi2 = joueur1.getFighters().get(1);

			persoEnnemiChoisi2 = joueur2.getFighters().get(1);

			if (persoEnnemiChoisi2.getFighterClass().contentEquals("GUARD")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("ORC")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("PRIEST")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ORC")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ORC");
				}

			}

			}

	}

	public void play() throws IOException {
		
	}

	public void battreBot1() throws IOException {
		int reste = nbTourOfficiel % 2;

		if (reste == 0) {
			rest.actionJeu(idPartieCurrent, idEquipe, "A1,ATTACK,E1$A2,ATTACK,E2$A3,ATTACK,E3");
		} else {
			rest.actionJeu(idPartieCurrent, idEquipe, "A1,DEFEND,E1$A2,ATTACK,E2$A3,ATTACK,E3");
		}
	}

	public void serverSleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Erreur time serveur");
		}
	}

}
