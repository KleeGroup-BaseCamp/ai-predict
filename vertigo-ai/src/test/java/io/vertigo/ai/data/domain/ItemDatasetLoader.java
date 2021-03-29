package io.vertigo.ai.data.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.definitions.RecordChunk;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.impl.AbstractDatasetLoader;
import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.UID;

public class ItemDatasetLoader extends AbstractDatasetLoader<Long, Item, Item>{

	private static final int SEARCH_CHUNK_SIZE = 5;
	private final PredictionManager predictionManager;
	private ItemDatabase itemDatabase;
	
	@Inject
	public ItemDatasetLoader(final PredictionManager predictionManager) {
		Assertion.check().isNotNull(predictionManager);
		//---
		this.predictionManager = predictionManager;
	}
	
	@Override
	protected List<UID<Item>> loadNextURI(Long lastId,
			DtDefinition dtDefinition) {
		final DatasetItemDefinition indexDefinition = predictionManager.findFirstDatasetDefinitionByKeyConcept(Item.class);
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
	public List<DatasetItem<Item, Item>> loadData(final RecordChunk<Item> searchChunk) {
		Assertion.check().isNotNull(itemDatabase, "itemDataBase not bound");
		//-----
		final DatasetItemDefinition datasetDefinition = predictionManager.findFirstDatasetDefinitionByKeyConcept(Item.class);
		final List<DatasetItem<Item, Item>> itemIndexes = new ArrayList<>();
		final Map<Long, Item> itemPerId = new HashMap<>();
		for (final Item item : itemDatabase.getAllItems()) {
			itemPerId.put(item.getId(), item);
		}
		for (final UID<Item> uid : searchChunk.getAllUIDs()) {
			final Item item = itemPerId.get(uid.getId());
			itemIndexes.add(DatasetItem.createIndex(datasetDefinition, uid, item));
		}
		return itemIndexes;
	}
	
	public void bindDataBase(final ItemDatabase boundedDataBase) {
		Assertion.check().isNotNull(boundedDataBase);
		//----
		itemDatabase = boundedDataBase;
	}

}
