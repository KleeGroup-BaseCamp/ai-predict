package io.vertigo.ai.impl;

import javax.inject.Inject;

import io.vertigo.ai.predict.PredictionEntity;
import io.vertigo.ai.predict.PredictionManager;

public class PredictionManagerImpl implements PredictionManager {
	
	private final PredictionPlugin predictionPlugin;
	
	@Inject
	public PredictionManagerImpl(PredictionPlugin predictionPlugin) {
		this.predictionPlugin = predictionPlugin;
	}


	@Override
	public PredictionEntity predict(String data) {
		// TODO Auto-generated method stub
		return predictionPlugin.predict(data);
	}
	
	
	
}
