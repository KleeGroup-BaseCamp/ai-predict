package io.vertigo.ai.structure.record;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;


/**
 * Gestionnaire des records.
 */
public interface RecordManager extends Manager {

	/**
	 * Find RecordDefinition for a keyConcept. It must be one and only one RecordDefinition.
	 * @param keyConceptClass keyConcept class
	 * @return RecordDefinition for this keyConcept (not null)
	 */
	RecordDefinition findFirstRecordDefinitionByKeyConcept(Class<? extends KeyConcept> keyConceptClass);
	
	RecordDefinition findRecordDefinitionByKeyConcept(Class<? extends KeyConcept> keyConceptClass);

	/**
	 * Mark an uid list as dirty. Record of these elements will be re-stored.
	 * Re-storation isn't synchrone, strategy is dependant of plugin's parameters.
	 * @param keyConceptUIDs UID of keyConcept marked as dirty.
	 */
	void markAsDirty(List<UID<? extends KeyConcept>> keyConceptUIDs);

	/**
	 * Launch a complete re-storation of a record.
	 * @param datasteDefinition Type du record
	 * @return Future of number elements stored
	 */
	Future<Long> refreshAll(RecordDefinition datasteDefinition);

	/**
	 * Ajout de plusieurs ressources au record.
	 * Si les éléments étaient déjà dans le record ils sont remplacés.
	 * @param <I> Type de l'objet représentant du record
	 * @param <K> Type du keyConcept métier enregistré
	 * @param recordDefinition Type du record
	 * @param recordCollection Liste des objets à pousser dans le record
	 */
	<K extends KeyConcept, I extends DtObject> void putAll(RecordDefinition recordDefinition, Set<UID<? extends KeyConcept>> uids);

	/**
	 * Ajout d'une ressource au record.
	 * Si l'élément était déjà dans le record il est remplacé.
	 * @param <I> Type de l'objet représentant le record
	 * @param <K> Type du keyConcept métier enregistré
	 * @param recordDefinition Type du record
	 * @param record Objet à pousser dans le record
	 */
	<K extends KeyConcept, I extends DtObject> void put(RecordDefinition recordDefinition, Record<K, I> record);
	
	/**
	 * @param datasteDefinition  Type du record
	 * @return Nombre de record enregistrés
	 */
	long count(RecordDefinition recordDefinition);

	/**
	 * Suppression d'une ressource du record.
	 * @param <K> Type du keyConcept métier recordé
	 * @param recordDefinition Type du record
	 * @param uid UID de la ressource à supprimer
	 */
	<K extends KeyConcept> void remove(RecordDefinition recordDefinition, final UID<K> uid);
	
	/**
	 * Suppression des données correspondant à un filtre.
	 * @param recordDefinition Type de l'record
	 */
	void removeAll(RecordDefinition recordDefinition);

}
