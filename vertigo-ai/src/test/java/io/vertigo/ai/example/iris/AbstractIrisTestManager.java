package io.vertigo.ai.example.iris;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.structure.record.DatasetManager;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public abstract class AbstractIrisTestManager {

	private DatasetDefinition datasetDefinition;
	
	@Inject
	private IrisGenerator irisGenerator;
	
	@Inject
	private DatasetManager datasetManager;

	@Inject
	private EntityStoreManager entityStoreManager;
	
	@Inject
	private VTransactionManager transactionManager;
	
	private AutoCloseableNode node;
	
	private DtDefinition dtDefinitionIris; 
	private DtDefinition dtDefinitionIrisTrain; 
	
	protected final void init(final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
		dtDefinitionIris = DtObjectUtil.findDtDefinition(Iris.class);
		dtDefinitionIrisTrain = DtObjectUtil.findDtDefinition(IrisTrain.class);
	}
	
	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//--
		doSetUp();
		irisGenerator.createIrisFromCSV();
	}
	
	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}
	
	protected abstract void doSetUp();

	protected abstract NodeConfig buildNodeConfig();
	
	@Test
	public void testRefreshAll() throws InterruptedException, ExecutionException, TimeoutException {
		
		long appSize;
		long trainSize;
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionIris);
			trainSize = entityStoreManager.count(dtDefinitionIrisTrain);
		}
		Assertions.assertNotEquals(appSize, trainSize);

		//on refresh la base de train
		trainSize = datasetManager.refreshAll(datasetDefinition).get(10, TimeUnit.SECONDS);
		
		//on attend 10s + le temps de reindexation
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionIris);
			trainSize = entityStoreManager.count(dtDefinitionIrisTrain);
		}
		Assertions.assertEquals(appSize, trainSize);

	}
}
