package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum EventTypeTrainEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.EventTypeTrain> {

	;

	private final Serializable entityId;

	private EventTypeTrainEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.EventTypeTrain> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.EventTypeTrain.class, entityId);
	}

}
