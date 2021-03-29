package io.vertigo.ai.impl;

import java.util.List;

import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface PredictionPlugin extends Plugin {

	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<DatasetItem<K, D>> data);

}
