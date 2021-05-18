package io.vertigo.ai.structure.dataset.definitions;

import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.definitions.RowChunk;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface DatasetLoader<K extends KeyConcept, I extends DtObject> extends Component {

	/**
	 * Load all data from a list of keyConcepts.
	 * @param recordChunk the chunk
	 * @return List of data
	 */
	Dataset loadData(RowChunk<K> searchChunk, String datasetName);

	/**
	 * Create a chunk iterator for crawl all keyConcept data.
	 * @param keyConceptClass keyConcept class
	 * @return Iterator of chunk
	 */
	Iterable<RowChunk<K>> chunk(final Class<K> keyConceptClass);
}
