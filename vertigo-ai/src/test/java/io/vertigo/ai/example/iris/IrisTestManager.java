package io.vertigo.ai.example.iris;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class IrisTestManager extends AbstractIrisTestManager {

	private static final String DS_DATASET = "DsIris";
	@Override
	protected void doSetUp() {
		init(DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("iris");
	}

}
