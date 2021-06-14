package io.vertigo.ai.example.iris;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.structure.record.DatasetManager;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
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
	
	@Inject
	private IrisServices irisServices;
	
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
			//irisServices.removeIrisTrain();
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
		//Assertions.assertNotEquals(appSize, trainSize);

		//on refresh la base de train
		
		datasetManager.refreshAll(datasetDefinition).get(10, TimeUnit.SECONDS);
		
		//on attend 10s + le temps de reindexation
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionIris);
			trainSize = entityStoreManager.count(dtDefinitionIrisTrain);
		}
		Assertions.assertEquals(appSize, trainSize);

	}
	
	@Test
	public void testRefresh() throws InterruptedException, ExecutionException, TimeoutException {
		
		long appSize;
		long trainSize;
		
		datasetManager.refreshAll(datasetDefinition).get(10, TimeUnit.SECONDS);
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionIris);
			trainSize = entityStoreManager.count(dtDefinitionIrisTrain);
		}
		
		Assertions.assertEquals(appSize, trainSize);
		
		Iris newIris = new Iris();
		newIris.setPetalLength(BigDecimal.TEN);
		newIris.setPetalWidth(BigDecimal.TEN);
		newIris.setSepalLength(BigDecimal.TEN);
		newIris.setSepalWidth(BigDecimal.TEN);
		newIris.setVariety("test");
		irisServices.create(newIris);
		
		Iris firstIris = irisServices.getFirst();
		firstIris.setVariety("test");
		irisServices.update(firstIris);
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionIris);
			trainSize = entityStoreManager.count(dtDefinitionIrisTrain);
		}

		Assertions.assertEquals(appSize, trainSize);
	}
}

