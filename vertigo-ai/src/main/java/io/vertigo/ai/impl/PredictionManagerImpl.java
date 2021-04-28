package io.vertigo.ai.impl;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.ai.train.models.ScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class PredictionManagerImpl implements PredictionManager {
	
	private final PredictionPlugin predictionPlugin;
	
	@Inject
	public PredictionManagerImpl(PredictionPlugin predictionPlugin) {
		this.predictionPlugin = predictionPlugin;
	}


	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version) {
		return predictionPlugin.predict(data, modelName, version);
	}
	
	@Override
	public  TrainResponse train(HashMap<String, Object> config) {
		return predictionPlugin.train(config);
	}


	@Override
	public ScoreResponse score(String modelName, Integer version) {
		return predictionPlugin.score(modelName, version);
	}


	@Override
	public Integer delete(String modelName, Integer version) {
		return predictionPlugin.delete(modelName, version);
	}
	
}
