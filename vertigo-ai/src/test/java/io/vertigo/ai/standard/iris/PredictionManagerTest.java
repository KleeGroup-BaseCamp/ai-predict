package io.vertigo.ai.standard.iris;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class PredictionManagerTest extends AbstractPredictionManagerTest {

	private static final String DSI_ITEM = "DsIIrisItem";
	private static final String DS_DATASET = "DsIrisDataset";
	
	@Override
	protected void doSetUp() {
		init(DSI_ITEM, DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false);
	}

}
