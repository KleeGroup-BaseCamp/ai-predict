package io.vertigo.ai.impl;

import java.util.Collection;

import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public interface DatasetServicesPlugin extends Plugin {

	<K extends KeyConcept, I extends DtObject> void putAll(DatasetDefinition datasetDefinition, Collection<Dataset<K, I>> datasetCollection);

	<K extends KeyConcept, I extends DtObject> void put(DatasetDefinition datasetDefinition, Dataset<K, I> dataset);

	<K extends KeyConcept, I extends DtObject> void remove(DatasetDefinition datasetDefinition, UID<K> uid);

	void remove(DatasetDefinition datasetDefinition);
	
	
	long count(DatasetDefinition datasetDefinition);

}
