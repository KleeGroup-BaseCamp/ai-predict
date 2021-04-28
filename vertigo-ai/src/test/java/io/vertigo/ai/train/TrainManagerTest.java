package io.vertigo.ai.train;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class TrainManagerTest extends AbstractTrainManagerTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false);
	}

	@Override
	protected boolean commitRequiredOnSchemaModification() {
		return true;
	}

	@Override
	protected String createSequenceIris() {
		return "CREATE SEQUENCE seq_iris";
	}

	@Override
	protected String createTableIris() {
		final String myString = "CREATE TABLE iris ( "
				+ "id 						NUMERIC(6), "
				+ "sepal_length 			NUMERIC(6,3), "
				+ "sepal_width 				NUMERIC(6,3), "
				+ "petal_length				NUMERIC(6,3), "
				+ "petal_width 				NUMERIC(6,3), "
				+ "variety					VARCHAR(80)"
				+ ")";
		return myString;
	}

}

