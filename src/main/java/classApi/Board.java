package classApi;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import epicProjectSS.*;

public class Board {

		@JsonProperty("playerId")
		int nbrTurnsLeft;
		@JsonProperty("playerBoards")
		List<EpicHeroesLeague> playerBoards;
		
		public int getNbrTurnsLeft() {
			return nbrTurnsLeft;
		}
		public void setNbrTurnsLeft(int nbrTurnsLeft) {
			this.nbrTurnsLeft = nbrTurnsLeft;
		}
		public List<EpicHeroesLeague> getPlayerBoards() {
			return playerBoards;
		}
		public void setPlayerBoards(List<EpicHeroesLeague> playerBoards) {
			this.playerBoards = playerBoards;
		}
}
