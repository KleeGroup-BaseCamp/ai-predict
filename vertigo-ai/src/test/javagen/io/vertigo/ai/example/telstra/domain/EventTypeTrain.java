package io.vertigo.ai.example.telstra.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtStaticMasterData;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
@io.vertigo.datamodel.structure.stereotype.DataSpace("train")
public final class EventTypeTrain implements DtStaticMasterData {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer code;
	private String eventType;

	/** {@inheritDoc} */
	@Override
	public UID<EventTypeTrain> getUID() {
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
	 * Récupère la valeur de la propriété 'Code'.
	 * @return Integer code
	 */
	@Field(smartType = "STyInteger", label = "Code")
	public Integer getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Code'.
	 * @param code Integer
	 */
	public void setCode(final Integer code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Event Type'.
	 * @return String eventType <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Event Type")
	public String getEventType() {
		return eventType;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Event Type'.
	 * @param eventType String <b>Obligatoire</b>
	 */
	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
