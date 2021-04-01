package io.vertigo.ai.withstore;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import io.vertigo.ai.datasetItems.definitions.DatasetItemChunk;
import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.DatasetManager;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.ai.datasets.models.Dataset;
import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.ai.data.domain.iris.IrisItem;
import io.vertigo.ai.data.domain.boston.BostonDatabase;
import io.vertigo.ai.data.domain.boston.BostonItem;
import io.vertigo.ai.data.domain.boston.BostonRegressionDatabase;
import io.vertigo.ai.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.data.domain.iris.IrisDatabase;
import io.vertigo.ai.withstore.ItemDatasetStoreLoader;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.connection.SqlConnection;
import io.vertigo.database.sql.statement.SqlStatement;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public abstract class AbstractPredictionManagerStoreTest {

	@Inject
	private SqlManager dataBaseManager;
	@Inject
	private EntityStoreManager entityStoreManager;
	@Inject
	private VTransactionManager transactionManager;
	@Inject
	private PredictionManager predictionManager;
	@Inject
	private DatasetManager datasetManager;
	@Inject
	private TaskManager taskManager;
	//Item
	private static final String DSI_ITEM = "DsIBostonRegressionItem";
	//Dataset
	private static final String DS_DATASET = "DsBostonRegressionDataset";
	
	private DatasetDefinition datasetDefinition;
	
	private DatasetItemDefinition datasetItemDefinition;

	private long initialDbItemSize = 0;

	private AutoCloseableNode node;
	
	private List<UID<BostonRegressionItem>> uids;
	
	protected abstract NodeConfig buildNodeConfig();

	@BeforeEach
	public final void setUp() throws SQLException {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//---
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();
		datasetDefinition = definitionSpace.resolve(DS_DATASET, DatasetDefinition.class);
		datasetItemDefinition = definitionSpace.resolve(DSI_ITEM, DatasetItemDefinition.class);
		//A chaque test on recrée la table famille
		try (final SqlConnectionCloseable connectionCloseable = new SqlConnectionCloseable(dataBaseManager)) {
			execCallableStatement(connectionCloseable.getConnection(), "create table boston_regression_item(ID BIGINT, RM NUMERIC(4,3));");
			execCallableStatement(connectionCloseable.getConnection(), "create sequence SEQ_BOSTON_REGRESSION_ITEM start with 10001 increment by 1");
		} 

		final BostonRegressionDatabase itemDatabase = new BostonRegressionDatabase();
		initialDbItemSize = itemDatabase.size();
		
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			uids = itemDatabase.getAllUIDs();
			for (final BostonRegressionItem item : itemDatabase.getAllItems()) {
				item.setId(null);
				entityStoreManager.create(item);
			}
			transaction.commit();
		};
	}

	@AfterEach
	public final void tearDown() throws SQLException {
		if (node != null) {
			//A chaque fin de test on arréte la base.
			try (final SqlConnectionCloseable connectionCloseable = new SqlConnectionCloseable(dataBaseManager)) {
				execCallableStatement(connectionCloseable.getConnection(), "shutdown;");
			} finally {
				node.close();
			}
		}
	}

	private void execCallableStatement(final SqlConnection connection, final String sql) throws SQLException {
		dataBaseManager.executeUpdate(
				SqlStatement.builder(sql).build(),
				Collections.emptyMap(),
				connection);
	}


	private class SqlConnectionCloseable implements AutoCloseable {
		private final SqlConnection connection;

		SqlConnectionCloseable(final SqlManager dataBaseManager) {
			connection = dataBaseManager.getConnectionProvider(SqlManager.MAIN_CONNECTION_PROVIDER_NAME).obtainConnection();
		}

		SqlConnection getConnection() {
			return connection;
		}

		public void commit() throws SQLException {
			connection.commit();
		}

		@Override
		public void close() throws SQLException {
			connection.close();
		}
	}
	
	@Test
	public void testPredictWithStore() {
		final ItemDatasetStoreLoader loader = new ItemDatasetStoreLoader(taskManager, datasetManager, transactionManager);
		DatasetItemChunk<BostonRegressionItem> chunk = new DatasetItemChunk<BostonRegressionItem>(uids);
		Dataset<DatasetItem<BostonRegressionItem, BostonRegressionItem>> dataset = loader.loadData(chunk, DS_DATASET);
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized(), "boston-regression", 0);
		Assertions.assertEquals(BigDecimal.valueOf(25.175095544577786), response.getPredictionList().get(0).getPredictionVector().get(0));

	}
}
