package io.vertigo.ai.data.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.definitions.DatasetItemChunk;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.DatasetManager;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.ai.datasets.models.Dataset;
import io.vertigo.ai.impl.AbstractDatasetLoader;
import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.UID;

public class ItemDatasetLoader extends AbstractDatasetLoader<Long, Item, Item>{

	private static final int SEARCH_CHUNK_SIZE = 5;
	private final DatasetManager datasetManager;
	private ItemDatabase itemDatabase;
	
	@Inject
	public ItemDatasetLoader(final DatasetManager predictionManager) {
		Assertion.check().isNotNull(predictionManager);
		//---
		this.datasetManager = predictionManager;
	}
	
	@Override
	protected List<UID<Item>> loadNextURI(Long lastId,
			DtDefinition dtDefinition) {
		final DatasetItemDefinition indexDefinition = datasetManager.findFirstDatasetItemDefinitionByKeyConcept(Item.class);
		final List<UID<Item>> uris = new ArrayList<>(SEARCH_CHUNK_SIZE);
		//call loader service
		for (final Item item : itemDatabase.getAllItems()) {
			if (item.getId() > lastId) {
				uris.add(UID.of(indexDefinition.getKeyConceptDtDefinition(), item.getId()));
			}
			if (uris.size() >= SEARCH_CHUNK_SIZE) {
				break;
			}
		}
		return uris;
	}
	
	@Override
	public Dataset<DatasetItem<Item, Item>> loadData(final DatasetItemChunk<Item> recordChunk, final String datasetName) {
		Assertion.check().isNotNull(itemDatabase, "itemDataBase not bound");
		//-----
		final DatasetItemDefinition itemDefinition = datasetManager.findFirstDatasetItemDefinitionByKeyConcept(Item.class);
		final DatasetDefinition datasetDefinition = datasetManager.findDatasetDefinition(datasetName);
		final Dataset<DatasetItem<Item, Item>> dataset = new Dataset<DatasetItem<Item, Item>>(datasetDefinition, new ArrayList<DatasetItem<?, ?>>());
		final Map<Long, Item> itemPerId = new HashMap<>();
		for (final Item item : itemDatabase.getAllItems()) {
			itemPerId.put(item.getId(), item);
		}
		for (final UID<Item> uid : recordChunk.getAllUIDs()) {
			final Item item = itemPerId.get(uid.getId());
			dataset.addDatasetItem(DatasetItem.createItem(itemDefinition, uid, item));
		}
		return dataset;
	}
	
	public void bindDataBase(final ItemDatabase boundedDataBase) {
		Assertion.check().isNotNull(boundedDataBase);
		//----
		itemDatabase = boundedDataBase;
	}

}
