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

package io.vertigo.ai.impl.structure.record.loader;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicType;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * Abstract RecordLoader with default chunk implementation.
 * @param <P> Primary key type
 * @param <K> KeyConcept type
 * @param <I> Records type
 */
public abstract class AbstractRecordLoader<P extends Serializable, K extends KeyConcept, I extends DtObject> implements RecordLoader<K, I> {

	@Override
	public final Iterable<RecordChunk<K>> chunk(final Class<K> keyConceptClass) {
		return () -> createIterator(keyConceptClass);
	}

	private List<UID<K>> doLoadNextURI(final P lastId, final DtDefinition dtDefinition) {
		return loadNextURI(lastId, dtDefinition);
	}
	
	/**
	 * Load uris of next chunk.
	 * @param lastId Last chunk id
	 * @param dtDefinition KeyConcept definition
	 * @return Uris of next chunk.
	 */
	protected abstract List<UID<K>> loadNextURI(final P lastId, final DtDefinition dtDefinition);
	

	private P getLowestIdValue(final DtDefinition dtDefinition) {
		final DtField idField = dtDefinition.getIdField().get();
		Assertion.check().isTrue(
				idField.getSmartTypeDefinition().getScope().isPrimitive(),
				"Ids must be primitives : idField '{0}' on dtDefinition '{1}' has the smartType '{2}'", dtDefinition, idField.getName(), idField.getSmartTypeDefinition());
		//---
		final BasicType idDataType = idField.getSmartTypeDefinition().getBasicType();
		P pkValue;
		switch (idDataType) {
			case Integer:
				pkValue = (P) Integer.valueOf(-1);
				break;
			case Long:
				pkValue = (P) Long.valueOf(-1);
				break;
			case String:
				pkValue = (P) "";
				break;
			case BigDecimal:
			case DataStream:
			case Boolean:
			case Double:
			case LocalDate:
			case Instant:
			default:
				throw new IllegalArgumentException("Type's PK " + idDataType.name() + " of "
						+ dtDefinition.getClassSimpleName() + " is not supported, prefer int, long or String ID.");
		}
		return pkValue;
	}
	
	private Iterator<RecordChunk<K>> createIterator(final Class<K> keyConceptClass) {
		return new Iterator<>() {
			private final DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(keyConceptClass);
			private RecordChunk<K> current;
			private RecordChunk<K> next = firstChunk();

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				if (next == null) {
					next = nextChunk(current);
				}
				return !next.getAllUIDs().isEmpty();
			}
		
			/** {@inheritDoc} */
			@Override
			public RecordChunk<K> next() {
				if (!hasNext()) {
					throw new NoSuchElementException("no next chunk found");
				}
				current = next;
				next = null;
				return current;
			}
		
			private RecordChunk<K> nextChunk(final RecordChunk<K> previousChunk) {
				final List<UID<K>> previousUris = previousChunk.getAllUIDs();
				final P lastId = (P) previousUris.get(previousUris.size() - 1).getId();
				// call loader service
				final List<UID<K>> uris = doLoadNextURI(lastId, dtDefinition);
				return new RecordChunk<>(uris);
			}
		
			private RecordChunk<K> firstChunk() {
				final P lastId = getLowestIdValue(dtDefinition);
				// call loader service
				final List<UID<K>> uris = doLoadNextURI(lastId, dtDefinition);
				return new RecordChunk<>(uris);
			}
			
		};
	}
}
