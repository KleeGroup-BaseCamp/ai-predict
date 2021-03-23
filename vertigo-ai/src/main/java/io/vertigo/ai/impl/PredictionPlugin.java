package io.vertigo.ai.impl;

import io.vertigo.ai.predict.PredictionEntity;
import io.vertigo.core.node.component.Plugin;

public interface PredictionPlugin extends Plugin {

	PredictionEntity predict(String data);

}
