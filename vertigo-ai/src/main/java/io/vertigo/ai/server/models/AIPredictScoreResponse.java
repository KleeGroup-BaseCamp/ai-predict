package io.vertigo.ai.server.models;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class AIPredictScoreResponse implements DtObject {
	private static final long serialVersionUID = 1L;

	private String modelName;
	private Integer version;
	private io.vertigo.ai.server.models.ScoreResponse score;
	private Integer time;
	private String status;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model name'.
	 * @return String modelName <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Model name")
	public String getModelName() {
		return modelName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model name'.
	 * @param modelName String <b>Obligatoire</b>
	 */
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model version'.
	 * @return Integer version <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInteger", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Model version")
	public Integer getVersion() {
		return version;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model version'.
	 * @param version Integer <b>Obligatoire</b>
	 */
	public void setVersion(final Integer version) {
		this.version = version;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model score'.
	 * @return BigDecimal score <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDtScoreResponse", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Model score")
	public io.vertigo.ai.server.models.ScoreResponse getScore() {
		return score;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model score'.
	 * @param score BigDecimal <b>Obligatoire</b>
	 */
	public void setScore(final io.vertigo.ai.server.models.ScoreResponse score) {
		this.score = score;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Request duration'.
	 * @return Integer time <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInteger", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Request duration")
	public Integer getTime() {
		return time;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Request duration'.
	 * @param time Integer <b>Obligatoire</b>
	 */
	public void setTime(final Integer time) {
		this.time = time;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Status'.
	 * @return String status <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Status")
	public String getStatus() {
		return status;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Status'.
	 * @param status String <b>Obligatoire</b>
	 */
	public void setStatus(final String status) {
		this.status = status;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}