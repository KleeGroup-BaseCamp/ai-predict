package io.vertigo.ai.structure.record.definitions;

import java.util.Collection;
import java.util.List;

import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public interface RecordLoader<K extends KeyConcept, I extends DtObject> extends Component {

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
	
	
	/**
	 * Remove all data from a list of keyConcepts.
	 */
	void removeData();
	
	/**
	 * Remove some data from a list of keyConcepts.
	 */
	void removeData(long id);

	/**
	 * 
	 * @param datasets
	 */
	void insertData(Collection<Dataset<K, I>> datasets);
	
}
