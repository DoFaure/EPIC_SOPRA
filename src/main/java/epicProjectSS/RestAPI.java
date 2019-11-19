package epicProjectSS;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class RestAPI {

	/* Création du client pour l'appel API */
	private Client client = ClientBuilder.newClient();

	/*
	 * Méthode pour les appels URL API Rest et renvoi des retours des appels
	 */

	public String restProperties(String... args) throws IOException {
		/* On doit utiliser des asserts pour la verif des args > 0 */

		if (args.length < 1) {
			return "Error restProperties() - RestAPI : Il doit au moins avoir 1 arguments. ";
		} else {

			String chaineCaractere = "";

			for (String arg : args) {
				chaineCaractere = chaineCaractere + "/" + arg;
		}

			/*
			 * Debug System.out.println(args.length); System.out.println(chaineCaractere);
			 */

		String filename = "/Users/Jules/Documents/Developpement/Eclipse_workspace/M1_Miage_Nantes/Projet_SopraSteria_Maven/EPIC_SOPRA/src/main/resources/configuration.properties";

		Properties properties = new Properties();

		FileInputStream input = new FileInputStream(filename);

		properties.load(input);

			/* Affichae URL - Debug */

			/*
			 * System.out.println(properties.getProperty("rest.base.url").concat(
			 * chaineCaractere));
			 */

			return client.target(properties.getProperty("rest.base.url").concat(chaineCaractere)).request()
					.get(String.class);
		}

	}

	/* Test Appel API */

	public String testPong() throws IOException {
		return restProperties("ping");
	}

	/* Utilisateurs */

	public String idEquipe(String nomEquipe, String motDePasse) throws IOException {
		return restProperties("player/getIdEquipe", nomEquipe, motDePasse);
	}

	/* Initialisation affrontement entre joueurs */

	public String idPartie(String idEquipe) throws IOException {
		return restProperties("versus/next", idEquipe);
	}

	/* Initialisation affrontement contre les bots */

	public String nouvellePartieBot(String numberBot, String idEquipe) throws IOException {
		return restProperties("practice/new", numberBot, idEquipe);
	}

	/* Déroulement de la partie */

	public String tourEquipe(String idPartie, String idEquipe) throws IOException {
		return restProperties("game/status", idPartie, idEquipe);
	}

	public String plateauJeu(String idPartie) throws IOException {
		return restProperties("game/board", idPartie, "?format=(JSON)");
	}

	public String plateauJeuEquipe(String idPartie, String idEquipe) throws IOException {
		return restProperties("game/board", idPartie, idEquipe, "?format=(JSON)");
	}

	public String dernierCoupJouer(String idPartie, String idEquipe) throws IOException {
		return restProperties("game/getlastmove", idPartie, idEquipe);
	}

	public String donnerCoup(String idPartie, String idEquipe, String move) throws IOException {
		return restProperties("game/play", idPartie, idEquipe, move);
	}

	public String nomAdversaire(String idPartie, String idEquipeAdversaire) throws IOException {
		return restProperties("game/opponent", idPartie, idEquipeAdversaire);
	}

}