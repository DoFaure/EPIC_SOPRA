package epicProjectSS;

import java.io.IOException;

import classApi.Board;
import classApi.EpicHeroesLeague;

public class MoteurJeu {

	RestAPI rest = new RestAPI();

	public MoteurJeu() {

	}

	public void Pratice() throws IOException {


		String idEquipe = rest.idEquipe("test", "test");
		String idPartie = rest.idPartie(idEquipe);
		
		if ((idEquipe == "")) {
			System.out.println("Pas d'accès à l'ID Equipe");
		} else {

		}
		
		Board board = rest.plateauJeuEquipe(idPartie, idEquipe);
		EpicHeroesLeague heros = board.getPlayerBoards().get(0);

	}

	public void newPartie(String idPartie) {

	}

}
