package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum SeverityTypeEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.SeverityType> {

	;

	private final Serializable entityId;

	private SeverityTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.SeverityType> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.SeverityType.class, entityId);
	}

}
