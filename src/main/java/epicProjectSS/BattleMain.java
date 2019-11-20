package epicProjectSS;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jeu.Orc;

public class BattleMain {
	
	public static void main(String[] args) throws IOException, ParseException {

	
		Options options = new Options();
		RestAPI rest = new RestAPI();
		
		/* Notre equipe */
		String idEquipe = rest.idEquipe("The%20Imps", "Rud3=W");

		String idPartie = rest.idPartie(idEquipe);

		options.addOption("p", false, "");
		options.addOption("Reponse_API", false, "");
		options.addOption("orc", false, "");

		CommandLineParser parser = new DefaultParser();
		CommandLine commande = parser.parse(options, args);

		if (commande.hasOption("p")) {

			/* Récuperer l'argument de p */

			String argument = commande.getOptionValue("p");

			System.out.println("Test Pong : \n" + rest.testPong());
		}

		if (commande.hasOption("Reponse_API")) {

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

			System.out.println("\nDernier Coup Joué sur le Plateau : \n"
					+ rest.dernierCoupJouer(idPartie, idEquipe));

			System.out.println("\nDonner Coup à l'adversaire : \n" + rest.donnerCoup(idPartie, idEquipe, "coup"));

			System.out.println("\nNom Adversaire : \n" + rest.nomAdversaire(idPartie, idEquipe));
			
		}

		if (commande.hasOption("orc")) {
			Orc orc = new Orc();

			System.out.println("Point de vie de l'ORC : " + orc.getMaxPV());

		}



	}
}
