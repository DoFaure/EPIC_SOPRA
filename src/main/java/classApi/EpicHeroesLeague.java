package classApi;
import java.util.List;

import epicProjectSS.*;

public class EpicHeroesLeague {

	String playerId;
	String playerName;
	List<EpicHero> fighters;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public List<EpicHero> getFighters() {
		return fighters;
	}
	public void setFighters(List<EpicHero> fighters) {
		this.fighters = fighters;
	}
	
	
	
	
	
}
