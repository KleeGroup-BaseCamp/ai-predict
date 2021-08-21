package io.vertigo.ai.structure.row.definitions;

import java.util.Collections;
import java.util.List;

import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Chunk of keyConcept to store Rows in a Dataset.
 * @param <K> the type of the KeyConcept
 */
public class RowChunk<K extends KeyConcept> {

	private final List<UID<K>> uids;
	private final Class<?> clazz;
	
	/**
	 * @param uids the list of keyConcept uids
	 */
	public RowChunk(final List<UID<K>> uids, final Class<?> clazz) {
		Assertion.check().isNotNull(uids);
		//---
		this.uids = Collections.unmodifiableList(uids);
		this.clazz = clazz;
    }

	/**
	 * @return All KeyConcept's uids of this chunk
	 */
	public List<UID<K>> getAllUIDs() {
		return uids;
	}

	public UID<K> getLastUID() {
		return uids.get(uids.size() - 1);
	}

	public Class<?> getRowClass() {
		return clazz;
	}
}