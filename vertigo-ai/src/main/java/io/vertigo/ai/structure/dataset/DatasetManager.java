package io.vertigo.ai.structure.dataset;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface DatasetManager extends Manager{
	
	RowDefinition findFirstRowDefinitionByKeyConcept(final Class<? extends KeyConcept> keyConceptClass);
	
	DatasetDefinition findDatasetDefinition(final String dtName);
}
