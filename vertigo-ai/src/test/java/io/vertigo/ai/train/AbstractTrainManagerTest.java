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
import io.vertigo.ai.train.models.AIPredictScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.ai.utils.CSVReaderUtil;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.connection.SqlConnection;
import io.vertigo.database.sql.statement.SqlStatement;

public abstract class AbstractTrainManagerTest {
    
	private static final String DROP_TABLE_IRIS = "DROP TABLE iris";
	private static final String DROP_SEQUENCE_IRIS = "DROP SEQUENCE seq_iris";
	
	private static final String INSERT_INTO_IRIS_VALUES = "insert into iris values (#iris.id#, #iris.sepalLength#, #iris.sepalWidth#, #iris.petalLength#, #iris.petalWidth#, #iris.variety#)";
	private static final int IRIS_CSV_FILE_COLUMN_NUMBER = 5;

	@Inject
	private ResourceManager resourceManager;

	@Inject
	protected SqlManager dataBaseManager;
	
	@Inject
	protected PredictionManager predictionManager;
	
	private AutoCloseableNode node;
	
	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
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
	
	private HashMap<String,Object> createConfig(String configName) throws JsonParseException, JsonMappingException, IOException {
		File jsonSource = new File("./src/test/java/io/vertigo/ai/train/configs/"+ configName + ".json");
		HashMap<String,Object> config = new ObjectMapper().readValue(jsonSource , HashMap.class);
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
	
	private void createInitialIrisFromCSV(final String csvFilePath, final String[] irisRecord) {
		Assertion.check().isTrue(irisRecord.length == IRIS_CSV_FILE_COLUMN_NUMBER, "CSV File {0} Format not suitable for Iris", csvFilePath);
		// ---
		long id = 1;
		//FIXME: replace vertigo-database with dao/store
		try (final SqlConnection connection = obtainMainConnection()) {
			insert(connection, Iris.createIris(id++, Double.parseDouble(irisRecord[0]), Double.parseDouble(irisRecord[1]), Double.parseDouble(irisRecord[2]), Double.parseDouble(irisRecord[3]), irisRecord[4]));
			connection.commit();
		} catch (SQLException e) {
			throw WrappedException.wrap(e, "Can't insert data");
		}
		
	}
	
	
	private void createDatas() throws Exception {
		CSVReaderUtil.parseCSV(resourceManager, "io/vertigo/ai/datageneration/iris.csv", this::createInitialIrisFromCSV);
	}


	protected void execpreparedStatement(final SqlConnection connection, final String sql) throws SQLException {
		dataBaseManager
				.executeUpdate(SqlStatement.builder(sql).build(), null, connection);
	}
	
	@Test
	public void testTrainPostgresql() throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,Object> config = createConfig("postgresql");
		TrainResponse response = predictionManager.train(config);
		Assertions.assertEquals(BigDecimal.valueOf(0.9533333333333334), response.getScore().getScoreMean());
	}
	
	
	public void testTrainCassandraSpark() throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,Object> config = createConfig("cassandra_spark");
		TrainResponse response = predictionManager.train(config);
		Assertions.assertEquals(1, response.getScore().getScoreMean().compareTo(BigDecimal.valueOf(0.9)));
	}
	
	@Test
	public void testScore() {
		AIPredictScoreResponse response = predictionManager.score("iris-classification-postgresql", 0);
		Assertions.assertEquals(BigDecimal.valueOf(0.9466666666666669), response.getScore().getScoreMean());
	}

	@Test
	public void testDelete() {
		Integer response = predictionManager.delete("iris-classification-postgresql", 1);
		//predictionManager.delete("iris-classification-cassandra-spark", 1);
		Assertions.assertEquals(204, response);
	}
}
