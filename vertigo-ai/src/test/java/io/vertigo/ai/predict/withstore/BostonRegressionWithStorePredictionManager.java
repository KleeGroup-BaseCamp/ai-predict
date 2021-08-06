package io.vertigo.ai.predict.withstore;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class BostonRegressionWithStorePredictionManager extends AbstractPredictionManagerStoreTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("");
	}
}
