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

		options.addOption("p", false, "");

		CommandLineParser parser = new DefaultParser();
		CommandLine commande = parser.parse(options, args);

		if (commande.hasOption("p")) {
			rest.testPong();
			/* rest.idEquipe("{test}", "{test}"); */
		}

	}

}
