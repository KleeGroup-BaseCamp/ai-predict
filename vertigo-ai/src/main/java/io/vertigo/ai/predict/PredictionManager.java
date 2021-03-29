package io.vertigo.ai.predict;

import java.util.List;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface PredictionManager extends Manager {

	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<DatasetItem<K, D>> data);
	
	
	DatasetItemDefinition findFirstDatasetDefinitionByKeyConcept(final Class<? extends KeyConcept> keyConceptClass);
}
