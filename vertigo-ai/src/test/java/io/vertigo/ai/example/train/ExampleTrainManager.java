package io.vertigo.ai.example.train;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public final class ExampleTrainManager extends AbstractExampleTrainManagerTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false, true);
	}

}
