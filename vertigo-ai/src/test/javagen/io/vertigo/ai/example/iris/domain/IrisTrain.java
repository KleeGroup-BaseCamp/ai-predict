package io.vertigo.ai.example.iris.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
@io.vertigo.datamodel.structure.stereotype.DataSpace("train")
public final class IrisTrain implements KeyConcept {
	private static final long serialVersionUID = 1L;

	private Long id;
	private java.math.BigDecimal sepalLength;
	private java.math.BigDecimal sepalWidth;
	private java.math.BigDecimal petalLength;
	private java.math.BigDecimal petalWidth;
	private String variety;

	/** {@inheritDoc} */
	@Override
	public UID<IrisTrain> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long id <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getId() {
		return id;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param id Long <b>Obligatoire</b>
	 */
	public void setId(final Long id) {
		this.id = id;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Sepal Lenght'.
	 * @return BigDecimal sepalLength <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDecimal", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Sepal Lenght")
	public java.math.BigDecimal getSepalLength() {
		return sepalLength;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Sepal Lenght'.
	 * @param sepalLength BigDecimal <b>Obligatoire</b>
	 */
	public void setSepalLength(final java.math.BigDecimal sepalLength) {
		this.sepalLength = sepalLength;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Sepal Width'.
	 * @return BigDecimal sepalWidth <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDecimal", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Sepal Width")
	public java.math.BigDecimal getSepalWidth() {
		return sepalWidth;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Sepal Width'.
	 * @param sepalWidth BigDecimal <b>Obligatoire</b>
	 */
	public void setSepalWidth(final java.math.BigDecimal sepalWidth) {
		this.sepalWidth = sepalWidth;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Petal Lenght'.
	 * @return BigDecimal petalLength <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDecimal", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Petal Lenght")
	public java.math.BigDecimal getPetalLength() {
		return petalLength;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Petal Lenght'.
	 * @param petalLength BigDecimal <b>Obligatoire</b>
	 */
	public void setPetalLength(final java.math.BigDecimal petalLength) {
		this.petalLength = petalLength;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Petal Width'.
	 * @return BigDecimal petalWidth <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDecimal", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Petal Width")
	public java.math.BigDecimal getPetalWidth() {
		return petalWidth;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Petal Width'.
	 * @param petalWidth BigDecimal <b>Obligatoire</b>
	 */
	public void setPetalWidth(final java.math.BigDecimal petalWidth) {
		this.petalWidth = petalWidth;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Label'.
	 * @return String variety <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Label")
	public String getVariety() {
		return variety;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Label'.
	 * @param variety String <b>Obligatoire</b>
	 */
	public void setVariety(final String variety) {
		this.variety = variety;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
