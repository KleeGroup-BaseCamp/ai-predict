package io.vertigo.ai.impl;

import java.util.HashMap;
import java.util.List;

import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.ai.train.models.AIPredictScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface PredictionPlugin extends Plugin {

	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version);
	
	TrainResponse train(HashMap<String,Object> config);
	
	AIPredictScoreResponse score(String model_name, Integer version);

	Integer delete(String modelName, Integer version);

}
