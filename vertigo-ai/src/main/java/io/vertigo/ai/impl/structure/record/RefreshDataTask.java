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

package io.vertigo.ai.impl.structure.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.structure.record.RecordManager;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Refresh on event training data task.
 */
public final class RefreshDataTask implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(RefreshDataTask.class);
	private static final int DIRTY_ELEMENTS_CHUNK_SIZE = 500;
	private static final int REFRSH_ERROR_WAIT = 1000; // attend 1s avant de recommencer
	private static final int REFRESH_ERROR_MAX_RETRY = 5; //il y a 5 + 1 essais au total (le premier + 5 retry)

	private final RecordDefinition recordDefinition;
	private final Set<UID<? extends KeyConcept>> dirtyElements;

	/**
	 * Constructor.
	 * @param recordDefinition DataSet definition
	 * @param dirtyElements Set of UID to refresh
	 */
	public RefreshDataTask(final RecordDefinition recordDefinition, final Set<UID<? extends KeyConcept>> dirtyElements) {
		Assertion.check()
				.isNotNull(recordDefinition)
				.isNotNull(dirtyElements);
		//-----
		this.recordDefinition = recordDefinition;
		this.dirtyElements = dirtyElements; //On ne fait pas la copie ici
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		long dirtyElementsCount = 0;
		do {
			final long startTime = System.currentTimeMillis();
			final List<UID<? extends KeyConcept>> refreshUris = new ArrayList<>();
			try {
				synchronized (dirtyElements) {
					if (!dirtyElements.isEmpty()) {
						refreshUris.addAll(dirtyElements.stream()
								.limit(DIRTY_ELEMENTS_CHUNK_SIZE)
								.collect(Collectors.toList()));
						dirtyElements.removeAll(refreshUris);
					}
				}
				dirtyElementsCount = refreshUris.size();
				if (!refreshUris.isEmpty()) {
					final RecordLoader recordLoader = Node.getNode().getComponentSpace().resolve(recordDefinition.getRecordLoaderId(), RecordLoader.class);
					RecordChunk recordChunk = new RecordChunk(refreshUris);
					Collection<Record<KeyConcept, DtObject>> records = recordLoader.loadData(recordChunk);
					if (!records.isEmpty()) {
						List<Long> ids = new ArrayList<Long>((int)dirtyElementsCount);
						refreshUris.forEach(uid -> {
							try {
								recordLoader.removeData((long) uid.getId());
							} catch (Exception e) {
								LOGGER.error("Update train database error, skip id " + uid.getId() + " because does not exist");
							}
						});
						recordLoader.insertData(records);
					}
				}
			} catch (final Exception e) {
				LOGGER.error("Update train database error, skip " + dirtyElementsCount + " elements (" + refreshUris + ")", e);
			} finally {
				LOGGER.log(dirtyElementsCount > 0 ? Level.INFO : Level.DEBUG,
						"Update train database, " + dirtyElementsCount + " " + recordDefinition.getName() + " finished in " + (System.currentTimeMillis() - startTime) + "ms");
			}
		} while (dirtyElementsCount > 0);

	}

	
}
