package io.vertigo.ai;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.ai.data.domain.Item;
import io.vertigo.ai.data.domain.ItemDatabase;
import io.vertigo.ai.data.domain.ItemDatasetLoader;
import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.ai.datasets.models.Dataset;

public abstract class AbstractPredictionManagerTest {
    
	/** Logger. */
	private final Logger log = LogManager.getLogger(getClass());
	
	@Inject
	private PredictionManager predictionManager;

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
		final List<DatasetItem<Item, Item>> data = itemDatabase.getAllItems()
				.stream()
				.map(item -> DatasetItem.createIndex(datasetItemDefinition, item.getUID(), item))
				.collect(Collectors.toList());
		final Dataset dataset = new Dataset(datasetDefinition, data);
		PredictResponse response = predictionManager.predict(dataset.getDatasetSerialized());
		System.out.println(response);
		
	}
}
