package io.vertigo.ai.example.data.models;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Location implements DtObject {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String location;
	private Integer severityFault;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Integer id <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInteger", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Integer getId() {
		return id;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'ID'.
	 * @param id Integer <b>Obligatoire</b>
	 */
	public void setId(final Integer id) {
		this.id = id;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Location'.
	 * @return String location <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Location")
	public String getLocation() {
		return location;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Location'.
	 * @param location String <b>Obligatoire</b>
	 */
	public void setLocation(final String location) {
		this.location = location;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Severity Fault'.
	 * @return Integer severityFault <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInteger", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Severity Fault")
	public Integer getSeverityFault() {
		return severityFault;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Severity Fault'.
	 * @param severityFault Integer <b>Obligatoire</b>
	 */
	public void setSeverityFault(final Integer severityFault) {
		this.severityFault = severityFault;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}