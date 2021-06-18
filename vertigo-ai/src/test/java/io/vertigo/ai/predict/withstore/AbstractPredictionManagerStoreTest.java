package io.vertigo.ai.predict.withstore;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.definitions.RowChunk;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.predict.data.domain.boston.BostonRegressionDatabase;
import io.vertigo.ai.predict.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.connection.SqlConnection;
import io.vertigo.database.sql.statement.SqlStatement;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
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
	private ModelManager predictionManager;
	@Inject
	private DatasetManager datasetManager;
	@Inject
	private TaskManager taskManager;
	//Dataset
	private static final String DS_DATASET = "DsBostonRegressionDataset";
	
	


	private AutoCloseableNode node;
	
	private List<UID<BostonRegressionItem>> uids;
	
	protected abstract NodeConfig buildNodeConfig();

	@BeforeEach
	public final void setUp() throws SQLException {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//---
		//A chaque test on recrée la table famille
		try (final SqlConnectionCloseable connectionCloseable = new SqlConnectionCloseable(dataBaseManager)) {
			execCallableStatement(connectionCloseable.getConnection(), "create table boston_regression_item(ID BIGINT, RM NUMERIC(4,3));");
			execCallableStatement(connectionCloseable.getConnection(), "create sequence SEQ_BOSTON_REGRESSION_ITEM start with 10001 increment by 1");
		} 

		final BostonRegressionDatabase itemDatabase = new BostonRegressionDatabase();
		
		
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			uids = new ArrayList<>();
			for (final BostonRegressionItem item : itemDatabase.getAllItems()) {
				item.setId(null);
				entityStoreManager.create(item);
				final DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(item);
				uids.add(UID.of(dtDefinition, DtObjectUtil.getId(item)));
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

		@Override
		public void close() throws SQLException {
			connection.close();
		}
	}
	
	@Test
	public void testPredictWithStore() {
		final ItemDatasetStoreLoader loader = new ItemDatasetStoreLoader(taskManager, datasetManager, transactionManager);
		RowChunk<BostonRegressionItem> chunk = new RowChunk<BostonRegressionItem>(uids, BostonRegressionItem.class);
		Dataset dataset = loader.loadData(chunk, DS_DATASET);
		PredictResponse response = predictionManager.predict(dataset.collect(), "boston-regression", 0);
		Assertions.assertEquals(BigDecimal.valueOf(25.175095544577786), response.getPredictionList().get(0).getPredictionVector().get(0));

	}
}
