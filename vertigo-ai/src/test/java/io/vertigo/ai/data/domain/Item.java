package io.vertigo.ai.data.domain;

import io.vertigo.core.lang.Cardinality;

import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;

public final class Item implements KeyConcept {


	private static final long serialVersionUID = 1L;

	private Long id;
	private Double sepalLength;
	private Double sepalWidth;
	private Double petalLength;
	private Double petalWidth;
	
	@Override
	public UID<Item> getUID() {
		return UID.of(this);
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Longueur du pistil")
	public Double getSepalLength() {
		return sepalLength;
	}

	public void setSepalLength(Double sepalLenght) {
		this.sepalLength = sepalLenght;
	}


	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Largeur du pistil")
	public Double getSepalWidth() {
		return sepalWidth;
	}

	public void setSepalWidth(Double sepalWidth) {
		this.sepalWidth = sepalWidth;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Longueur des pétales")
	public Double getPetalLength() {
		return petalLength;
	}

	public void setPetalLength(Double petalLenght) {
		this.petalLength = petalLenght;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Largeur des pétales")
	public Double getPetalWidth() {
		return petalWidth;
	}

	public void setPetalWidth(Double petalWidth) {
		this.petalWidth = petalWidth;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("{\"sepalWidth\":");
		res.append(sepalWidth);
		res.append(", ");
		res.append("\"sepalLength\":");
		res.append(sepalLength);
		res.append(", ");
		res.append("\"petalWidth\":");
		res.append(sepalWidth);
		res.append(", ");
		res.append("\"petalLength\":");
		res.append(petalLength);
		res.append("}");
		return res.toString();
	}

	@Field(smartType = "STyIdentifiant", type = "ID", cardinality = Cardinality.ONE, label = "identifiant de la fleur")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
