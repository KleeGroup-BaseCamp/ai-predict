package io.vertigo.ai.dataset;

public class Faction {
	private int id;
	private String faction;
	
	public Faction(int id, String faction) {
		setId(id);
		setFaction(faction);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setFaction(String faction) {
		this.faction = faction;
	}
	
	public String getFaction() {
		return faction;
	}

}
