package io.vertigo.ai.withstore;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class PredictionManagerStoreTest extends AbstractPredictionManagerStoreTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(true);
	}
}
