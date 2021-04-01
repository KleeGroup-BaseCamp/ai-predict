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

public class ItemDatasetLoader<T extends TestItems> extends AbstractDatasetLoader<Long, T, T>{

	private static final int SEARCH_CHUNK_SIZE = 5;
	private final DatasetManager datasetManager;
	private TestDatabase<T> itemDatabase;
	private DatasetItemDefinition itemDefinition;

	
	@Inject
	public ItemDatasetLoader(final DatasetManager predictionManager) {
		Assertion.check().isNotNull(predictionManager);
		//---
		this.datasetManager = predictionManager;
	}
	
	public void setItemDefinition(final DatasetItemDefinition datasetItemDefinition) {
		this.itemDefinition = datasetItemDefinition;
	}
	@Override
	protected List<UID<T>> loadNextURI(Long lastId,
			DtDefinition dtDefinition) {
		final List<UID<T>> uris = new ArrayList<>(SEARCH_CHUNK_SIZE);
		//call loader service
		for (final T item : itemDatabase.getAllItems()) {
			if (item.getId() > lastId) {
				uris.add(UID.of(itemDefinition.getKeyConceptDtDefinition(), item.getId()));
			}
			if (uris.size() >= SEARCH_CHUNK_SIZE) {
				break;
			}
		}
		return uris;
	}
	
	@Override
	public Dataset<DatasetItem<T, T>> loadData(final DatasetItemChunk<T> recordChunk, final String datasetName) {
		Assertion.check().isNotNull(itemDatabase, "itemDataBase not bound");
		//-----
		final DatasetDefinition datasetDefinition = datasetManager.findDatasetDefinition(datasetName);
		final Dataset<DatasetItem<T, T>> dataset = new Dataset<DatasetItem<T, T>>(datasetDefinition, new ArrayList<DatasetItem<?, ?>>());
		final Map<Long, T> itemPerId = new HashMap<>();
		for (final T item : itemDatabase.getAllItems()) {
			itemPerId.put(item.getId(), item);
		}
		for (final UID<T> uid : recordChunk.getAllUIDs()) {
			final T item = itemPerId.get(uid.getId());
			dataset.addDatasetItem(DatasetItem.createItem(itemDefinition, uid, item));
		}
		return dataset;
	}
	
	public void bindDataBase(final TestDatabase<T> boundedDataBase) {
		Assertion.check().isNotNull(boundedDataBase);
		//----
		itemDatabase = boundedDataBase;
	}

}
