package io.vertigo.ai.predict.data.domain;

import io.vertigo.core.lang.Cardinality;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;

public final class DatasetObject implements DtObject, KeyConcept {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	@Override
	public UID<?> getUID() {
		return null;
	}

	@Field(smartType = "STyIdentifiant", type = "ID", cardinality = Cardinality.ONE, label = "identifiant de la fleur")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
