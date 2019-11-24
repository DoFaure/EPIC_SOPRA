package epicProjectSS;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/************************************
 * 
 * @author Jules
 *
 *         ok
 *
 */

public class BattleMain {
	
	public static void main(String[] args) throws IOException {

		Options options = new Options();
		RestAPI rest = new RestAPI();
		MoteurJeu moteurJeu = new MoteurJeu();
		
		options.addOption("b", true, "Pratice");
		options.addOption("m", false, "Versus");
		options.addOption("Reponse_API", false, "Test");

		CommandLineParser parser = new DefaultParser();
		CommandLine commande;

		try {

			commande = parser.parse(options, args);

			if (commande.hasOption("b")) {
				String argument = commande.getOptionValue("b");
				moteurJeu.newPratice(argument);
			}

			if (commande.hasOption("m")) {

				moteurJeu.newVersus();

			}

			if (commande.hasOption("Reponse_API")) {

				String idEquipe = rest.idEquipe("The%20Imps", "Rud3=W");
				String idPartie = rest.idPartie(idEquipe);

				System.out.println("Test Pong : \n" + rest.testPong());

				System.out.println("\nNotre ID Equipe : \n" + idEquipe);

				System.out.println("\nID Partie Actuelle : \n" + idPartie);

				System.out.println(
						"\nID pour une Nouvelle Partie Contre un Bot : \n" + rest.nouvellePartieBot("1", idEquipe));

				System.out.println("\nTour Equipe, Indique qui doit jouer : \n" + rest.tourEquipe(idPartie, idEquipe));

				System.out.println("\nPlateau Jeu de toute les équipes : \n" + rest.plateauJeu(idPartie));

				/* A privilégier */
				System.out.println(
						"\nPlateau Jeu Equipe de la partie Concerné : \n" + rest.plateauJeuEquipe(idPartie, idEquipe));

				System.out
						.println("\nDernier Coup Joué sur le Plateau : \n" + rest.dernierCoupJouer(idPartie, idEquipe));

				System.out.println("\nDonner Coup à l'adversaire : \n" + rest.actionJeu(idPartie, idEquipe, "ORC"));

				System.out.println("\nNom Adversaire : \n" + rest.nomAdversaire(idPartie, idEquipe));

			}

		} catch (ParseException b) {
			// TODO Auto-generated catch block
			System.out.println("Erreur - Battle Main - Arguments saisi non présent dans Battle Main");
		}


	}
}
