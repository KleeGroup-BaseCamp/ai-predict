package io.vertigo.ai.predict.standard.boston;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.structure.dataset.DatasetManagerOld;
import io.vertigo.ai.structure.dataset.models.DatasetOpeOld;
import io.vertigo.ai.structure.row.definitions.RowChunk;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.predict.data.domain.ItemDatasetLoader;
import io.vertigo.ai.predict.data.domain.boston.BostonDatabase;
import io.vertigo.ai.predict.data.domain.boston.BostonItem;
import io.vertigo.ai.server.models.PredictResponse;

public abstract class AbstractPredictionManagerTest {
    
	@Inject
	private ModelManager modelManager;
	
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

	private RowDefinition rowDefinition;
	private BostonDatabase bostonDatabase;
	
	
	protected final void init(final String itemName, final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		bostonDatabase = new BostonDatabase();
		rowDefinition = definitionSpace.resolve(itemName, RowDefinition.class);
	}
	
	private DatasetOpeOld getBostonDataset(){
		final ItemDatasetLoader<BostonItem> loader = new ItemDatasetLoader<BostonItem>(datasetManager);
		loader.setItemDefinition(rowDefinition);
		loader.bindDataBase(bostonDatabase);
		List<UID<BostonItem>> items = bostonDatabase.getAllItems()
				.stream()   
				.map(item -> item.getUID())
				.collect(Collectors.toList());
		RowChunk<BostonItem> chunk = new RowChunk<BostonItem>(items, BostonItem.class);
		DatasetOpeOld dataset = loader.loadData(chunk, "DsBostonDataset");
		return dataset;
	}
	
	private PredictResponse testPrediction(final String predictionType) {
		DatasetOpeOld dataset = getBostonDataset();
		PredictResponse response = modelManager.predict(dataset.collect(), "boston-"+predictionType, 0);
		return response;
	}
	
	@Test
	public void testBostonPredictAll() {
		
		DatasetOpeOld dataset = getBostonDataset();
		
		int i = 0;
		modelManager.activate("boston", i);
		PredictResponse response = modelManager.predict(dataset.collect(), "boston", i);
		
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
		Assertions.assertEquals(BigDecimal.valueOf(22.43731140365631), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictDecisionTreeReg() {
		PredictResponse response = testPrediction("dtr");
		Assertions.assertEquals(BigDecimal.valueOf(24.0), response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	@Test
	public void testPredictRandomForestReg() {
		PredictResponse response = testPrediction("rfr");
		Assertions.assertEquals(BigDecimal.valueOf(25.974999999999994), response.getPredictionList().get(0).getPredictionNumeric());
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
		Assertions.assertEquals(BigDecimal.valueOf(31.88471545753804), response.getPredictionList().get(0).getPredictionNumeric());
	}
}
