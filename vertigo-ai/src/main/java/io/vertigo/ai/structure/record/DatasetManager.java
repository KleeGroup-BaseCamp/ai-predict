package io.vertigo.ai.structure.record;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public interface DatasetManager extends Manager {

	/**
	 * Find DatasetDefinition for a keyConcept. It must be one and only one DatasetDefinition.
	 * @param keyConceptClass keyConcept class
	 * @return DatasetDefinition for this keyConcept (not null)
	 */
	DatasetDefinition findFirstDatasetDefinitionByKeyConcept(Class<? extends KeyConcept> keyConceptClass);
	
	DatasetDefinition findDatasetDefinitionByKeyConcept(Class<? extends KeyConcept> keyConceptClass);

	/**
	 * Mark an uid list as dirty. Dataset of these elements will be re-stored.
	 * Re-storation isn't synchrone, strategy is dependant of plugin's parameters.
	 * @param keyConceptUIDs UID of keyConcept marked as dirty.
	 */
	void markAsDirty(List<UID<? extends KeyConcept>> keyConceptUIDs);

	/**
	 * Launch a complete re-storation of a dataset.
	 * @param datasteDefinition Type du dataset
	 * @return Future of number elements stored
	 */
	Future<Long> refreshAll(DatasetDefinition datasteDefinition);

	/**
	 * Ajout de plusieurs ressources au dataset.
	 * Si les éléments étaient déjà dans le dataset ils sont remplacés.
	 * @param <I> Type de l'objet représentant du dataset
	 * @param <K> Type du keyConcept métier enregistré
	 * @param datasetDefinition Type du dataset
	 * @param datasetCollection Liste des objets à pousser dans le dataset
	 */
	<K extends KeyConcept, I extends DtObject> void putAll(DatasetDefinition datasetDefinition, Set<UID<? extends KeyConcept>> uids);

	/**
	 * Ajout d'une ressource au dataset.
	 * Si l'élément était déjà dans le dataset il est remplacé.
	 * @param <I> Type de l'objet représentant le dataset
	 * @param <K> Type du keyConcept métier enregistré
	 * @param datasetDefinition Type du dataset
	 * @param dataset Objet à pousser dans le dataset
	 */
	<K extends KeyConcept, I extends DtObject> void put(DatasetDefinition datasetDefinition, Dataset<K, I> dataset);
	/**
	 * @param datasteDefinition  Type du dataset
	 * @return Nombre de record enregistrés
	 */
	long count(DatasetDefinition indexDefinition);

	/**
	 * Suppression d'une ressource du dataset.
	 * @param <K> Type du keyConcept métier indexé
	 * @param datasetDefinition Type du dataset
	 * @param uid UID de la ressource à supprimer
	 */
	<K extends KeyConcept> void remove(DatasetDefinition datasetDefinition, final UID<K> uid);
	
	
	/**
	 * Suppression des données correspondant à un filtre.
	 * @param indexDefinition Type de l'index
	 */
	void removeAll(DatasetDefinition indexDefinition);

}
