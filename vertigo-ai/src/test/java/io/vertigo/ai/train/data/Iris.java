package io.vertigo.ai.train.data;

import java.io.Serializable;

import io.vertigo.core.lang.Cardinality;
import io.vertigo.datamodel.structure.stereotype.Field;

public class Iris implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Double sepallength;
	private Double sepalwidth;
	private Double petallength;
	private Double petalwidth;
	private String variety;
	
	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Longueur du pistil")
	public Double getSepallength() {
		return sepallength;
	}

	public void setSepallength(Double sepalLenght) {
		this.sepallength = sepalLenght;
	}


	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Largeur du pistil")
	public Double getSepalwidth() {
		return sepalwidth;
	}

	public void setSepalwidth(Double sepalWidth) {
		this.sepalwidth = sepalWidth;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Longueur des pétales")
	public Double getPetallength() {
		return petallength;
	}

	public void setPetallength(Double petalLenght) {
		this.petallength = petalLenght;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "Largeur des pétales")
	public Double getPetalwidth() {
		return petalwidth;
	}

	public void setPetalwidth(Double petalWidth) {
		this.petalwidth = petalWidth;
	}

	@Field(smartType = "STyIdentifiant", type = "ID", cardinality = Cardinality.ONE, label = "identifiant de la fleur")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Field(smartType = "STyLabel", cardinality = Cardinality.ONE, label = "Variété d'iris")
	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}
	
	public static Iris createIris(Long id, Double sepalLength, Double sepalWidht, Double petalLength, Double petalWidth, String variety) {
		Iris iris = new Iris();
		iris.setPetallength(petalLength);
		iris.setId(id);
		iris.setPetalwidth(petalWidth);
		iris.setSepallength(sepalLength);
		iris.setSepalwidth(sepalWidht);		
		iris.setVariety(variety);
		return iris;
	}
}
