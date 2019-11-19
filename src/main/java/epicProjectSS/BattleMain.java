package epicProjectSS;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BattleMain {
	
	public static void main(String[] args) throws IOException, ParseException {

	
		Options options = new Options();
		RestAPI rest = new RestAPI();
		
		/* Notre equipe */
		String idEquipe = rest.idEquipe("test", "test");

		/* Renseigner les id equipes adverse */
		String idEquipe1 = rest.idEquipe("test", "test");
		String idEquipe2 = rest.idEquipe("test", "test");
		String idEquipe3 = rest.idEquipe("test", "test");
		String idEquipe4 = rest.idEquipe("test", "test");

		String idPartie = rest.idPartie(idEquipe);

		options.addOption("p", false, "");

		CommandLineParser parser = new DefaultParser();
		CommandLine commande = parser.parse(options, args);

		if (commande.hasOption("p")) {
			System.out.println("Test Pong : \n" + rest.testPong());

		}

		if (commande.hasOption("Reponse_API")) {

			System.out.println("Test Pong : \n" + rest.testPong());

			System.out.println("\nNotre ID Equipe : \n" + idEquipe);

			System.out.println("\nID Partie Actuelle : \n" + idPartie);

			System.out.println(
					"\nID pour une Nouvelle Partie Contre un Bot : \n" + rest.nouvellePartieBot("1", idEquipe));

			System.out.println("\nTour Equipe, Indique qui doit jouer : \n" + rest.tourEquipe(idPartie, idEquipe));

			System.out.println("\nPlateau Jeu de la partie Concerné : \n" + rest.plateauJeu(idPartie));

			System.out.println(
					"\nPlateau Jeu Equipe : \n" + rest.plateauJeuEquipe(idPartie, rest.idEquipe("test", "test")));

			System.out.println("\nDernier Coup Joué sur le Plateau : \n"
					+ rest.dernierCoupJouer(idPartie, rest.idEquipe("test", "test")));

			System.out.println("\nDonner Coup à l'adversaire : \n" + rest.donnerCoup(idPartie, idEquipe, "coup"));

			System.out.println("\nNom Adversaire : \n" + rest.nomAdversaire(idPartie, rest.idEquipe("test", "test")));
			
		}



	}
}
