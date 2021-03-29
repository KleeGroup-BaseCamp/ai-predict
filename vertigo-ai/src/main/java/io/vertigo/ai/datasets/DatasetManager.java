package io.vertigo.ai.datasets;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface DatasetManager extends Manager{
	
	DatasetItemDefinition findFirstDatasetItemDefinitionByKeyConcept(final Class<? extends KeyConcept> keyConceptClass);
	
	DatasetDefinition findDatasetDefinition(final String dtName);
}
