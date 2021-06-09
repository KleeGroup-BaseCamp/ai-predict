package io.vertigo.ai.train.postgresql;

import io.vertigo.ai.MyNodeConfig;
import io.vertigo.core.node.config.NodeConfig;

public class PostgresqlTrainManagerTest extends AbstractPostgresqlTrainManagerTest {

	@Override
	protected NodeConfig buildNodeConfig() {
		return MyNodeConfig.config(false, false);
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
