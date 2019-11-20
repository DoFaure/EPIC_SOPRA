package epicProjectSS;

import java.io.IOException;

public class MoteurJeu {

	RestAPI rest = new RestAPI();

	public MoteurJeu() {

	}

	public void Pratice() throws IOException {
		String idEquipe = rest.idEquipe("test", "test");
		
		if ((idEquipe.isEmpty())) {

		}
	}

}
