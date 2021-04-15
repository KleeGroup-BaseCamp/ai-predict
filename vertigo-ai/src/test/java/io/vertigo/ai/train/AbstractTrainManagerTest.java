package io.vertigo.ai.train;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.train.data.Iris;
import io.vertigo.ai.train.models.ScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.connection.SqlConnection;
import io.vertigo.database.sql.statement.SqlStatement;

public abstract class AbstractTrainManagerTest {
    
	private static final String DROP_TABLE_IRIS = "DROP TABLE iris";
	private static final String DROP_SEQUENCE_IRIS = "DROP SEQUENCE seq_iris";
	
	private static final String INSERT_INTO_IRIS_VALUES = "insert into iris values (#iris.id#, #iris.sepalLength#, #iris.sepalWidth#, #iris.petalLength#, #iris.petalWidth#, #iris.variety#)";
	private static final String  CREATE_TABLE_IRIS = "CREATE TABLE iris ( "
			+ "id 						NUMERIC(6), "
			+ "sepalLength 				NUMERIC(6,3), "
			+ "sepalWidht 				NUMERIC(6,3), "
			+ "petalLength 				NUMERIC(6,3), "
			+ "petalWidth 				NUMERIC(6,3), "
			+ "variety					VARCHAR(80), "
			+ ")";

	@Inject
	protected SqlManager dataBaseManager;
	
	@Inject
	protected PredictionManager predictionManager;
	
	private AutoCloseableNode node;
	
	protected HashMap<String,Object> config;
	
	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		config = createConfig();
		try (final SqlConnection connection = obtainMainConnection()) {
			execpreparedStatement(connection, createTableIris());
			execpreparedStatement(connection, createSequenceIris());
			if (commitRequiredOnSchemaModification()) {
				connection.commit();
			}
		}
		createDatas();
	}
	

	protected abstract boolean commitRequiredOnSchemaModification();


	protected abstract String createSequenceIris();


	protected abstract String createTableIris();


	@AfterEach
	public final void tearDown() throws Exception {
		if (node != null) {
			try (final SqlConnection connection = obtainMainConnection()) {
				// we use a shared database so we need to drop the table
				execpreparedStatement(connection, DROP_SEQUENCE_IRIS);
				execpreparedStatement(connection, DROP_TABLE_IRIS);
				if (commitRequiredOnSchemaModification()) {
					connection.commit();
				}
			} finally {
				node.close();
			}
		}
	}
	
	protected abstract NodeConfig buildNodeConfig();
	
	private HashMap<String,Object> createConfig() throws JsonParseException, JsonMappingException, IOException {
		File JSON_SOURCE = new File("./src/test/java/io/vertigo/ai/train/config.json");
		@SuppressWarnings("unchecked")
		HashMap<String,Object> config =
		        new ObjectMapper().readValue(JSON_SOURCE , HashMap.class);
		return config;
	}
	
	protected SqlConnection obtainMainConnection() {
		return dataBaseManager
				.getConnectionProvider(SqlManager.MAIN_CONNECTION_PROVIDER_NAME)
				.obtainConnection();
	}
	
	private void insert(
			final SqlConnection connection,
			final Iris iris) throws SQLException {
		//-----
		dataBaseManager
				.executeUpdate(
						SqlStatement.builder(INSERT_INTO_IRIS_VALUES)
								.bind("iris", Iris.class, iris)
								.build(),
						null,
						connection);
	}
	
	private void createDatas() throws Exception {
		try (final SqlConnection connection = obtainMainConnection()) {
			insert(connection, Iris.createIris(Long.valueOf(1), 1.0, 1.0, 1.0, 1.0, "test"));
			insert(connection, Iris.createIris(Long.valueOf(2), 1.0, 1.0, 1.0, 1.0, "test"));
			insert(connection, Iris.createIris(Long.valueOf(3), 1.0, 1.0, 1.0, 1.0, "test"));
			insert(connection, Iris.createIris(Long.valueOf(4), 1.0, 1.0, 1.0, 1.0, "test"));
			insert(connection, Iris.createIris(Long.valueOf(5), 1.0, 1.0, 1.0, 1.0, "test"));
			insert(connection, Iris.createIris(Long.valueOf(5), 1.0, 1.0, 1.0, 1.0, "test"));
			connection.commit();
		}
	}


	protected void execpreparedStatement(final SqlConnection connection, final String sql) throws SQLException {
		dataBaseManager
				.executeUpdate(SqlStatement.builder(sql).build(), null, connection);
	}
	
	@Test
	public void testTrain() {
		TrainResponse response = predictionManager.train(config);
		Assertions.assertEquals(BigDecimal.valueOf(0.9600000000000002), response.getScore());
	}
	
	@Test
	public void testScore() {
		ScoreResponse response = predictionManager.score("iris-classification-postgresql", 0);
		Assertions.assertEquals(BigDecimal.valueOf(0.9600000000000002), response.getScore());
	}

	@Test
	public void testDelete() {
		Integer response = predictionManager.delete("iris-classification-postgresql", 1);
		Assertions.assertEquals(204, response);
	}
}
