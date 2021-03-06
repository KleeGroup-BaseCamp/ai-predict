package io.vertigo.ai.predict.standard.boston;


import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class BostonRegressionPredictionManager extends AbstractPredictionManagerTest {

	private static final String DSI_ITEM = "DsRBostonItem";
	private static final String DS_DATASET = "DsBostonDataset";
	
	@Override
	protected void doSetUp() {
		init(DSI_ITEM, DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("");
	}

}
