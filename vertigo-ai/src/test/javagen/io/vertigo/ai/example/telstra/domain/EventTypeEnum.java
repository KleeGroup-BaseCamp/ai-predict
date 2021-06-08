package io.vertigo.ai.example.telstra.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum EventTypeEnum implements MasterDataEnum<io.vertigo.ai.example.telstra.domain.EventType> {

	;

	private final Serializable entityId;

	private EventTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.ai.example.telstra.domain.EventType> getEntityUID() {
		return UID.of(io.vertigo.ai.example.telstra.domain.EventType.class, entityId);
	}

}
