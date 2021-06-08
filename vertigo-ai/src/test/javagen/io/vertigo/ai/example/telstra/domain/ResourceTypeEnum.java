package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum ResourceTypeEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.ResourceType> {

	;

	private final Serializable entityId;

	private ResourceTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.ResourceType> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.ResourceType.class, entityId);
	}

}
