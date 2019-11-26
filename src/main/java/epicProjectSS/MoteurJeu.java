package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHero;
import classApi.EpicHeroesLeague;
import classApi.State;

public class MoteurJeu {

	RestAPI rest = new RestAPI();
	Board board;

	/******************************/
	EpicHero scaredEnemy = null, hero = null, needHealFighter = null, lowerLifeFighter = null,
			targetEnemy = null;

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
		fighter1.setOrderNumberInTeam(1);
		fighter2 = persoEquipeChoisi2;
		fighter2.setOrderNumberInTeam(2);
		fighter3 = persoEquipeChoisi3;
		fighter3.setOrderNumberInTeam(3);

		enemyFighter1 = persoEnnemiChoisi1;
		enemyFighter1.setOrderNumberInTeam(1);
		enemyFighter2 = persoEnnemiChoisi2;
		enemyFighter2.setOrderNumberInTeam(2);
		enemyFighter3 = persoEnnemiChoisi3;
		enemyFighter3.setOrderNumberInTeam(3);

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

		player1 = joueur1;
		player2 = joueur2;

		
		needHealFighter = player1.getFighters().get(0);
		lowerLifeFighter = player1.getFighters().get(0);
		targetEnemy = null;
		scaredAlly = null;
		envoiStratServeur = "";
		burntAlly = null;
		healAlly = null;
		noEffectsEnnemies = true;
		healEnemy = null;
		scaredEnemy = null;
		lifeDif = 5000;
		paladinAjoue = false;
	
		
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
		
		//On identifie le heal allié, s'il n'y en a pas healAlly = null

		
		//On identifie le heal allié, s'il n'y en a pas healAlly = null
		for (int a = 0 ; a < 3 ; a++) {
			if (player1.getFighters().get(a).getFighterClass().contentEquals("PRIEST")) {
				healAlly = player1.getFighters().get(a);
			}
		}
		
		//Le brulé avec le moins de PV
		for (int z = 0; z < player1.getFighters().size(); z++) {
			if (player1.getFighters().get(z).getIsDead() == false) {
				if (player1.getFighters().get(z).getStates() != null) {
					for(int c = 0; c < player1.getFighters().get(z).getStates().size(); c++) {
						if (player1.getFighters().get(z).getStates().get(c).getType().contentEquals("BURNING")) {
							if (burntAlly == null) {
								burntAlly = player1.getFighters().get(z);
							} else {
								if (player1.getFighters().get(z).getCurrentLife() < burntAlly.getCurrentLife()) {
									burntAlly = player1.getFighters().get(z);
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
	
		// On vérifie si un allié n'est pas effrayé
		for(EpicHero ally : player1.getFighters()) {
			if(!ally.getIsDead() && ally.getStates() != null) {
				for(State state : ally.getStates()) {
					if(state.getType().equalsIgnoreCase("SCARED")) {
						scaredAlly = ally;
					}
				}

			}
		}

		
		for (EpicHero enemy : player2.getFighters()){
			//On identifie le guardien ennemi, s'il n'y en a pas guardEnemy = null
			if(enemy.getFighterClass().contentEquals("GUARD")) {
				guardEnemy = enemy;
			}
			//On identifie le heal ennemi, s'il n'y en a pas healEnemy = null
			if(enemy.getFighterClass().contentEquals("PRIEST")) {
				healEnemy = enemy;
			}
			//On identifie l'orc ennemi, s'il n'y en a pas orcEnemy = null
			if(enemy.getFighterClass().contentEquals("ORC")) {
				orcEnemy = enemy;
			}				
			//Récupération des statuts de l'équipe ennemie
			//Statut effrayé
			if (!enemy.getIsDead() && enemy.getStates() != null) {
				for (State state : enemy.getStates()) {
					if(state.getType().equals("SCARED")){
						scaredEnemy = enemy;
						System.out.println("SCARED - " + enemy.getFighterClass() );
					}
				}
			}	
		}
		System.out.println("Il y a un garde ennemi : " + guardEnemy);
		
		System.out.println("Il y a un prêtre ennemi : " + healEnemy);
		
		System.out.println("Il y a un orc ennemi : " + orcEnemy);
		
		
		//Cible de prédilection (à utiliser quand aucun champion ennemi n'est effrayé)
		//Si il n'y a pas de prêtre dans l'équipe ennemie, on tape toujours la méme cible jusqu'à ce qu'elle meure
		if (healEnemy == null) {
			for (EpicHero enemy : player2.getFighters()){
				//On vérifie que le champion n'est pas mort
				if (!enemy.getIsDead()) {
					System.out.println(enemy.getFighterClass() + "a pour état de santé : " + enemy.getCurrentLife() + "c'est à dire mort ? " + enemy.getIsDead());
					//on cible en priorité l'orc ennemi
					if (enemy.getFighterClass().contentEquals("PALADIN")) {
						targetEnemy = enemy;
						break;
					} else if (enemy.getFighterClass().contentEquals("ORC")) {
						targetEnemy = enemy;
						break;
					} else {
						if (targetEnemy == null) {
							targetEnemy = enemy;
						} else {
							if (targetEnemy.getCurrentLife() > enemy.getCurrentLife()) {
								targetEnemy = enemy;
							}
						}
					}
				}
			}
		} else {
			//Sinon s'il n'y a pas de heal, on frappe toujours le champion ayant le plus de vie
			for (EpicHero enemy : player2.getFighters()){
				//On vérifie que le champion n'est pas mort
				if (!enemy.getIsDead()) {
					//On cible en priorité ceux qui n'ont pas perdu de vie
					lifeDif = enemy.getMaxAvailableLife() - enemy.getCurrentLife();
					if (lifeDif == 0) {
						targetEnemy = enemy;
					} else {
						if (targetEnemy != null) {
							if(enemy.getMaxAvailableLife() - enemy.getCurrentLife() < targetEnemy.getMaxAvailableLife() - targetEnemy.getCurrentLife()) {
								targetEnemy = enemy;
							}
						} else {
							targetEnemy = enemy;
						}
					}
				}
			}
		}
		//Fin des récupérations d'objetif
		
		
		//S'il y a des champions ennemis avec des capacités à effet (hors ORC)
		for(b = 0; b < player2.getFighters().size(); b++) {		
			if (player2.getFighters().get(b).getFighterClass().contentEquals("PALADIN") || player2.getFighters().get(b).getFighterClass().contentEquals("ARCHER")) {
				noEffectsEnnemies = false;
			}
		}
		
		
		
		//Vérification des cibles
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
		if (targetEnemy != null) {
			System.out.println("Champion ennemi ciblé ce tour : " + targetEnemy.getFighterClass() + ", " + targetEnemy.getCurrentLife() + "HP et " + targetEnemy.getCurrentMana() + "PM");
		} else {
			System.out.println("Pas de champion ennemi ciblé ce tour");
		}
		if (burntAlly != null) {
			System.out.println("Le champion allié brûlé avec le moins de PV est le " + burntAlly.getFighterClass());
		} else {
			System.out.println("Pas de champion allié brûlé ce tour");
		}
	
		
		//Pour chaque champion dans notre équipe
		for (EpicHero hero : player1.getFighters()){
			
			System.out.println("Needheal : " + lowerLifeFighter.getOrderNumberInTeam());
			
			
			System.out.println("Champion : " + hero.getFighterClass() + ", " + hero.getCurrentLife() + "PV" + ", " + hero.getCurrentMana() + "PM" + ", " + hero.getOrderNumberInTeam() + "rank ");
			
			
			//Choix stratégiques
			if (!hero.getIsDead()) {

				// Si c'est un pretre

				if (hero.getFighterClass().contentEquals("PRIEST")) {
					if (hero.getCurrentMana() == 0) {
						//On ne fait rien
						choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
						System.out.println("DEBUG 1,1");
					} else if (hero.getCurrentMana() == 1) {
						if(scaredEnemy != null){
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 1,2");
							System.out.println(choixStrat);
						}else {
							//On ne fait rien
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();						
							System.out.println(choixStrat);
							System.out.println("DEBUG 1,2,1");
						}
						
					} else if (hero.getCurrentMana() == 2) {
						if(scaredEnemy != null && needHealFighter.getCurrentLife() <= 10){
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 1,3");
							System.out.println(choixStrat);
						}else if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",HEAL,A" + needHealFighter.getOrderNumberInTeam();
							System.out.println("DEBUG 1,3,1 = " + hero.getCurrentMana());
							System.out.println(choixStrat);
						} else {
							//Sinon
							//On ne fait rien
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
							System.out.println("DEBUG 1,4");
						}
					} else if (hero.getCurrentMana() == 3) {
						if(scaredEnemy != null && needHealFighter.getCurrentLife() <= 1){
						choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
						System.out.println("DEBUG 1,5");
						System.out.println(choixStrat);
						}
						else if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",HEAL,A" + lowerLifeFighter.getOrderNumberInTeam();
							System.out.println("DEBUG 1,5,1");
							
						} else {
							//Sinon
							//On ne fait rien
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
							System.out.println("DEBUG 1,5,2");
						}
					} else if (hero.getCurrentMana() == 4) {
						if(scaredEnemy != null && needHealFighter.getCurrentLife() <= 1){
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 1,6");
							System.out.println(choixStrat);
						}
						else if (needHealFighter != null) {
							//Si quelqu'un a besoin d'un heal
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",HEAL,A" + needHealFighter.getOrderNumberInTeam();
							System.out.println("DEBUG 1,7");
						} else {
							//Sinon
							//On se met en défense
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,A" + hero.getOrderNumberInTeam();
							System.out.println("DEBUG 1,8");
						}
					}
				} // fin du cas pretre
					
					
					//Si c'est un orc
				if (hero.getFighterClass().contentEquals("ORC")) {
						if (hero.getCurrentMana() == 0) {
							//On ne fait rien
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
						System.out.println("DEBUG 2,1");
						System.out.println(choixStrat);
						} else if (hero.getCurrentMana() == 1) {
							if (scaredEnemy != null) {
								//Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,2");
							System.out.println(choixStrat);
							} else {
								//Sinon
								//On ne fait rien
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
							System.out.println("DEBUG 2,3");
							System.out.println(choixStrat);
							}						
						} else if (hero.getCurrentMana() == 2) {
							if (scaredEnemy != null) {
								//Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,4");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (!guardEnemy.getIsDead() && guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",YELL,E" + guardEnemy.getOrderNumberInTeam();
								System.out.println("DEBUG 2,5");
								System.out.println(choixStrat);
								}else{
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",YELL,E" + targetEnemy.getOrderNumberInTeam();
									System.out.println("DEBUG 2,5,1");
									System.out.println(choixStrat);
								}
							} else {
									//Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + targetEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,6");
							System.out.println(choixStrat);
							}
						} else if (hero.getCurrentMana() == 3) {
							if (scaredEnemy != null) {
								//Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,7");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (guardEnemy.getIsDead() == false & guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",YELL,E" + guardEnemy.getOrderNumberInTeam();
								System.out.println("DEBUG 2,8");
								System.out.println(choixStrat);
								}else{
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",YELL,E" + targetEnemy.getOrderNumberInTeam();
									System.out.println("DEBUG 2,8,1");
									System.out.println(choixStrat);
								}
							} else {
									//Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + targetEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,9");
							System.out.println(choixStrat);
							}
						} else if (hero.getCurrentMana() == 4) {
							if (scaredEnemy != null) {
								//Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,10");
							System.out.println(choixStrat);
							} else  if (guardEnemy != null) {
								//Sinon
								System.out.println(guardEnemy != null);
								//Si le guardien ennemi existe, n'est pas mort et a 0 PA on lance un hurlement
								if (guardEnemy.getIsDead() == false & guardEnemy.getCurrentMana() == 0) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",YELL,E" + guardEnemy.getOrderNumberInTeam();
								System.out.println("DEBUG 2,11");
								System.out.println(choixStrat);
								}
							} else {
									//Sinon on attaque simplement la cible de prédilection
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + targetEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 2,12");
							System.out.println(choixStrat);
							}
			
						}
					} //fin du cas de l'orc
					
					
					//Si c'est un gardien
					if (hero.getFighterClass().contentEquals("GUARD")) {
						if (hero.getCurrentMana() == 0) {
							//On ne fait rien
							choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
						System.out.println("DEBUG 3,1");
						} else if (hero.getCurrentMana() == 1) {
							if (scaredEnemy != null) {
								//Si il y a un ennemi effrayé, on l'attaque
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
							System.out.println("DEBUG 3,2");
							} else {
								//Sinon
								//On ne fait rien
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
							System.out.println("DEBUG 3,3");
							}
						} else if (hero.getCurrentMana() == 2) {
							if (scaredAlly != null & scaredAlly != hero) {
								//Si l'un de nos champions est effrayé (sauf le gardien lui-méme), on le protége en priorité
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + scaredAlly.getOrderNumberInTeam();
							System.out.println("DEBUG 3,4");
							} else if (needHealFighter != null & hero.getCurrentLife() > 20 & needHealFighter != hero) {
								//Si l'allié é protéger a moins de la moitié de ses vies
								if (needHealFighter.getCurrentLife() < needHealFighter.getMaxAvailableLife() / 2) {
									//On le protége
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + needHealFighter.getOrderNumberInTeam();
									System.out.println("On protége un allié qui a moins de la moitié de sa vie");
								System.out.println("DEBUG 3,5");
								} else {
									//Sinon on ne fait rien
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
								System.out.println("DEBUG 3,6");
								}
							} else {
								//Sinon on se met en défense si on a moins de 30PV
								if (hero.getCurrentLife() <= 30) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",DEFEND,A" + hero.getOrderNumberInTeam();
								System.out.println("DEBUG 3,7");
								} else {
									if (scaredEnemy != null) {
										//Si il y a un ennemi effrayé, on l'attaque
										choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
									System.out.println("DEBUG 3,8");
									} else {
										//Sinon on ne fait rien
										choixStrat = "A" + hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam();
									System.out.println("DEBUG 3,9");
									}
								}
							}
						} else if (hero.getCurrentMana() == 3) {
							if (scaredAlly != null & scaredAlly != hero) {
								//Si l'un de nos champions est effrayé, on le protége en priorité
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + scaredAlly.getOrderNumberInTeam();
							System.out.println("DEBUG 3,10");
							} else if (needHealFighter != null & hero.getCurrentLife() > 20 & needHealFighter != hero) {
								//Sinon si quelqu'un a besoin d'un heal et que l'allié é défendre a moins de 15PV on le protége (sauf si le guardien a moins de 20PV )
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + needHealFighter.getOrderNumberInTeam();
							System.out.println("DEBUG 3,11");
							} else {
								//Sinon on se met en défense si on a moins de 15PV
								if (hero.getCurrentLife() <= 15) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",DEFEND,A" + hero.getOrderNumberInTeam();
								System.out.println("DEBUG 3,12");
								} else if (scaredEnemy != null) {
									//Si il y a un ennemi effrayé, on l'attaque
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
								System.out.println("DEBUG 3,13");
								} else {
									if (hero != lowerLifeFighter & hero.getCurrentLife() > 20) {
										//On défend l'allié ayant le moins de PV si ce n'est pas nous (il faut qu'on ait + de 20PV)
										choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + lowerLifeFighter.getOrderNumberInTeam();
									System.out.println("DEBUG 3,14");
									} else {
										//On se met en défense
										choixStrat = "A" + hero.getOrderNumberInTeam() + ",DEFEND,A" + hero.getOrderNumberInTeam();
									System.out.println("DEBUG 3,15");
									}
								}
							}
						} else if (hero.getCurrentMana() == 4) {
							
							if (scaredAlly != null) {
								//Si l'un de nos champions est effrayé, on le protége en priorité
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + scaredAlly.getOrderNumberInTeam();
							} else if (needHealFighter != null & hero.getCurrentLife() > 20) {
								//Sinon si quelqu'un a besoin d'un heal et que l'allié é défendre a moins de 15PV on le protége (sauf si le guardien a moins de 20PV )
								choixStrat = "A" + hero.getOrderNumberInTeam() + ",PROTECT,A" + needHealFighter.getOrderNumberInTeam();
							} else {
								//Sinon on se met en défense si on a moins de 15PV
								if (hero.getCurrentLife() <= 15) {
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",DEFEND,A" + hero.getOrderNumberInTeam();
								} else if (scaredEnemy != null) {
									//Si il y a un ennemi effrayé, on l'attaque
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + scaredEnemy.getOrderNumberInTeam();
								} else {
									//Sinon on attaque simplement la cible de prédilection
									choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" + targetEnemy.getOrderNumberInTeam();
								}
							}
							
						System.out.println("DEBUG 3,16");
							System.out.println("N'est pas sensé exister");
						}
					} //fin du cas du guardien
					
					
					
				/*
				 * //Si c'est un chaman if
				 * (hero.getFighterClass().contentEquals("CHAMAN")) { if
				 * (hero.getCurrentMana() == 0) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 1) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 2) { if (burntAlly != null) { choixStrat
				 * = "A" + hero.getOrderNumberInTeam() + ",CLEANSE,A" + burntAlly.getOrderNumberInTeam(); } else
				 * { //Si un allié a besoin d'un heal et que le heal é moins de 2 PA, on le
				 * cleanse é condition d'avoir plus de 20PV if (needHealFighter != null &
				 * healAlly != null) { if (healAlly.getCurrentMana() < 2 &
				 * hero.getCurrentLife() >= 20) { choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CLEANSE,A" + healAlly.getOrderNumberInTeam(); } else {
				 * //Sinon choixStrat = "A" + hero.getOrderNumberInTeam() + ",CLEANSE,A" +
				 * hero.getOrderNumberInTeam(); } } else if (noEffectsEnnemies == true) { //On
				 * attaque simplement la cible de prédilection choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",ATTACK,E" + targetEnemy.getOrderNumberInTeam(); } else {
				 * //Sinon choixStrat = "A" + hero.getOrderNumberInTeam() + ",CLEANSE,A" +
				 * hero.getOrderNumberInTeam(); } } } else if (hero.getCurrentMana() > 2)
				 * { if (burntAlly != null) { choixStrat = "A" + hero.getOrderNumberInTeam() +
				 * ",CLEANSE,A" + burntAlly.getOrderNumberInTeam(); } else { //Si un allié a besoin d'un heal
				 * et que le heal é moins de 2 PA, on le cleanse é condition d'avoir plus de
				 * 20PV if (needHealFighter != null & healAlly != null) { if
				 * (healAlly.getCurrentMana() < 2 & hero.getCurrentLife() >= 20) {
				 * choixStrat = "A" + hero.getOrderNumberInTeam() + ",CLEANSE,A" +
				 * healAlly.getOrderNumberInTeam(); } else { //Sinon choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CLEANSE,A" + hero.getOrderNumberInTeam(); } } else
				 * if (noEffectsEnnemies == true) { //On attaque simplement la cible de
				 * prédilection choixStrat = "A" + hero.getOrderNumberInTeam() + ",ATTACK,E" +
				 * targetEnemy.getOrderNumberInTeam(); } else { //Sinon choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CLEANSE,A" + hero.getOrderNumberInTeam(); } } } }
				 * //fin du cas du chaman
				 */
					
					
				/*
				 * //Si c'est un archer if
				 * (hero.getFighterClass().contentEquals("ARCHER")) { if
				 * (hero.getCurrentMana() == 0) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 1) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 2) { //On lance une fléche sur l'ennemi
				 * de prédilection choixStrat = "A" + hero.getOrderNumberInTeam() + ",FIREBOLT,E" +
				 * targetEnemy.getOrderNumberInTeam(); } else if (hero.getCurrentMana() == 3) {
				 * //On lance une fléche sur l'ennemi de prédilection choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",FIREBOLT,E" + targetEnemy.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 4) { //On lance une fléche sur l'ennemi
				 * de prédilection choixStrat = "A" + hero.getOrderNumberInTeam() + ",FIREBOLT,E" +
				 * targetEnemy.getOrderNumberInTeam(); } } //fin du cas de l'archer
				 */
					
					
					
				/*
				 * //Si c'est un paladin if
				 * (hero.getFighterClass().contentEquals("PALADIN")) { if
				 * (hero.getCurrentMana() == 0) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 1) { //On ne fait rien choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",REST,A" + hero.getOrderNumberInTeam(); } else if
				 * (hero.getCurrentMana() == 2) { //Si jamais le garde ennemi existe
				 * if (guardEnemy != null) { if (guardEnemy.getCurrentMana() >= 2) { //Et qu'il
				 * a deux PA ou plus, on le charge choixStrat = "A" + hero.getOrderNumberInTeam() +
				 * ",CHARGE,E" + guardEnemy.getOrderNumberInTeam(); paladinAjoue = true; } } //Si jamais le
				 * prétre ennemi existe if (healEnemy != null) { if (healEnemy.getCurrentMana()
				 * >= 2) { //Et qu'il a deux PA ou plus, on le charge choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + healEnemy.getOrderNumberInTeam(); paladinAjoue =
				 * true; } } //Si jamais l'archer ennemi existe if (archerEnemy != null) { if
				 * (archerEnemy.getCurrentMana() >= 2) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * archerEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * //Si jamais le chaman ennemi existe if (chamanEnemy != null) { if
				 * (chamanEnemy.getCurrentMana() >= 1) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * chamanEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * if (paladinAjoue == false) { //Si jamais il n'y a aucun garde, ni prétre, ni
				 * archer on charge l'ennemi de prédilection choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + targetEnemy.getOrderNumberInTeam(); } } else if
				 * (hero.getCurrentMana() == 3) { //Si jamais le garde ennemi existe
				 * if (guardEnemy != null) { if (guardEnemy.getCurrentMana() >= 2) { //Et qu'il
				 * a deux PA ou plus, on le charge choixStrat = "A" + hero.getOrderNumberInTeam() +
				 * ",CHARGE,E" + guardEnemy.getOrderNumberInTeam(); paladinAjoue = true; } } //Si jamais le
				 * prétre ennemi existe if (healEnemy != null) { if (healEnemy.getCurrentMana()
				 * >= 2) { //Et qu'il a deux PA ou plus, on le charge choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + healEnemy.getOrderNumberInTeam(); paladinAjoue =
				 * true; } } //Si jamais l'archer ennemi existe if (archerEnemy != null) { if
				 * (archerEnemy.getCurrentMana() >= 2) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * archerEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * //Si jamais le chaman ennemi existe if (chamanEnemy != null) { if
				 * (chamanEnemy.getCurrentMana() >= 1) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * chamanEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * if (paladinAjoue == false) { //Si jamais il n'y a aucun garde, ni prétre, ni
				 * archer on charge l'ennemi de prédilection choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + targetEnemy.getOrderNumberInTeam(); } } else if
				 * (hero.getCurrentMana() == 4) { //Si jamais le garde ennemi existe
				 * if (guardEnemy != null) { if (guardEnemy.getCurrentMana() >= 1) { //Et qu'il
				 * a deux PA ou plus, on le charge choixStrat = "A" + hero.getOrderNumberInTeam() +
				 * ",CHARGE,E" + guardEnemy.getOrderNumberInTeam(); paladinAjoue = true; } } //Si jamais le
				 * prétre ennemi existe if (healEnemy != null) { if (healEnemy.getCurrentMana()
				 * >= 1) { //Et qu'il a deux PA ou plus, on le charge choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + healEnemy.getOrderNumberInTeam(); paladinAjoue =
				 * true; } } //Si jamais l'archer ennemi existe if (archerEnemy != null) { if
				 * (archerEnemy.getCurrentMana() >= 1) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * archerEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * //Si jamais le chaman ennemi existe if (chamanEnemy != null) { if
				 * (chamanEnemy.getCurrentMana() >= 1) { //Et qu'il a deux PA ou plus, on le
				 * charge choixStrat = "A" + hero.getOrderNumberInTeam() + ",CHARGE,E" +
				 * chamanEnemy.getOrderNumberInTeam(); paladinAjoue = true; } }
				 * 
				 * if (paladinAjoue == false) { //Si jamais il n'y a aucun garde, ni prétre, ni
				 * archer on charge l'ennemi de prédilection choixStrat = "A" +
				 * hero.getOrderNumberInTeam() + ",CHARGE,E" + targetEnemy.getOrderNumberInTeam(); } } } //fin
				 * du cas du paladin
				 */
					
			} else { //fin de la condition "n'est pas mort" -- if (hero.getIsDead() == false)
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
