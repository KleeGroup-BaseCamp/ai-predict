package io.vertigo.ai.standard.boston;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.ai.data.domain.ItemDatasetLoader;
import io.vertigo.ai.data.domain.boston.BostonDatabase;
import io.vertigo.ai.data.domain.boston.BostonItem;
import io.vertigo.ai.data.domain.iris.IrisItem;
import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.definitions.DatasetItemChunk;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.DatasetManager;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.ai.datasets.models.Dataset;

public abstract class AbstractPredictionManagerTest {
    
	@Inject
	private PredictionManager predictionManager;
	
	@Inject
	private DatasetManager datasetManager;

	private AutoCloseableNode node;
	
	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//--
		doSetUp();
	}
	
	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}
	
	protected abstract void doSetUp();

	protected abstract NodeConfig buildNodeConfig();

	private DatasetItemDefinition datasetItemDefinition;
	private DatasetDefinition datasetDefinition;
	private BostonDatabase bostonDatabase;
	
	
	protected final void init(final String itemName, final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		bostonDatabase = new BostonDatabase();
		datasetItemDefinition = definitionSpace.resolve(itemName,DatasetItemDefinition.class);
		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
	}
	
	private Dataset<DatasetItem<BostonItem, BostonItem>> getBostonDataset(){
		final ItemDatasetLoader<BostonItem> loader = new ItemDatasetLoader<BostonItem>(datasetManager);
		loader.setItemDefinition(datasetItemDefinition);
		loader.bindDataBase(bostonDatabase);
		List<UID<BostonItem>> items = bostonDatabase.getAllItems()
				.stream()   
				.map(item -> item.getUID())
				.collect(Collectors.toList());
		DatasetItemChunk<BostonItem> chunk = new DatasetItemChunk<BostonItem>(items);
		Dataset<DatasetItem<BostonItem, BostonItem>> dataset = loader.loadData(chunk, "DsBostonDataset");
		return dataset;
	}
	
	private PredictResponse testPrediction(final String predictionType) {
		Dataset<DatasetItem<BostonItem, BostonItem>> dataset = getBostonDataset();
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized(), "boston-"+predictionType, 0);
		return response;
	}
	
	@Test
	public void testPredictIsolation() {
		PredictResponse response = testPrediction("isolation");
		Assertions.assertEquals(BigDecimal.ONE, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictXGBRegressor() {
		PredictResponse response = testPrediction("xgb");
		Assertions.assertEquals(BigDecimal.valueOf(24.019338607788086), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictSVR() {
		PredictResponse response = testPrediction("svr");
		Assertions.assertEquals(BigDecimal.valueOf(22.437300266590725), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictDecisionTreeReg() {
		PredictResponse response = testPrediction("dtr");
		Assertions.assertEquals(BigDecimal.valueOf(24.0), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictRandomForestReg() {
		PredictResponse response = testPrediction("rfr");
		Assertions.assertEquals(BigDecimal.valueOf(26.04799999999999), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictCustom() {
		PredictResponse response = testPrediction("custom");
		Assertions.assertEquals(BigDecimal.valueOf(22.289970501474926), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictExtraReg() {
		PredictResponse response = testPrediction("extra");
		Assertions.assertEquals(BigDecimal.valueOf(24.0), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictGradientReg() {
		PredictResponse response = testPrediction("gradient");
		Assertions.assertEquals(BigDecimal.valueOf(25.22716310264782), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictMLPReg() {
		PredictResponse response = testPrediction("mlp");
		Assertions.assertEquals(BigDecimal.valueOf(30.120559630842063), response.getPredictionList().get(0).getPredictionNumeric());
	}
}
