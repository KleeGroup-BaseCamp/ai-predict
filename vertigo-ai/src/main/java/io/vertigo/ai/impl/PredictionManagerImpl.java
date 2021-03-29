package io.vertigo.ai.impl;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class PredictionManagerImpl implements PredictionManager {
	
	private final PredictionPlugin predictionPlugin;
	
	@Inject
	public PredictionManagerImpl(PredictionPlugin predictionPlugin) {
		this.predictionPlugin = predictionPlugin;
	}


	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data) {
		return predictionPlugin.predict(data);
	}	
	
}
