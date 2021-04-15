package io.vertigo.ai.train.data;

import io.vertigo.core.lang.Cardinality;
import io.vertigo.datamodel.structure.stereotype.Field;

public class Iris {

	private Long id;
	private Double sepalLength;
	private Double sepalWidth;
	private Double petalLength;
	private Double petalWidth;
	private String variety;
	
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
		iris.setPetalLength(petalLength);
		iris.setId(id);
		iris.setPetalWidth(petalWidth);
		iris.setSepalLength(sepalLength);
		iris.setSepalWidth(sepalWidht);		
		iris.setVariety(variety);
		return iris;
	}
}
