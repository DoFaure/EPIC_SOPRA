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

	public void restProperties(String... args) throws IOException {
		/* On doit utiliser des asserts pour la verif des args > 0 */

		if (args.length < 1) {
			System.out.println("Error restProperties() - RestAPI : Il doit au moins avoir 1 arguments. ");
		} else {

		String chaineCaractere = "/";

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

		System.out.println(client.target(properties.getProperty("rest.base.url").concat(chaineCaractere)).request()
				.get(String.class));
		}

	}

	/* Test Appel API */

	public void testPong() throws IOException {
		restProperties("ping");
	}

	/* Utilisateurs */

	public void idEquipe(String nomEquipe, String motDePasse) throws IOException {
		restProperties(nomEquipe, motDePasse);
	}

	/* Initialisation affrontement entre joueurs */

	public void idPartie(String idEquipe) throws IOException {
		restProperties(idEquipe);
	}

	/* Initialisation affrontement contre les bots */

	public void nouvellePartie(String numberBot) throws IOException {
		restProperties(numberBot);
	}

	/* Déroulement de la partie */

	public void tourEquipe(String idPartie, String idEquipe) throws IOException {
		restProperties(idPartie, idEquipe);
	}

	public void plateauJeu(String idPartie) throws IOException {
			restProperties(idPartie);
	}

	public void plateauJeuEquipe(String idPartie, String idEquipe) throws IOException {
		restProperties(idPartie, idEquipe);
	}

	public void dernierCoupJouer(String idPartie, String idEquipe) throws IOException {
		restProperties(idPartie, idEquipe);
	}

	public void donnerCoup(String idPartie, String idEquipe, String coup) throws IOException {
		restProperties(idPartie, idEquipe, coup);
	}

	public void nomAdversaire(String idPartie, String idEquipe) throws IOException {
		restProperties(idPartie, idEquipe);
	}

}
