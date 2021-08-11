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
public final class DeployPredict implements DtObject {
	private static final long serialVersionUID = 1L;

	private String status;
	private io.vertigo.ai.server.models.DeployResponse response;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Status'.
	 * @return String status <b>Obligatoire</b>
	 */
	@Field(smartType = "STyStringAIResponse", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Status")
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
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Response'.
	 * @return DeployResponse response <b>Obligatoire</b>
	 */
	@Field(smartType = "STyDtDeployResponse", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Response")
	public io.vertigo.ai.server.models.DeployResponse getResponse() {
		return response;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Response'.
	 * @param response DeployResponse <b>Obligatoire</b>
	 */
	public void setResponse(final io.vertigo.ai.server.models.DeployResponse response) {
		this.response = response;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}