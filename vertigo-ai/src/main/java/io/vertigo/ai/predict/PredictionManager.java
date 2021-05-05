package io.vertigo.ai.predict;

import java.util.HashMap;
import java.util.List;

import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.ai.train.models.AIPredictScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface PredictionManager extends Manager {

	/**
	 * Transmet des données à une API de prediction et parse la réponse.
	 */
	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version);
	
	TrainResponse train(HashMap<String,Object> config);
	
	AIPredictScoreResponse score(String model_name, Integer version);

	Integer delete(String model_name, Integer version);

}
