package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHero;
import classApi.EpicHeroesLeague;

public class MoteurJeu {

	RestAPI rest = new RestAPI();
	Board board;

	/******************************/
	EpicHero scaredEnemy = null, currentFighter = null, needHealFighter = null, lowerLifeFighter = null,
			targuetEnemy = null;

	EpicHero guardEnemy = null, scaredAlly = null, burntAlly = null, healAlly = null, healEnemy = null,
			archerEnemy = null, chamanEnemy = null, orcEnemy = null;
	String choixStrat, envoiStratServeur = "";
	boolean fin = false, finInitialisation = false, noEffectsEnnemies = false, paladinAjoue = false;
	int nb, b, lifeDif;

	float calcul;

	EpicHero fighter1 = null, fighter2 = null, fighter3 = null, enemyFighter1 = null, enemyFighter2 = null,
			enemyFighter3 = null;


	/******************************/

	String idEquipe, idPartieCurrent;

	String listePerso;
	String statutPartie;

	boolean arretPartie = false;
	boolean accesPartie = false;
	boolean changementStatut = false;

	int nombresTours = 1;
	int nbTourOfficiel = 1;

	EpicHeroesLeague joueur1, joueur2;

	/***********************************/

	EpicHeroesLeague player1 = joueur1;
	EpicHeroesLeague player2 = joueur2;

	/***********************************/

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

		if (niveauBot > 0 && niveauBot < 7 || niveauBot > 10 && niveauBot < 22) {

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

				statutPartie = rest.tourEquipe(idPartieCurrent, idEquipe);

				if (statutPartie.equals("CANTPLAY")) {
					System.out.println("EN ATTENTE - NE PAS JOUER");

					while (statutPartie.equals("CANTPLAY") && changementStatut == false) {
						statutPartie = rest.tourEquipe(idPartieCurrent, idEquipe);
						if (statutPartie.equals("CANPLAY")) {
							changementStatut = true;
						}
					}
				}

				board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);
				joueur1 = board.getPlayerBoards().get(0);
				joueur2 = board.getPlayerBoards().get(1);

				if (statutPartie.equals("CANPLAY")) {
					serverSleep();
					System.out.println("OK - ON PEUT JOUER");
					choixPersonnage();
					System.out.println("Choix Personnage " + nbTourOfficiel);
					nbTourOfficiel++;
				}

			}

			/* Déroulement Partie */

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

				board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

				joueur1 = board.getPlayerBoards().get(0);
				joueur2 = board.getPlayerBoards().get(1);

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

		fighter1 = persoEquipeChoisi1;
		fighter1.setRank(1);
		fighter2 = persoEquipeChoisi2;
		fighter2.setRank(2);
		fighter3 = persoEquipeChoisi3;
		fighter3.setRank(3);

		enemyFighter1 = persoEnnemiChoisi1;
		enemyFighter1.setRank(1);
		enemyFighter2 = persoEnnemiChoisi2;
		enemyFighter2.setRank(2);
		enemyFighter3 = persoEnnemiChoisi3;
		enemyFighter3.setRank(3);

	}

	public void choixPersonnage() throws IOException {

		board = rest.plateauJeuEquipe(idPartieCurrent, idEquipe);

		joueur1 = board.getPlayerBoards().get(0);
		joueur2 = board.getPlayerBoards().get(1);

		if (nbTourOfficiel == 1) { /* 1er tour, choix personnage 1 par défaut, == get(0) dans json */

			rest.actionJeu(idPartieCurrent, idEquipe, "PALADIN");

		} else if (nbTourOfficiel == 2) {
			/*
			 * == get(1)pour nous et mais get(0) pour ennemi afin de retourner le 1er
			 * personnage choisi par l'équipe adverse
			 */

			persoEnnemiChoisi1 = joueur2.getFighters().get(0);

			if (persoEnnemiChoisi1.getFighterClass().contentEquals("GUARD")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("ORC")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("PRIEST")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "PALADIN");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("PALADIN")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("CHAMAN")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");

			} else if (persoEnnemiChoisi1.getFighterClass().contentEquals("ARCHER")) {
				rest.actionJeu(idPartieCurrent, idEquipe, "PRIEST");

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

			if (persoEnnemiChoisi2.getFighterClass().contentEquals("PALADIN")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ARCHER")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("ARCHER")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("CHAMAN")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "CHAMAN");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("PRIEST")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("PALADIN")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "PALADIN");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("CHAMAN")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ARCHER")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "PALADIN");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("GUARD")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ORC")
						|| persoEquipeChoisi2.getFighterClass().contentEquals("ARCHER")
						|| persoEquipeChoisi2.getFighterClass().contentEquals("PALADIN")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "CHAMAN");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				}

			} else if (persoEnnemiChoisi2.getFighterClass().contentEquals("ORC")) {

				if (persoEquipeChoisi2.getFighterClass().contentEquals("ARCHER")) {
					rest.actionJeu(idPartieCurrent, idEquipe, "GUARD");
				} else {
					rest.actionJeu(idPartieCurrent, idEquipe, "ARCHER");
				}

			}

		}

	}

	public void play() throws IOException {

		player1 = joueur1;
		player2 = joueur2;

		scaredEnemy = null;
		choixStrat = null;
		currentFighter = null;
		needHealFighter = player1.getFighters().get(0);
		lowerLifeFighter = player1.getFighters().get(0);
		targuetEnemy = null;
		scaredAlly = null;
		envoiStratServeur = "";
		guardEnemy = null;
		burntAlly = null;
		healAlly = null;
		noEffectsEnnemies = true;
		healEnemy = null;
		archerEnemy = null;
		lifeDif = 5000;
		paladinAjoue = false;
		chamanEnemy = null;
		orcEnemy = null;

		// Récupérations d'objetif
		// Récupération des statuts de notre équipe
		// Celui ayant le moins de PV
		for (b = 0; b < player1.getFighters().size(); b++) {
			if (joueur1.getFighters().get(b).getIsDead() == false) {
				if (lowerLifeFighter == null) {
					lowerLifeFighter = joueur1.getFighters().get(b);
				} else {
					if (joueur1.getFighters().get(b).getMaxAvailableLife()
							- joueur1.getFighters().get(b).getCurrentLife() > lowerLifeFighter.getMaxAvailableLife()
									- lowerLifeFighter.getCurrentLife()) {
						lowerLifeFighter = joueur1.getFighters().get(b);
					}
			 	}
			}
		}

		System.out.println("Le lowerLifeFighter est le " + lowerLifeFighter.getFighterClass());

		// On identifie le heal allié, s'il n'y en a pas healAlly = null
		/*
		 * for (int a = 0 ; a < 3 ; a++) { if
		 * (player1.getFighters().get(a).getFighterClass().contentEquals("HEAL")) {
		 * healAlly = player1.getFighters().get(a); } }
		 */

		// On identifie le heal allié, s'il n'y en a pas healAlly = null
		for (int a = 0 ; a < 3 ; a++) {
			if (player1.getFighters().get(a).getFighterClass().contentEquals("HEAL")) {
				healAlly = player1.getFighters().get(a);
			}
		}
		
		// Le brulé avec le moins de PV
		for (b = 0; b < player1.getFighters().size(); b++) {
			if (player1.getFighters().get(b).getIsDead() == false) {
				if (player1.getFighters().get(b).getStates() != null) {
					for(int c = 0; c < player1.getFighters().get(b).getStates().size(); c++) {
						if (player1.getFighters().get(b).getStates().get(c).getType().contentEquals("BURNING")) {
							if (burntAlly == null) {
								burntAlly = player1.getFighters().get(b);
							} else {
								if (player1.getFighters().get(b).getCurrentLife() < burntAlly.getCurrentLife()) {
									burntAlly = player1.getFighters().get(b);
								}
							}
						}
					}
				}
				
			}
		}
		
		
		//Est-ce que celui ayant perdu le plus de PV a perdu plus de 4PV (= ayant besoin d'un heal)
		if (lowerLifeFighter.getMaxAvailableLife() - lowerLifeFighter.getCurrentLife() >= 4) {
			needHealFighter = lowerLifeFighter;
		}
		
		
		// Statut effrayé (dans notre équipe)
		if (fighter1.getStates() != null) {
			for(b = 0; b < fighter1.getStates().size(); b++) {
				if (fighter1.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredAlly = fighter1;
				}
			}
		}
		if (fighter2.getStates() != null) {
			for(b = 0; b < fighter2.getStates().size(); b++) {
				if (fighter2.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredAlly = fighter2;
				}
			}
		}
		if (fighter3.getStates() != null) {
			for(b = 0; b < fighter3.getStates().size(); b++) {
				if (fighter3.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredAlly = fighter3;
				}
			}
		}
		
		
		
		// Récupération des statuts de l'équipe ennemie
		// Statut effrayé
		if (enemyFighter1.getStates() != null) {
			for(b = 0; b < enemyFighter1.getStates().size(); b++) {
				
				
				if (enemyFighter1.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredEnemy = enemyFighter1;
					System.out.println("SCARED - 1");
				}
			}
		}
		if (enemyFighter2.getStates() != null) {
			for(b = 0; b < enemyFighter2.getStates().size(); b++) {
				if (enemyFighter2.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredEnemy = enemyFighter2;
					System.out.println("SCARED - 2");
				}
			}
		}
		if (enemyFighter3.getStates() != null) {
			for(b = 0; b < enemyFighter3.getStates().size(); b++) {
				if (enemyFighter3.getStates().get(b).getType().contentEquals("SCARED")) {
					scaredEnemy = enemyFighter3;
					System.out.println("SCARED - 3");
				}
			}
		}
		
		/*
		 * // //On identifie l'archer ennemi, s'il n'y en a pas archerEnemy = null for
		 * (int a = 0 ; a < 3 ; a++) { if
		 * (player2.getFighters().get(a).getFighterClass().contentEquals("ARCHER")) {
		 * archerEnemy = player2.getFighters().get(a); System.out.println("ARCHER"); } }
		 * System.out.println("Il y a un archer ennemi : " + archerEnemy);
		 * 
		 * //On identifie le chaman ennemi, s'il n'y en a pas archerEnemy = null for
		 * (int a = 0 ; a < 3 ; a++) { if
		 * (player2.getFighters().get(a).getFighterClass().contentEquals("CHAMAN")) {
		 * chamanEnemy = player2.getFighters().get(a); } }
		 * System.out.println("Il y a un chaman ennemi : " + chamanEnemy);
		 */
		
		// Archer ennemi, si pas présent == null
		for (int a = 0; a < 3; a++) {
			if (player2.getFighters().get(a).getFighterClass().contentEquals("ARCHER")) {
				archerEnemy = player2.getFighters().get(a);
			}
		}
		System.out.println("Il y a un archer ennemi : " + archerEnemy);

		// Archer ennemi, si pas présent == null
		for (int a = 0; a < 3; a++) {
			if (player2.getFighters().get(a).getFighterClass().contentEquals("CHAMAN")) {
				chamanEnemy = player2.getFighters().get(a);
			}
		}
		System.out.println("Il y a un archer ennemi : " + chamanEnemy);

		/*
		// Archer ennemi, si pas présent == null 
		for (int a = 0; a < 3; a++) { 
			if (player2.getFighters().get(a).getFighterClass().contentEquals("PALADIN")) {
		 paladinEnemy = player2.getFighters().get(a); } }
		 
		 System.out.println("Il y a un paladin ennemi : " + paladinEnemy);
		*/ 

		// Guardien ennemi, s'il n'y en a pas guardEnemy = null
		for (int a = 0 ; a < 3 ; a++) {
			if (player2.getFighters().get(a).getFighterClass().contentEquals("GUARD")) {
				guardEnemy = player2.getFighters().get(a);
			}
		}
		System.out.println("Il y a un garde ennemi : " + guardEnemy);
		
		//On identifie le heal ennemi, s'il n'y en a pas healEnemy = null
		for (int a = 0 ; a < 3 ; a++) {
			if (player2.getFighters().get(a).getFighterClass().contentEquals("PRIEST")) {
				healEnemy = player2.getFighters().get(a);
			}
		}
		System.out.println("Il y a un prêtre ennemi : " + healEnemy);

		// On identifie l'orc ennemi, s'il n'y en a pas orcEnemy = null
		for (int a = 0; a < 3; a++) {
			if (player2.getFighters().get(a).getFighterClass().contentEquals("ORC")) {
				orcEnemy = player2.getFighters().get(a);
			}
		}
		System.out.println("Il y a un orc ennemi : " + orcEnemy);
		
		// Cible de prédilection (à utiliser quand aucun champion ennemi n'est effrayé)
		// Si il n'y a pas de prêtre dans l'équipe ennemie, on tape toujours la méme
		// cible jusqu'é ce qu'elle meure
		if (healEnemy == null) {
			for(b = 0; b < player2.getFighters().size(); b++) {
				// On vérifie que le champion n'est pas mort
				if (player2.getFighters().get(b).getIsDead() == false) {
					// on cible en priorité l'orc ennemi
					if (player2.getFighters().get(b).getFighterClass().contentEquals("PALADIN")) {
						targuetEnemy = player2.getFighters().get(b);
						break;
					} else if (player2.getFighters().get(b).getFighterClass().contentEquals("ORC")) {
						targuetEnemy = player2.getFighters().get(b);
						break;
					} else {
						if (targuetEnemy == null) {
							targuetEnemy = player2.getFighters().get(b);
						} else {
							if (targuetEnemy.getCurrentLife() > player2.getFighters().get(b).getCurrentLife()) {
								targuetEnemy = player2.getFighters().get(b);
							}
						}
					}
				}
			}
		} else {
			//Sinon s'il n'y a pas de heal, on frappe toujours le champion ayant le plus de vie
			for(b = 0; b < player2.getFighters().size(); b++) {
				// On vérifie que le champion n'est pas mort
				if (player2.getFighters().get(b).getIsDead() == false) {
					// On cible en priorité ceux qui n'ont pas perdu de vie
					lifeDif = player2.getFighters().get(b).getMaxAvailableLife() - player2.getFighters().get(b).getCurrentLife();
					if (lifeDif == 0) {
						targuetEnemy = player2.getFighters().get(b);
					} else {
						if (targuetEnemy != null) {
							if(player2.getFighters().get(b).getMaxAvailableLife() - player2.getFighters().get(b).getCurrentLife() < targuetEnemy.getMaxAvailableLife() - targuetEnemy.getCurrentLife()) {
								targuetEnemy = player2.getFighters().get(b);
							}
						} else {
							targuetEnemy = player2.getFighters().get(b);
						}
					}
				}
			}
		}
		// Fin des récupérations d'objetif
		
		
		// S'il y a des champions ennemis avec des capacités é effet (hors ORC)
		for(b = 0; b < player2.getFighters().size(); b++) {		
			if (player2.getFighters().get(b).getFighterClass().contentEquals("PALADIN") || player2.getFighters().get(b).getFighterClass().contentEquals("ARCHER")) {
				noEffectsEnnemies = false;
			}
		}
		
		
		
		// Vérification des cibles
		if (lowerLifeFighter != null) {
			System.out.println("Allié avec le moins de vie (en %) : " + lowerLifeFighter.getFighterClass());
		} else {
			System.out.println("Pas d'allié avec le moins de vie");
		}
		
		if (needHealFighter != null) {
			System.out.println("Allié nécessitant un soin : " + needHealFighter.getFighterClass());
		} else {
			System.out.println("Pas d'allié nécessitant un soin");
		}
		if (scaredAlly != null) {
			System.out.println("Allié nécessitant une protection car effrayé : " + scaredAlly.getFighterClass());
		} else {
			System.out.println("Pas d'allié nécessitant une protection car effrayé");
		}
		
		if (guardEnemy != null) {
			System.out.println("Guardien ennemi : " + guardEnemy.getFighterClass());
		} else {
			System.out.println("Pas de guardien ennemi");
		}
		if (scaredEnemy != null) {
			System.out.println("Champion ennemi effrayé : " + scaredEnemy.getFighterClass());
		} else {
			System.out.println("Pas de champion ennemi effrayé");
		}
		if (targuetEnemy != null) {
			System.out.println("Champion ennemi ciblé ce tour : " + targuetEnemy.getFighterClass() + ", "
					+ targuetEnemy.getCurrentLife() + "HP et " + targuetEnemy.getCurrentMana() + "PM");
		} else {
			System.out.println("Pas de champion ennemi ciblé ce tour");
		}
		if (burntAlly != null) {
			System.out.println("Le champion allié brûlé avec le moins de PV est le " + burntAlly.getFighterClass());
		} else {
			System.out.println("Pas de champion allié brûlé ce tour");
		}
		
		for (int a = 0; a < player1.getFighters().size(); a++) {
			player1.getFighters().get(a).setRank(a + 1);
			player2.getFighters().get(a).setRank(a + 1);

		}
		
		
		
		// Pour chaque champion dans notre équipe
		for (int a = 0; a < player1.getFighters().size(); a++) {
			
			// On récupére le champion courant dans la liste de nos champions
			currentFighter = player1.getFighters().get(a);
			
			currentFighter.setRank(a + 1);
			player2.getFighters().get(a).setRank(a + 1);

			System.out.println("Needheal : " + lowerLifeFighter.getRank());

			System.out.println("Champion : " + currentFighter.getFighterClass() + ", " + currentFighter.getCurrentLife()
					+ "PV" + ", " + currentFighter.getCurrentMana() + "PM" + ", " + currentFighter.getRank() + "rank ");
			
			
			// Choix stratégiques
			if (!currentFighter.getIsDead()) {

				// Si c'est un pretre

				if (currentFighter.getFighterClass().contentEquals("PRIEST")) {
					if (currentFighter.getCurrentMana() == 0) {
						//On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
						System.out.println("DEBUG 1,1");
					} else if (currentFighter.getCurrentMana() == 1) {
						//On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
						System.out.println(choixStrat);
						System.out.println("DEBUG 1,2");
					} else if (currentFighter.getCurrentMana() == 2) {
						if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + currentFighter.getRank() + ",HEAL,A" + needHealFighter.getRank();
							System.out.println("DEBUG 1,3");
							System.out.println(choixStrat);
						} else {
							//Sinon
							//On ne fait rien
							choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
							System.out.println("DEBUG 1,4");
						}
					} else if (currentFighter.getCurrentMana() == 3) {
						if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + currentFighter.getRank() + ",HEAL,A" + lowerLifeFighter.getRank();
							System.out.println("DEBUG 1,5");
						} else {
							//Sinon
							//On ne fait rien
							choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
							System.out.println("DEBUG 1,6");
						}
					} else if (currentFighter.getCurrentMana() == 4) {
						if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + currentFighter.getRank() + ",HEAL,A" + needHealFighter.getRank();
							System.out.println("DEBUG 1,7");
						} else {
							//Sinon
							// On se met en défense
							choixStrat = "A" + currentFighter.getRank() + ",DEFEND,A" + currentFighter.getRank();
							System.out.println("DEBUG 1,8");
						}
					}
				} // fin du cas pretre
					
					
					//Si c'est un orc
				if (currentFighter.getFighterClass().contentEquals("ORC")) {
						if (currentFighter.getCurrentMana() == 0) {
							//On ne fait rien
							choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
						System.out.println("DEBUG 2,1");
						System.out.println(choixStrat);
						} else if (currentFighter.getCurrentMana() == 1) {
							if (scaredEnemy != null) {
							// Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
							System.out.println("DEBUG 2,2");
							System.out.println(choixStrat);
							} else {
								//Sinon
								//On ne fait rien
								choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
							System.out.println("DEBUG 2,3");
							System.out.println(choixStrat);
							}						
						} else if (currentFighter.getCurrentMana() == 2) {
							if (scaredEnemy != null) {
							// Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
							System.out.println("DEBUG 2,4");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (guardEnemy.getIsDead() == false & guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + currentFighter.getRank() + ",YELL,E" + guardEnemy.getRank();
								System.out.println("DEBUG 2,5");
								System.out.println(choixStrat);
								}
							} else {
							// Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
							System.out.println("DEBUG 2,6");
							System.out.println(choixStrat);
							}
						} else if (currentFighter.getCurrentMana() == 3) {
							if (scaredEnemy != null) {
							// Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
							System.out.println("DEBUG 2,7");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (guardEnemy.getIsDead() == false & guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + currentFighter.getRank() + ",YELL,E" + guardEnemy.getRank();
								System.out.println("DEBUG 2,8");
								System.out.println(choixStrat);
								}
							} else {
							// Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
							System.out.println("DEBUG 2,9");
							System.out.println(choixStrat);
							}
						} else if (currentFighter.getCurrentMana() == 4) {
							if (scaredEnemy != null) {
							// Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
							System.out.println("DEBUG 2,10");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (guardEnemy.getIsDead() == false & guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + currentFighter.getRank() + ",YELL,E" + guardEnemy.getRank();
								System.out.println("DEBUG 2,11");
								System.out.println(choixStrat);
								}
							} else {
							// Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
							System.out.println("DEBUG 2,12");
							System.out.println(choixStrat);
							}

						}
					} //fin du cas de l'orc
					
					
					//Si c'est un gardien
					if (currentFighter.getFighterClass().contentEquals("GUARD")) {
						if (currentFighter.getCurrentMana() == 0) {
							//On ne fait rien
							choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
						System.out.println("DEBUG 3,1");
						} else if (currentFighter.getCurrentMana() == 1) {
							if (scaredEnemy != null) {
							// Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
							System.out.println("DEBUG 3,2");
							} else {
								//Sinon
								//On ne fait rien
								choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
							System.out.println("DEBUG 3,3");
							}
						} else if (currentFighter.getCurrentMana() == 2) {
							if (scaredAlly != null & scaredAlly != currentFighter) {
							// Si l'un de nos champions est effrayé (sauf le gardien lui-méme), on le
							// protége en priorité
								choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + scaredAlly.getRank();
							System.out.println("DEBUG 3,4");
							} else if (needHealFighter != null & currentFighter.getCurrentLife() > 20 & needHealFighter != currentFighter) {
							// Si l'allié é protéger a moins de la moitié de ses vies
								if (needHealFighter.getCurrentLife() < needHealFighter.getMaxAvailableLife() / 2) {
								// On le protége
									choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + needHealFighter.getRank();
								System.out.println("On protége un allié qui a moins de la moitié de sa vie");
								System.out.println("DEBUG 3,5");
								} else {
									//Sinon on ne fait rien
									choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
								System.out.println("DEBUG 3,6");
								}
							} else {
							// Sinon on se met en défense si on a moins de 30PV
								if (currentFighter.getCurrentLife() <= 30) {
									choixStrat = "A" + currentFighter.getRank() + ",DEFEND,A" + currentFighter.getRank();
								System.out.println("DEBUG 3,7");
								} else {
									if (scaredEnemy != null) {
									// Si il y a un ennemi effrayé, on l'attaque
										choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
									System.out.println("DEBUG 3,8");
									} else {
										//Sinon on ne fait rien
										choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
									System.out.println("DEBUG 3,9");
									}
								}
							}
						} else if (currentFighter.getCurrentMana() == 3) {
							if (scaredAlly != null & scaredAlly != currentFighter) {
							// Si l'un de nos champions est effrayé, on le protége en priorité
								choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + scaredAlly.getRank();
							System.out.println("DEBUG 3,10");
							} else if (needHealFighter != null & currentFighter.getCurrentLife() > 20 & needHealFighter != currentFighter) {
							// Sinon si quelqu'un a besoin d'un heal et que l'allié é défendre a moins de
							// 15PV on le protége (sauf si le guardien a moins de 20PV )
								choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + needHealFighter.getRank();
							System.out.println("DEBUG 3,11");
							} else {
							// Sinon on se met en défense si on a moins de 15PV
								if (currentFighter.getCurrentLife() <= 15) {
									choixStrat = "A" + currentFighter.getRank() + ",DEFEND,A" + currentFighter.getRank();
								System.out.println("DEBUG 3,12");
								} else if (scaredEnemy != null) {
								// Si il y a un ennemi effrayé, on l'attaque
									choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
								System.out.println("DEBUG 3,13");
								} else {
									if (currentFighter != lowerLifeFighter & currentFighter.getCurrentLife() > 20) {
									// On défend l'allié ayant le moins de PV si ce n'est pas nous (il faut qu'on
									// ait + de 20PV)
										choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + lowerLifeFighter.getRank();
									System.out.println("DEBUG 3,14");
									} else {
									// On se met en défense
										choixStrat = "A" + currentFighter.getRank() + ",DEFEND,A" + currentFighter.getRank();
									System.out.println("DEBUG 3,15");
									}
								}
							}
						} else if (currentFighter.getCurrentMana() == 4) {

							if (scaredAlly != null) {
							// Si l'un de nos champions est effrayé, on le protége en priorité
								choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + scaredAlly.getRank();
							} else if (needHealFighter != null & currentFighter.getCurrentLife() > 20) {
							// Sinon si quelqu'un a besoin d'un heal et que l'allié é défendre a moins de
							// 15PV on le protége (sauf si le guardien a moins de 20PV )
								choixStrat = "A" + currentFighter.getRank() + ",PROTECT,A" + needHealFighter.getRank();
							} else {
							// Sinon on se met en défense si on a moins de 15PV
								if (currentFighter.getCurrentLife() <= 15) {
									choixStrat = "A" + currentFighter.getRank() + ",DEFEND,A" + currentFighter.getRank();
								} else if (scaredEnemy != null) {
								// Si il y a un ennemi effrayé, on l'attaque
									choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + scaredEnemy.getRank();
								} else {
								// Sinon on attaque simplement la cible de prédilection
									choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
								}
							}

						System.out.println("DEBUG 3,16");
						System.out.println("N'est pas sensé exister");
						}
					} //fin du cas du guardien
					
					
					
				
				if (currentFighter.getFighterClass().contentEquals("CHAMAN")) {
					if (currentFighter.getCurrentMana() == 0) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 1) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 2) {
						if (burntAlly != null) {
							choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + burntAlly.getRank();
						} else {
							// Si un alli� a besoin d'un heal et que le heal � moins de 2 PA, on le cleanse
							// � condition d'avoir plus de 20PV
							if (needHealFighter != null & healAlly != null) {
								if (healAlly.getCurrentMana() < 2 & currentFighter.getCurrentLife() >= 20) {
									choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + healAlly.getRank();
								} else {
									// Sinon
									choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A"
											+ currentFighter.getRank();
								}
							} else if (noEffectsEnnemies == true) {
								// On attaque simplement la cible de pr�dilection
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
							} else {
								// Sinon
								choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + currentFighter.getRank();
							}
						}
					} else if (currentFighter.getCurrentMana() > 2) {
						if (burntAlly != null) {
							choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + burntAlly.getRank();
						} else {
							// Si un alli� a besoin d'un heal et que le heal � moins de 2 PA, on le cleanse
							// � condition d'avoir plus de 20PV
							if (needHealFighter != null & healAlly != null) {
								if (healAlly.getCurrentMana() < 2 & currentFighter.getCurrentLife() >= 20) {
									choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + healAlly.getRank();
								} else {
									// Sinon
									choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A"
											+ currentFighter.getRank();
								}
							} else if (noEffectsEnnemies == true) {
								// On attaque simplement la cible de pr�dilection
								choixStrat = "A" + currentFighter.getRank() + ",ATTACK,E" + targuetEnemy.getRank();
							} else {
								// Sinon
								choixStrat = "A" + currentFighter.getRank() + ",CLEANSE,A" + currentFighter.getRank();
							}
						}
					}
				} // fin du cas du chaman
					
					
				// Si c'est un archer
				if (currentFighter.getFighterClass().contentEquals("ARCHER")) {
					if (currentFighter.getCurrentMana() == 0) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 1) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 2) {
						// On lance une fl�che sur l'ennemi de pr�dilection
						choixStrat = "A" + currentFighter.getRank() + ",FIREBOLT,E" + targuetEnemy.getRank();
					} else if (currentFighter.getCurrentMana() == 3) {
						// On lance une fl�che sur l'ennemi de pr�dilection
						choixStrat = "A" + currentFighter.getRank() + ",FIREBOLT,E" + targuetEnemy.getRank();
					} else if (currentFighter.getCurrentMana() == 4) {
						// On lance une fl�che sur l'ennemi de pr�dilection
						choixStrat = "A" + currentFighter.getRank() + ",FIREBOLT,E" + targuetEnemy.getRank();
					}
				} // fin du cas de l'archer
					
					
					
				// Si c'est un paladin
				if (currentFighter.getFighterClass().contentEquals("PALADIN")) {
					if (currentFighter.getCurrentMana() == 0) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 1) {
						// On ne fait rien
						choixStrat = "A" + currentFighter.getRank() + ",REST,A" + currentFighter.getRank();
					} else if (currentFighter.getCurrentMana() == 2) {
						// Si jamais le garde ennemi existe
						if (guardEnemy != null) {
							if (guardEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + guardEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais le pr�tre ennemi existe
						if (healEnemy != null) {
							if (healEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + healEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais l'archer ennemi existe
						if (archerEnemy != null) {
							if (archerEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + archerEnemy.getRank();
								paladinAjoue = true;
							}
						}

						// Si jamais le chaman ennemi existe
						if (chamanEnemy != null) {
							if (chamanEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + chamanEnemy.getRank();
								paladinAjoue = true;
							}
						}

						if (paladinAjoue == false) {
							// Si jamais il n'y a aucun garde, ni pr�tre, ni archer on charge l'ennemi de
							// pr�dilection
							choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + targuetEnemy.getRank();
						}
					} else if (currentFighter.getCurrentMana() == 3) {
						// Si jamais le garde ennemi existe
						if (guardEnemy != null) {
							if (guardEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + guardEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais le pr�tre ennemi existe
						if (healEnemy != null) {
							if (healEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + healEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais l'archer ennemi existe
						if (archerEnemy != null) {
							if (archerEnemy.getCurrentMana() >= 2) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + archerEnemy.getRank();
								paladinAjoue = true;
							}
						}

						// Si jamais le chaman ennemi existe
						if (chamanEnemy != null) {
							if (chamanEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + chamanEnemy.getRank();
								paladinAjoue = true;
							}
						}

						if (paladinAjoue == false) {
							// Si jamais il n'y a aucun garde, ni pr�tre, ni archer on charge l'ennemi de
							// pr�dilection
							choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + targuetEnemy.getRank();
						}
					} else if (currentFighter.getCurrentMana() == 4) {
						// Si jamais le garde ennemi existe
						if (guardEnemy != null) {
							if (guardEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + guardEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais le pr�tre ennemi existe
						if (healEnemy != null) {
							if (healEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + healEnemy.getRank();
								paladinAjoue = true;
							}
						}
						// Si jamais l'archer ennemi existe
						if (archerEnemy != null) {
							if (archerEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + archerEnemy.getRank();
								paladinAjoue = true;
							}
						}

						// Si jamais le chaman ennemi existe
						if (chamanEnemy != null) {
							if (chamanEnemy.getCurrentMana() >= 1) {
								// Et qu'il a deux PA ou plus, on le charge
								choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + chamanEnemy.getRank();
								paladinAjoue = true;
							}
						}

						if (paladinAjoue == false) {
							// Si jamais il n'y a aucun garde, ni pr�tre, ni archer on charge l'ennemi de
							// pr�dilection
							choixStrat = "A" + currentFighter.getRank() + ",CHARGE,E" + targuetEnemy.getRank();
						}
					}
				} // fin du cas du paladin
					
			} else { //fin de la condition "n'est pas mort" -- if (currentFighter.getIsDead() == false)
				//Si le personnage est mort
				choixStrat = "";
				System.out.println("DEBUG 3,17 - MORT");
			}
			
		System.out.println("choixStrat = " + choixStrat);
		envoiStratServeur = envoiStratServeur + choixStrat + "$";
		} //fin de le boucle for			
		
		envoiStratServeur = envoiStratServeur.substring(0, envoiStratServeur.length() - 1);
		System.out.println("envoiStratServeur = " + envoiStratServeur);
		
		//On envoie l'action au serveur
	
		rest.actionJeu(idPartieCurrent, idEquipe, envoiStratServeur);
		
		
		System.out.println("------------ fin du tour ------------");
		// fin du tour

		// Mise à jour de la variable fin

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
