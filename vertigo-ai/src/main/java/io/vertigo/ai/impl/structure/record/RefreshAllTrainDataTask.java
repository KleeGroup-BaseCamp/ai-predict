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

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.Node;
import io.vertigo.core.util.ClassUtil;
import io.vertigo.datafactory.impl.search.WritableFuture;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Refresh all training data task.
 * @author dcouillard, xdurand
 *
 * @param <S> KeyConcept type
 */
public class RefreshAllTrainDataTask<S extends KeyConcept> implements Runnable {
 
	private static final Logger LOGGER = LogManager.getLogger(RefreshAllTrainDataTask.class);
	private static volatile boolean REFRESH_IN_PROGRESS;
	private static volatile long REFRESH_COUNT;
	private final WritableFuture<Long> refreshFuture;
	private final RecordDefinition recordDefinition;

	/**
	 * Constructor.
	 * @param recordDefinition DataSet definition
	 * @param refreshFuture Future for result
	 */
	public RefreshAllTrainDataTask(RecordDefinition recordDefinition, WritableFuture<Long> refreshFuture) {
		Assertion.check()
			.isNotNull(recordDefinition)
			.isNotNull(refreshFuture);
		
		this.recordDefinition = recordDefinition;
		this.refreshFuture = refreshFuture;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		if (isRefreshInProgress()) {
			final String warnMessage = "Refreshing of all data of " + recordDefinition.getName() + " is already in progess (" + getRefreshCount() + " elements done)";
			LOGGER.warn(warnMessage);
			refreshFuture.fail(new VSystemException(warnMessage));
		} else {
			//-----
			startRefresh();
			long refreshCount = 0;
			final long startTime = System.currentTimeMillis();
			try {
				final Class<S> keyConceptClass = (Class<S>) ClassUtil.classForName(recordDefinition.getKeyConceptDtDefinition().getClassCanonicalName(), KeyConcept.class);
				final RecordLoader<S, DtObject> recordLoader = Node.getNode().getComponentSpace().resolve(recordDefinition.getRecordLoaderId(), RecordLoader.class);
				LOGGER.info("Refreshing training data of {} started", recordDefinition.getName());

				recordLoader.removeData();
				for (final RecordChunk<S> recordChunk : recordLoader.chunk(keyConceptClass)) {
					final Collection<Record<S, DtObject>> records = recordLoader.loadData(recordChunk);

					if (!records.isEmpty()) {
						recordLoader.insertData(records);
					}
					
					refreshCount += recordChunk.getAllUIDs().size();
					updateRefreshCount(refreshCount);
				}
				
				refreshFuture.success(refreshCount);
			} catch (final Exception e) {
				LOGGER.error("Refresh error", e);
				refreshFuture.fail(e);
			} finally {
				stopRefresh();
				LOGGER.info("Refresh of {} finished in {} ms ({} elements done)", recordDefinition.getName(), System.currentTimeMillis() - startTime, refreshCount);
			}
		}
	}

	private static boolean isRefreshInProgress() {
		return REFRESH_IN_PROGRESS;
	}

	private static void startRefresh() {
		REFRESH_IN_PROGRESS = true;
	}

	private static void stopRefresh() {
		REFRESH_IN_PROGRESS = false;
	}

	private static void updateRefreshCount(final long refreshCount) {
		REFRESH_COUNT = refreshCount;
	}

	private static long getRefreshCount() {
		return REFRESH_COUNT;
	}

}
