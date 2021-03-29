package io.vertigo.ai;

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
import io.vertigo.ai.data.domain.Item;
import io.vertigo.ai.data.domain.ItemDatabase;
import io.vertigo.ai.data.domain.ItemDatasetLoader;
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
	private ItemDatabase itemDatabase;
	
	protected final void init(final String itemName, final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		itemDatabase = new ItemDatabase();
		final ItemDatasetLoader itemDatasetLoader = node.getComponentSpace().resolve(ItemDatasetLoader.class);
		itemDatasetLoader.bindDataBase(itemDatabase);

		datasetItemDefinition = definitionSpace.resolve(itemName,DatasetItemDefinition.class);
		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
	}
	
	@Test
	public void testPredict() {
		final List<DatasetItem<?, ?>> data = itemDatabase.getAllItems()
				.stream()
				.map(item -> DatasetItem.createItem(datasetItemDefinition, item.getUID(), item))
				.collect(Collectors.toList());
		final Dataset<DatasetItem<Item, Item>> dataset = new Dataset<DatasetItem<Item, Item>>(datasetDefinition, data);
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized());
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
		
	}
	
	@Test
	public void testPredictWithLoader() {
		final ItemDatasetLoader loader = new ItemDatasetLoader(datasetManager);
		loader.bindDataBase(itemDatabase);
		List<UID<Item>> items = itemDatabase.getAllItems()
				.stream()   
				.map(item -> item.getUID())
				.collect(Collectors.toList());
		DatasetItemChunk<Item> chunk = new DatasetItemChunk<Item>(items);
		final Dataset<?> dataset = loader.loadData(chunk, "DsDatasetObject");
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized());
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
		
	}
}
