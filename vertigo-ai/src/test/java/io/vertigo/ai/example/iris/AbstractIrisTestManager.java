package io.vertigo.ai.example.iris;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.vertigo.ai.example.iris.data.datageneration.IrisGenerator;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.example.iris.predict.IrisPredictionTest;
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.ai.structure.record.RecordManager;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public abstract class AbstractIrisTestManager {

	private RecordDefinition datasetDefinition;
	
	@Inject
	private IrisGenerator irisGenerator;
	
	@Inject
	private RecordManager datasetManager;

	@Inject
	private EntityStoreManager entityStoreManager;
	
	@Inject
	private VTransactionManager transactionManager;
	
	@Inject
	private ModelManager modelManager;
	
	@Inject
	private IrisServices irisServices;
	
	private AutoCloseableNode node;
	
	private DtDefinition dtDefinitionIris; 
	private DtDefinition dtDefinitionIrisTrain; 
	
	protected final void init(final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		datasetDefinition = definitionSpace.resolve(datasetName, RecordDefinition.class);
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
		
		waitAndExpectIndexation(appSize + 1, dtDefinitionIrisTrain);
		
		long newAppSize;
		long newTrainSize;
		IrisTrain irisTrain;
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			newAppSize = entityStoreManager.count(dtDefinitionIris);
			newTrainSize = entityStoreManager.count(dtDefinitionIrisTrain);
			
			final UID<Entity> irisTrainURI = UID.of(dtDefinitionIrisTrain, newIris.getId());
			irisTrain = (IrisTrain) entityStoreManager.readOne(irisTrainURI);
		}
		
		Assertions.assertEquals(newAppSize, newTrainSize);
		Assertions.assertEquals(appSize + 1, newAppSize);
		Assertions.assertEquals(trainSize + 1, newTrainSize);

		Iris updatedIris = irisServices.get(newIris.getId());
		updatedIris.setVariety("new value");
		irisServices.update(updatedIris);
		
		
		irisTrain.setVariety(updatedIris.getVariety());
		waitAndExpectIndexation(irisTrain, dtDefinitionIrisTrain);
		
		Iris updatedIrisApp;
		IrisTrain updatedIrisTrain;

		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			newAppSize = entityStoreManager.count(dtDefinitionIris);
			newTrainSize = entityStoreManager.count(dtDefinitionIrisTrain);
			
			final UID<Entity> irisURI = UID.of(dtDefinitionIris, newIris.getId());
			final UID<Entity> irisTrainURI = UID.of(dtDefinitionIrisTrain, newIris.getId());
			updatedIrisApp = (Iris) entityStoreManager.readOne(irisURI);
			updatedIrisTrain = (IrisTrain) entityStoreManager.readOne(irisTrainURI);
		}
		
		// We check that an updated entity keep the train db size length unchanged 
		Assertions.assertEquals(newAppSize, newTrainSize);
		Assertions.assertEquals(appSize + 1, newAppSize);
		Assertions.assertEquals(trainSize + 1, newTrainSize);
		
		// We check that an updated entity has been updated to the train db
		Assertions.assertEquals(updatedIris.getVariety(), updatedIrisApp.getVariety());
		Assertions.assertEquals(updatedIris.getVariety(), updatedIrisTrain.getVariety());
	}
	
	@Test
	public void testIrisTrain() throws JsonParseException, JsonMappingException, IOException {
		//Test Train Postgresql
		TrainResponse trainResponse = modelManager.train("iris", 2);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(trainResponse.getScore().getScoreMean()));
		
		//Test Score
		ScoreResponse scoreResponse = modelManager.score("iris", 2);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(scoreResponse.getScore().getScoreMean()));
	}
	
	@Test
	public void testIrisPredictXGB() {
		IrisPredictionTest irisPredict = new IrisPredictionTest(modelManager);
		irisPredict.testPredictXGBClassifier();
	}

	@Test
	public void testIrisPredictAll() {
		IrisPredictionTest irisPredict = new IrisPredictionTest(modelManager);
		irisPredict.testPredictAllClassifier();
	}
	
	private void waitAndExpectIndexation(final long expectedCount, DtDefinition dtDefinition) {
		waitAndExpectIndexation(expectedCount, null, dtDefinition);
	}

	private void waitAndExpectIndexation(final Entity expectedEntity, DtDefinition dtDefinition) {
		waitAndExpectIndexation(-1, expectedEntity, dtDefinition);
	}
	
	private void waitAndExpectIndexation(final long expectedCount, final Entity expectedEntity, DtDefinition dtDefinition) {
		final long time = System.currentTimeMillis();
		long size = -1;
		Entity entity = null;
		try {
			do {
				Thread.sleep(250); //wait index was done

				if (expectedEntity != null) {
					try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
						UID entityURI = expectedEntity.getUID();
						entity = entityStoreManager.readOne(entityURI);
					}
					
					if (entity.equals(expectedEntity)) {
						break; //si l'entité correspond à l'entité attendue on sort.
					}
				} else {
					try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
						size = entityStoreManager.count(dtDefinition);
					}
					
					if (size == expectedCount) {
						break; //si le nombre est atteint on sort.
					}
				}

			} while (System.currentTimeMillis() - time < 5000);//timeout 5s
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt(); //si interrupt on relance
		}
		Assertions.assertEquals(expectedCount, size);
	}
}

