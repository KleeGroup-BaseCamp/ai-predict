package io.vertigo.ai.example.iris.predict;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.structure.dataset.DatasetManagerOld;
import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.definitions.RowChunk;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.predict.data.domain.ItemDatasetLoader;
import io.vertigo.ai.predict.data.domain.iris.IrisItem;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.predict.data.domain.iris.IrisDatabase;

public abstract class AbstractPredictionManagerTest {
    
	@Inject
	private ModelManager predictionManager;
	
	@Inject
	private DatasetManagerOld datasetManager;

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

	private RowDefinition datasetItemDefinition;
	private IrisDatabase irisDatabase;
	
	
	protected final void init(final String itemName, final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		irisDatabase = new IrisDatabase();
		datasetItemDefinition = definitionSpace.resolve(itemName,RowDefinition.class);
	}
	
	private Dataset getIrisDataset(){
		final ItemDatasetLoader<IrisItem> loader = new ItemDatasetLoader<IrisItem>(datasetManager);
		loader.setItemDefinition(datasetItemDefinition);
		loader.bindDataBase(irisDatabase);
		List<UID<IrisItem>> items = irisDatabase.getAllItems()
				.stream()   
				.map(item -> item.getUID())
				.collect(Collectors.toList());
		RowChunk<IrisItem> chunk = new RowChunk<IrisItem>(items, IrisItem.class);
		Dataset dataset = loader.loadData(chunk, "DsIrisDataset");
		return dataset;
	}

	private PredictResponse testPrediction(final String predictionType) {
		Dataset dataset = getIrisDataset();
		PredictResponse response = predictionManager.predict(dataset.collect(), "iris-"+predictionType, 0);
		return response;
	}
	
	@Test
	public void testPredictClassification() {
		PredictResponse response = testPrediction("classification");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictClustering() {
		PredictResponse response = testPrediction("clustering");
		Assertions.assertEquals(BigDecimal.ONE, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictKNN() {
		PredictResponse response = testPrediction("knn");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictSVC() {
		PredictResponse response = testPrediction("svc");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictXGBClassifier() {
		PredictResponse response = testPrediction("xgb");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictLogistic() {
		PredictResponse response = testPrediction("logistic");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictKeras() {
		PredictResponse response = testPrediction("keras");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictGradient() {
		PredictResponse response = testPrediction("gradient");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictExtra() {
		PredictResponse response = testPrediction("extra");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictQDAr() {
		PredictResponse response = testPrediction("qda");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictLDA() {
		PredictResponse response = testPrediction("lda");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictMLP() {
		PredictResponse response = testPrediction("mlp");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
}
