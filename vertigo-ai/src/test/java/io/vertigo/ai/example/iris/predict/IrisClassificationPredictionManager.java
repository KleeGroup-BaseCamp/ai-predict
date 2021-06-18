package io.vertigo.ai.example.iris.predict;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class IrisClassificationPredictionManager extends AbstractPredictionManagerTest {

	private static final String DSI_ITEM = "DsIris";
	private static final String DS_DATASET = "DsIrisDataset";
	
	@Override
	protected void doSetUp() {
		init(DSI_ITEM, DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false, false);
	}

}
