package io.vertigo.ai;

import io.vertigo.ai.impl.PredictionManagerImpl;
import io.vertigo.ai.plugins.AIPredictPluginImpl;
import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.core.node.config.Feature;
import io.vertigo.core.node.config.Features;
import io.vertigo.core.param.Param;

public class AIFeatures extends Features<AIFeatures>{

	protected AIFeatures() {
		super("vertigo-ai");
	}

	@Override
	protected void buildFeatures() {
		getModuleConfigBuilder().addComponent(PredictionManager.class, PredictionManagerImpl.class);		
	}
	

	@Feature("predict.AIPredict")
	public AIFeatures withAIPredictBackend(final Param... params) {
		getModuleConfigBuilder().addPlugin(AIPredictPluginImpl.class, params);
		return this;
		
	}
}
