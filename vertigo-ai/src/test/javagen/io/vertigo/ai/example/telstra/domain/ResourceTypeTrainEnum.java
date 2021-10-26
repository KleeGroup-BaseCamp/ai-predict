package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum ResourceTypeTrainEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.ResourceTypeTrain> {

	;

	private final Serializable entityId;

	private ResourceTypeTrainEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.ResourceTypeTrain> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.ResourceTypeTrain.class, entityId);
	}

}
