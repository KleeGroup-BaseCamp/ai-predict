package io.vertigo.ai.standard.iris;

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
import io.vertigo.ai.data.domain.iris.IrisItem;
import io.vertigo.ai.data.domain.iris.IrisDatabase;
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
	private IrisDatabase irisDatabase;
	
	
	protected final void init(final String itemName, final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		irisDatabase = new IrisDatabase();
		datasetItemDefinition = definitionSpace.resolve(itemName,DatasetItemDefinition.class);
		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
	}
	
	private Dataset<DatasetItem<IrisItem, IrisItem>> getIrisDataset(){
		final ItemDatasetLoader<IrisItem> loader = new ItemDatasetLoader<IrisItem>(datasetManager);
		loader.setItemDefinition(datasetItemDefinition);
		loader.bindDataBase(irisDatabase);
		List<UID<IrisItem>> items = irisDatabase.getAllItems()
				.stream()   
				.map(item -> item.getUID())
				.collect(Collectors.toList());
		DatasetItemChunk<IrisItem> chunk = new DatasetItemChunk<IrisItem>(items);
		Dataset<DatasetItem<IrisItem, IrisItem>> dataset = loader.loadData(chunk, "DsIrisDataset");
		return dataset;
	}

	private PredictResponse testPrediction(final String predictionType) {
		Dataset<DatasetItem<IrisItem, IrisItem>> dataset = getIrisDataset();
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized(), "iris-"+predictionType, 0);
		return response;
	}
	
	@Test
	public void testPredictClassification() {
		PredictResponse response = testPrediction("classification");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictClustering() {
		PredictResponse response = testPrediction("clustering");
		Assertions.assertEquals(BigDecimal.ONE, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictKNN() {
		PredictResponse response = testPrediction("knn");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictSVC() {
		PredictResponse response = testPrediction("svc");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictXGBClassifier() {
		PredictResponse response = testPrediction("xgb");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictLogistic() {
		PredictResponse response = testPrediction("logistic");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictKeras() {
		PredictResponse response = testPrediction("keras");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictGradient() {
		PredictResponse response = testPrediction("gradient");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictExtra() {
		PredictResponse response = testPrediction("extra");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictQDAr() {
		PredictResponse response = testPrediction("qda");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictLDA() {
		PredictResponse response = testPrediction("lda");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	@Test
	public void testPredictMLP() {
		PredictResponse response = testPrediction("mlp");
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
}
