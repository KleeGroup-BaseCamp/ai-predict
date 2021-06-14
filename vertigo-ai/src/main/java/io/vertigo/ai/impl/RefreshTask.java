package io.vertigo.ai.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.structure.record.DatasetManager;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public final class RefreshTask implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(RefreshTask.class);
	private static final int DIRTY_ELEMENTS_CHUNK_SIZE = 500;
	private static final int REFRSH_ERROR_WAIT = 1000; // attend 1s avant de recommencer
	private static final int REFRESH_ERROR_MAX_RETRY = 5; //il y a 5 + 1 essais au total (le premier + 5 retry)

	private final DatasetDefinition recordDefinition;
	private final Set<UID<? extends KeyConcept>> dirtyElements;

	public RefreshTask(final DatasetDefinition recordDefinition, final Set<UID<? extends KeyConcept>> dirtyElements) {
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
					Collection<Dataset<KeyConcept, DtObject>> records = recordLoader.loadData(recordChunk);
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
