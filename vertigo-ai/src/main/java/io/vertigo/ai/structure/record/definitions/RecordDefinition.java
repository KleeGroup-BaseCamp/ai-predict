/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2021, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertigo.ai.structure.record.definitions;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtStereotype;

/**
 * Définition du record.
 */
@DefinitionPrefix(RecordDefinition.PREFIX)
public final class RecordDefinition extends AbstractDefinition{

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
	protected RecordDefinition(
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
