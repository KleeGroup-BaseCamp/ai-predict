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

package io.vertigo.ai.impl.structure.record;

import java.util.Collection;

import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Plugin offrant des services de synchronisation et de manipulation de records.
 */
public interface RecordServicesPlugin extends Plugin {

	/**
	 * Ajout de plusieurs ressources à la base d'entrainement
	 * @param <K> Type de l'objet métier
	 * @param <I> Type du keyConcept d'entrainement
	 * @param recordDefinition Type du record
	 * @param recordCollection Liste des objets à pousser dans la base de données d'entrainement
	 */
	<K extends KeyConcept, I extends DtObject> void putAll(RecordDefinition recordDefinition, Collection<Record<K, I>> recordCollection);

	/**
	 * Ajout d'une ressource à la base d'entrainement
	 * @param <K> Type de l'objet métier
	 * @param <I> Type du keyConcept d'entrainement
	 * @param recordDefinition Type du record
	 * @param recordCollection Objet à pousser dans la base de données d'entrainement
	 */
	<K extends KeyConcept, I extends DtObject> void put(RecordDefinition recordDefinition, Record<K, I> record);

	/**
	 * Retire une ressource de la base d'entrainement
	 * @param <K> Type de l'objet métier
	 * @param <I> Type du keyConcept d'entrainement
	 * @param recordDefinition Type du record
	 * @param uid UID de l'objet à supprimer
	 */
	<K extends KeyConcept, I extends DtObject> void remove(RecordDefinition recordDefinition, UID<K> uid);

	/**
	 * @param recordDefinition Type du record
	 */
	void remove(RecordDefinition recordDefinition);

	/**
	 * Retire une ressource de la base d'entrainement
	 * @param <K> Type de l'objet métier
	 * @param <I> Type du keyConcept d'entrainement
	 * @param recordDefinition Type du record
	 * @param uid UID de l'objet à supprimer
	 */
	long count(RecordDefinition recordDefinition);

}
