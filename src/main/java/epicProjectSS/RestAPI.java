package epicProjectSS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import classApi.Board;

/*********************************
 * 
 * @author Jules
 * 
 *         ok
 *
 */

public class RestAPI {

	/* Création du client pour l'appel API */
	private Client client = ClientBuilder.newClient();
	
	String filename = "/Users/Jules/Documents/Developpement/Eclipse_workspace/M1_Miage_Nantes/Projet_SopraSteria_Maven/EPIC_SOPRA/src/main/resources/configuration.properties";

	Properties properties = new Properties();


	public FileInputStream fileInputStream() throws FileNotFoundException {

		FileInputStream input = new FileInputStream(filename);

		return input;

	}


	/*
	 * Méthode pour les appels URL API Rest et renvoi des retours des appels
	 */

	public <T> T restProperties(Class<T> typeClass, String... args) throws IOException {
		/* On doit utiliser des asserts pour la verif des args > 0 */

		if (args.length < 1) {
			System.out.println("Error restProperties() - RestAPI : Il doit au moins avoir 1 arguments. ");
			return null;
		} else {

			String chaineCaractere = "";

			for (String arg : args) {
				chaineCaractere = chaineCaractere + "/" + arg;
		}

			/*
			 * Debug System.out.println(args.length); System.out.println(chaineCaractere);
			 */

			/* Affichae URL - Debug */

			/*
			 * System.out.println(properties.getProperty("rest.base.url").concat(
			 * chaineCaractere));
			 */

			properties.load(fileInputStream());

			return client.target(properties.getProperty("rest.base.url").concat(chaineCaractere)).request()
					.get(typeClass);
		}

	}

	/* Test Appel API */

	public String testPong() throws IOException {
		return restProperties(String.class, "ping");
	}

	/* Utilisateurs */

	public String idEquipe(String nomEquipe, String motDePasse) throws IOException {
		return restProperties(String.class, "player/getIdEquipe", nomEquipe, motDePasse);
	}

	/* Initialisation affrontement entre joueurs */

	public String idPartie(String idEquipe) throws IOException {
		return restProperties(String.class, "versus/next", idEquipe);
	}

	/* Initialisation affrontement contre les bots */

	public String nouvellePartieBot(String numberBot, String idEquipe) throws IOException {
		return restProperties(String.class, "practice/new", numberBot, idEquipe);
	}

	/* Déroulement de la partie */

	public String tourEquipe(String idPartie, String idEquipe) throws IOException {
		return restProperties(String.class, "game/status", idPartie, idEquipe);
	}

	public Board plateauJeu(String idPartie) throws IOException {
		return restProperties(Board.class, "game/board", idPartie);
	}

	/* A privilégier */
	public Board plateauJeuEquipe(String idPartie, String idEquipe) throws IOException {
		return restProperties(Board.class, "game/board", idPartie, idEquipe);
	}

	public String dernierCoupJouer(String idPartie, String idEquipe) throws IOException {
		return restProperties(String.class, "game/getlastmove", idPartie, idEquipe);
	}

	public String actionJeu(String idPartie, String idEquipe, String move) throws IOException {
		return restProperties(String.class, "game/play", idPartie, idEquipe, move);
	}

	public String nomAdversaire(String idPartie, String idEquipeAdversaire) throws IOException {
		return restProperties(String.class, "game/opponent", idPartie, idEquipeAdversaire);
	}

}