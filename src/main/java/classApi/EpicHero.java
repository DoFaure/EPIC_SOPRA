package classApi;
import java.util.List;

import epicProjectSS.*;

public class EpicHero {
	
	String fighterClass;
	int orderNumberInTeam;
	boolean isDead;
	int maxAvailableMana;
	int maxAvailableLife;
	int currentMana;
	int currentLife;
	List<State> states;
	int fighterID;
	
	public String getFighterClass() {
		return fighterClass;
	}
	public void setFighterClass(String fighterClass) {
		this.fighterClass = fighterClass;
	}
	public int getOrderNumberInTeam() {
		return orderNumberInTeam;
	}
	public void setOrderNumberInTeam(int orderNumberInTeam) {
		this.orderNumberInTeam = orderNumberInTeam;
	}
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
	public int getMaxAvailableMana() {
		return maxAvailableMana;
	}
	public void setMaxAvailableMana(int maxAvailableMana) {
		this.maxAvailableMana = maxAvailableMana;
	}
	public int getMaxAvailableLife() {
		return maxAvailableLife;
	}
	public void setMaxAvailableLife(int maxAvailableLife) {
		this.maxAvailableLife = maxAvailableLife;
	}
	public int getCurrentMana() {
		return currentMana;
	}
	public void setCurrentMana(int currentMana) {
		this.currentMana = currentMana;
	}
	public int getCurrentLife() {
		return currentLife;
	}
	public void setCurrentLife(int currentLife) {
		this.currentLife = currentLife;
	}
	public List<State> getStates() {
		return states;
	}
	public void setStates(List<State> states) {
		this.states = states;
	}
	public int getFighterID() {
		return fighterID;
	}
	public void setFighterID(int fighterID) {
		this.fighterID = fighterID;
	}

}
