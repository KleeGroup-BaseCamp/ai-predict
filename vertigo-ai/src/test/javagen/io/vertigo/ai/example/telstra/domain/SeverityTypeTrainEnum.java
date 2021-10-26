package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum SeverityTypeTrainEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.SeverityTypeTrain> {

	;

	private final Serializable entityId;

	private SeverityTypeTrainEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.SeverityTypeTrain> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.SeverityTypeTrain.class, entityId);
	}

}
