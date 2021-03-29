package io.vertigo.ai.datasets.definitions;

import io.vertigo.ai.datasetItems.definitions.DatasetItemChunk;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.models.Dataset;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Specific DatasetItemLoader loader.
 * @param <K> KeyConcept
 * @param <I> Item data's type
 */

public interface DatasetLoader<K extends KeyConcept, I extends DtObject> extends Component {
    
  
	/**
	 * Load all data from a list of keyConcepts.
	 * @param recordChunk the chunk
	 * @return List of data
	 */
	Dataset<DatasetItem<K, I>> loadData(DatasetItemChunk<K> searchChunk, String datasetName);

	/**
	 * Create a chunk iterator for crawl all keyConcept data.
	 * @param keyConceptClass keyConcept class
	 * @return Iterator of chunk
	 */
	Iterable<DatasetItemChunk<K>> chunk(final Class<K> keyConceptClass);

}
