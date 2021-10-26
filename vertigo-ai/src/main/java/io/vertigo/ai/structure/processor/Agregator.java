package io.vertigo.ai.structure.processor;

import java.util.Optional;

public class Agregator {

	private AgregatorType agregatorType;
	private Optional<String> agregatorField;
	
	public Agregator(AgregatorType agregatorType) {
		super();
		this.agregatorType = agregatorType;
		this.agregatorField = Optional.empty();
	}
	public Agregator(AgregatorType agregatorType, String agregatorField) {
		super();
		this.agregatorType = agregatorType;
		this.agregatorField = Optional.of(agregatorField);
	}
	public AgregatorType getAgregatorType() {
		return agregatorType;
	}
	public void setAgregatorType(AgregatorType agregatorType) {
		this.agregatorType = agregatorType;
	}
	public Optional<String> getAgregatorField() {
		return agregatorField;
	}
	public void setAgregatorField(String agregatorField) {
		this.agregatorField = Optional.of(agregatorField);
	}
	
}