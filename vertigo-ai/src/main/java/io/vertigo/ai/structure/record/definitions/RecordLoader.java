/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2021, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertigo.ai.structure.record.definitions;

import java.util.Collection;
import java.util.List;

import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Specific Record loader.
 * @param <K> KeyConcept
 * @param <I> Record data's type
 */
public interface RecordLoader<K extends KeyConcept, I extends DtObject> extends Component {

	/**
	 * Load all data from a list of keyConcepts.
	 * @param searchChunk the chunk
	 * @return List of searchIndex
	 */
	List<Record<K, I>> loadData(RecordChunk<K> searchChunk);

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
	 * Remove a resource from a list of keyConcepts.
	 * @param id of the data to remove
	 */
	void removeData(long id);

	/**
	 * Insert a collection of records to the database
	 * @param datasets
	 */
	void insertData(Collection<Record<K, I>> records);
	
}
