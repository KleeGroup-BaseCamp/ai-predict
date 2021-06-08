package io.vertigo.ai.structure.record.definitions;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtStereotype;

@DefinitionPrefix(DatasetDefinition.PREFIX)
public final class DatasetDefinition extends AbstractDefinition{

	public static final String PREFIX = "Ds";
	
	/** Structure des éléments */
	private final DtDefinition itemDtDefinition;

	private final DtDefinition keyConceptDtDefinition;

	private final String recordLoaderId;

	/**
	 * Constructor.
	 * @param name Index name
	 * @param keyConceptDtDefinition KeyConcept associé au dataset
	 * @param itemDtDefinition Structure des items du dataset.
	 * @param datasetItemLoaderId Loader de chargement des items
	 */
	protected DatasetDefinition(
			final String name,
			final DtDefinition keyConceptDtDefinition,
			final DtDefinition itemDtDefinition,
			final String recordLoaderId) {
		super(name);
		//---
		Assertion.check()
		.isNotNull(keyConceptDtDefinition)
		.isTrue(
				keyConceptDtDefinition.getStereotype() == DtStereotype.KeyConcept,
				"keyConceptDtDefinition ({0}) must be a DtDefinition of a KeyConcept class", keyConceptDtDefinition.getName())
		.isNotNull(itemDtDefinition)
		.isNotBlank(recordLoaderId);
		//-----
		this.itemDtDefinition = itemDtDefinition;
		this.keyConceptDtDefinition = keyConceptDtDefinition;
		this.recordLoaderId = recordLoaderId;
	}
	
	/**
	 * Définition de l'objet représentant le contenu du dataset.
	 * @return Définition des champs de l'item.
	 */
	public DtDefinition getRecordDtDefinition() {
		return itemDtDefinition;
	}
	
	/**
	 * Définition du keyConcept maitre de ce dataset.
	 * Le keyConcept de l'item est surveillé pour rafraichir la bdd.
	 * @return Définition du keyConcept.
	 */
	public DtDefinition getKeyConceptDtDefinition() {
		return keyConceptDtDefinition;
	}
	
	/**
	 * Nom du composant de chargement des items.
	 * @return Nom du composant de chargement des items.
	 */
	public String getRecordLoaderId() {
		return recordLoaderId;
	}

	
}
