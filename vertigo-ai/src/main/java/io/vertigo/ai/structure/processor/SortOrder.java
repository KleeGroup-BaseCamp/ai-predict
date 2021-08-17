package io.vertigo.ai.structure.processor;

public enum SortOrder {

	ASC ("ASC"),
	DESC ("DESC");
	
	private String order;
	
	
	SortOrder(String order) {
		this.order = order;
	}


	public String getValue() {
		return order;
	}
}
