package io.vertigo.ai.structure.processor;

public enum Agregator {
	/*
	 * Agregation type for group by processor
	 */
	
	COUNT ("count"),
	MIN ("min"),
	MAX ("max"),
	SUM ("sum"),
	AVG ("avg");

	private String type;
	
	/**
	 * Constructor
	 * @param type agregation type
	 */
	Agregator(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
