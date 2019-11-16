package epicProjectSS;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BattleMain {
	public static void main(String[] args) throws ParseException {
		
		Options options = new Options();	
		
		options.addOption("p", "pong", false, "Print pong");
		options.addOption("config", "conf", false, "Print something");
		
		 CommandLineParser parser = new DefaultParser();
	     CommandLine cmd = parser.parse( options, args);

		try {

				if(cmd.hasOption("p")) {
					System.out.println("pong");
				}
				if(cmd.hasOption("config")) {
					Properties prop = new Properties();
					FileInputStream input = new FileInputStream("C:\\Users\\doria\\eclipse-workspace\\epicProjectSS\\src\\main\\resources\\configuration.properties");
					prop.load(input);
					System.out.println(prop);
				}
			
		}catch(Exception e) {
			  //  Block of code to handle errors
		}
		
	
		
	}
	

}

