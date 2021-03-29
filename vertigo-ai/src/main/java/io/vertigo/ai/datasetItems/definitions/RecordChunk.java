package io.vertigo.ai.datasetItems.definitions;

import java.util.Collections;
import java.util.List;

import io.vertigo.core.lang.Assertion;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public class RecordChunk<K extends KeyConcept> {

    private final List<UID<K>> uids;

	/**
	 */
	public RecordChunk(final List<UID<K>> uids) {
		Assertion.check().isNotNull(uids);
		//---
		this.uids = Collections.unmodifiableList(uids);
    }
	/**
	 */
	public List<UID<K>> getAllUIDs() {
		return uids;
	}

	public UID<K> getLastUID() {
		return uids.get(uids.size() - 1);
	}
}
