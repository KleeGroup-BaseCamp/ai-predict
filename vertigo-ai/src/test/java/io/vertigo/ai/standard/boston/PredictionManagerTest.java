package io.vertigo.ai.standard.boston;


import io.vertigo.ai.MyNodeConfig;
import io.vertigo.ai.standard.boston.AbstractPredictionManagerTest;
import io.vertigo.core.node.config.NodeConfig;

public class PredictionManagerTest extends AbstractPredictionManagerTest {

	private static final String DSI_ITEM = "DsIBostonItem";
	private static final String DS_DATASET = "DsBostonDataset";
	
	@Override
	protected void doSetUp() {
		init(DSI_ITEM, DS_DATASET);
	}

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false);
	}

}
