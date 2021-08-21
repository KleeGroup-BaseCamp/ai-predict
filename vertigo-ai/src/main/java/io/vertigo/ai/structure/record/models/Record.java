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

package io.vertigo.ai.structure.record.models;

import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

public final class Record<K extends KeyConcept, I extends DtObject> {

	/** Définition de du dataset. */
	private final RecordDefinition datasetDefinition;
	
	/** UID de l'objet enregistré : par convention il s'agit de l'uri de K.*/
	private final UID<K> uid;
	
	/** DtObject du dataset. */
	private final I datasetDtObject;
	
	/**
	 * Constructor.
	 * @param datasetDefinition definition de O, I
	 * @param uid UID de l'objet
	 */
	private Record(final RecordDefinition datasetDefinition, final UID<K> uid, final I datasetDtObject) {
		Assertion.check()
				.isNotNull(uid)
				.isNotNull(datasetDefinition)
				.isNotNull(datasetDtObject)
				//On vérifie la consistance des données.
				.isTrue(datasetDefinition.getKeyConceptDtDefinition().equals(uid.getDefinition()),
						"Le type de l'URI de l'objet indexé  ({0}) ne correspond pas au KeyConcept de l'index ({1})", uid.toString(), datasetDefinition.getKeyConceptDtDefinition().getName())
				.isTrue(datasetDefinition.getRecordDtDefinition().equals(DtObjectUtil.findDtDefinition(datasetDtObject)),
						"Le type l'objet indexé ({1}) ne correspond pas à celui de l'index ({1})", DtObjectUtil.findDtDefinition(datasetDtObject).getName(), datasetDefinition.getRecordDtDefinition().getName());
		//-----
		this.uid = uid;
		this.datasetDefinition = datasetDefinition;
		this.datasetDtObject = datasetDtObject;
	}
	
	/**
	 * @return Définition du dataset.
	 */
	public RecordDefinition getDefinition() {
		return datasetDefinition;
	}

	/**
	 * Récupération de l'uri de la ressource.
	 *  - Utilisé pour la récupération de highlight.
	 * @return UID de la ressource.
	 */
	public UID<K> getUID() {
		return uid;
	}

	/**
	 * Récupération de l'object contenant les champs à enregistrer.
	 * @return Objet contenant les champs à enregistrer
	 */
	public I getRecordDtObject() {
		Assertion.check().isTrue(hasIndex(), "Record n'est pas dans l'état indexable.");
		//-----
		return datasetDtObject;
	}

	/**
	 * @return Contient l'objet
	 */
	private boolean hasIndex() {
		return datasetDtObject != null;
	}

	/**
	 * Constructeur de l'Objet permettant de créer le dataset.
	 * @param <I> Type de l'objet représentant le dataset
	 * @param uid UID de l'objet enregistré
	 * @param datasetDefinition Définition de l'index de recherche.
	 * @param datasetDto  DTO représentant l'index
	 * @return  Objet permettant de créer l'index
	 */
	public static <S extends KeyConcept, I extends DtObject> Record<S, I> createDataset(final RecordDefinition datasetDefinition, final UID<S> uid, final I datasetDtObject) {
		return new Record<>(datasetDefinition, uid, datasetDtObject);
	}
}
