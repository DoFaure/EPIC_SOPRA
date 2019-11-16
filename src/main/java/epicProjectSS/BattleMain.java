package epicProjectSS;

import java.io.IOException;

public class BattleMain {

	public static void main(String[] args) throws IOException {
		/*
		 * Options options = new Options();
		 * 
		 * options.addOption("p", "pong", false, "Print pong");
		 * options.addOption("config", "conf", false, "Print something");
		 * 
		 * CommandLineParser parser = new DefaultParser(); CommandLine cmd =
		 * parser.parse(options, args);
		 * 
		 * try { if (cmd.hasOption("p")) { System.out.println("pong"); } if
		 * (cmd.hasOption("config")) { Properties prop = new Properties();
		 * FileInputStream input = new FileInputStream(
		 * "C:\\Users\\doria\\eclipse-workspace\\epicProjectSS\\src\\main\\resources\\configuration.properties"
		 * ); prop.load(input); System.out.println(prop); }
		 * 
		 * } catch (Exception e) { // Block of code to handle errors }
		 * 
		 * RestAPI rest = new RestAPI();
		 * 
		 * rest.testPong();
		 */

		RestAPI rest = new RestAPI();

		rest.testPong();

	}

}
