package io.vertigo.ai.datasets.definitions;


import java.util.List;

import io.vertigo.ai.datasets.models.Dataset;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface DatasetLoader<K extends KeyConcept, I extends DtObject> extends Component {
    
    	/**
	 * Load all data from a list of keyConcepts.
	 * @param searchChunk the chunk
	 * @return List of searchIndex
	 */
	List<Dataset<K, I>> loadData(RecordChunk<K> searchChunk);

	/**
	 * Create a chunk iterator for crawl all keyConcept data.
	 * @param keyConceptClass keyConcept class
	 * @return Iterator of chunk
	 */
	Iterable<RecordChunk<K>> chunk(final Class<K> keyConceptClass);

}
