package io.vertigo.ai.predict;

import io.vertigo.core.node.component.Manager;

public interface PredictionManager extends Manager{

	PredictionEntity predict(String data);
}
