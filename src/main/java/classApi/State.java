package classApi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
	
	@JsonProperty("type")
	String type;

	@JsonProperty("remainingDuration")
	int remainingDuration;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRemainingDuration() {
		return remainingDuration;
	}
	public void setRemainingDuration(int remainingDuration) {
		this.remainingDuration = remainingDuration;
	}

}
