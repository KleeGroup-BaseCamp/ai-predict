package io.vertigo.ai.train.sparkCassandra;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class SparkCassandraTrainManagerTest extends AbstractSparkCassandraTrainManagerTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config("");
	}

	@Override
	protected boolean commitRequiredOnSchemaModification() {
		return true;
	}

}

