package epicProjectSS;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.GET;

public class RestAPI {
	
	/* Appel API */
	
	public String restProperties(String chaineCaractere) throws IOException {

		String filename="/Users/Jules/Documents/Developpement/Eclipse_workspace/M1_Miage_Nantes/Projet_SopraSteria_Maven/tp1_soprasteria/src/main/resources/configuration.properties";
		
		Properties properties = new Properties();
		
		FileInputStream input = new FileInputStream(filename);
		
		properties.load(input);
		return properties.getProperty("rest.base.url").concat(chaineCaractere);


	}
	

	/* Test Appel API */
	@GET
	public void testPong() {
	
		
	}
	
	/* Utilisateurs */
	
	public void idEquipe(String nomEquipe, String motDePasse) {
		
	}
	
	/* Initialisation affrontement entre joueurs */
	
	public void idPartie(Integer idEquipe) {
		
	}
	
	
	/* Initialisation affrontement contre les bots */
	
	public void nouvellePartie(Integer numberBot) {
		
	}
	
	/* Déroulement de la partie */
	
	public void tourEquipe(Integer idPartie, Integer idEquipe) {
		
	}
	
	public void plateauJeu(Integer idPartie) {
		
	}
	
	public void plateauJeuEquipe(Integer idPartie, Integer idEquipe) {
		
	}
	
	
	public void dernierCoupJouer(Integer idPartie, Integer idEquipe) {
		
	}
	
	public void donnerCoup(Integer idPartie, Integer idEquipe, String move) {
		
	}
	
	public void nomAdversaire(Integer idPartie, Integer idEquipe) {
		
	}
	
	
	
	

}
