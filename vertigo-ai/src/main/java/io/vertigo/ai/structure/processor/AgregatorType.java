package io.vertigo.ai.structure.processor;

public enum AgregatorType {
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
	AgregatorType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
