package classApi;

import java.util.List;

public class Board {

	Integer nbrTurmsLeft;
	int nbrTurnsLeft;
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
