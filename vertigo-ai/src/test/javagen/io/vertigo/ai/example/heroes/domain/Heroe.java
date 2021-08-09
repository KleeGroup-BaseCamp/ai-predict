package io.vertigo.ai.example.heroes.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
@io.vertigo.datamodel.structure.stereotype.DataSpace("train")
public final class Heroe implements Entity {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String job;
	private Integer faction;

	/** {@inheritDoc} */
	@Override
	public UID<Heroe> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Integer id <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInt", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Integer getId() {
		return id;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param id Integer <b>Obligatoire</b>
	 */
	public void setId(final Integer id) {
		this.id = id;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name <b>Obligatoire</b>
	 */
	@Field(smartType = "STyString", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Name")
	public String getName() {
		return name;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Name'.
	 * @param name String <b>Obligatoire</b>
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Job'.
	 * @return String job <b>Obligatoire</b>
	 */
	@Field(smartType = "STyString", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Job")
	public String getJob() {
		return job;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Job'.
	 * @param job String <b>Obligatoire</b>
	 */
	public void setJob(final String job) {
		this.job = job;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Faction ID'.
	 * @return Integer faction <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInt", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Faction ID")
	public Integer getFaction() {
		return faction;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Faction ID'.
	 * @param faction Integer <b>Obligatoire</b>
	 */
	public void setFaction(final Integer faction) {
		this.faction = faction;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
