package io.vertigo.ai.impl;

import java.io.Serializable;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.structure.record.DatasetManager;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordLoader;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.Node;
import io.vertigo.core.util.ClassUtil;
import io.vertigo.datafactory.impl.search.WritableFuture;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class RefreshAllTrainDataTask<S extends KeyConcept> implements Runnable {
 
	private static final Logger LOGGER = LogManager.getLogger(RefreshAllTrainDataTask.class);
	private static volatile boolean REFRESH_IN_PROGRESS;
	private static volatile long REFRESH_COUNT;
	private final WritableFuture<Long> refreshFuture;
	private final DatasetDefinition datasetDefinition;
	private final DatasetManager datasetManager;

	public RefreshAllTrainDataTask(DatasetDefinition datasetDefinition,
			WritableFuture<Long> refreshFuture,
			DatasetManager datasetManager) {
		Assertion.check()
		.isNotNull(datasetDefinition)
		.isNotNull(refreshFuture)
		.isNotNull(datasetManager);
		
		this.datasetDefinition = datasetDefinition;
		this.datasetManager = datasetManager;
		this.refreshFuture = refreshFuture;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		if (isRefreshInProgress()) {
			final String warnMessage = "Refreshing of all data of " + datasetDefinition.getName() + " is already in progess (" + getRefreshCount() + " elements done)";
			LOGGER.warn(warnMessage);
			refreshFuture.fail(new VSystemException(warnMessage));
		} else {
			//-----
			startRefresh();
			long refreshCount = 0;
			final long startTime = System.currentTimeMillis();
			try {
				final Class<S> keyConceptClass = (Class<S>) ClassUtil.classForName(datasetDefinition.getKeyConceptDtDefinition().getClassCanonicalName(), KeyConcept.class);
				final RecordLoader<S, DtObject> recordLoader = Node.getNode().getComponentSpace().resolve(datasetDefinition.getRecordLoaderId(), RecordLoader.class);
				Serializable lastUID = null;
				LOGGER.info("Reindexation of {} started", datasetDefinition.getName());

				for (final RecordChunk<S> recordChunk : recordLoader.chunk(keyConceptClass)) {
					final Collection<Dataset<S, DtObject>> datasets = recordLoader.loadData(recordChunk);

					final Serializable maxUID = recordChunk.getLastUID().getId();
					Assertion.check().isFalse(maxUID.equals(lastUID), "SearchLoader ({0}) error : return the same uid list", datasetDefinition.getRecordLoaderId());
					datasetManager.removeAll(datasetDefinition, urisRangeToListFilter(lastUID, maxUID));
					if (!datasets.isEmpty()) {
						datasetManager.putAll(datasetDefinition, datasets);
					}
					lastUID = maxUID;
					refreshCount += recordChunk.getAllUIDs().size();
					updateRefreshCount(refreshCount);
				}
				//On vide la suite, pour le cas ou les dernières données ne sont plus là
				datasetManager.removeAll(datasetDefinition, urisRangeToListFilter(lastUID, null));
				//On ne retire pas la fin, il y a un risque de retirer les données ajoutées depuis le démarrage de l'indexation
				refreshFuture.success(refreshCount);
			} catch (final Exception e) {
				LOGGER.error("Reindexation error", e);
				refreshFuture.fail(e);
			} finally {
				stopRefresh();
				LOGGER.info("Reindexation of {} finished in {} ms ({} elements done)", datasetDefinition.getName(), System.currentTimeMillis() - startTime, refreshCount);
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

	private static ListFilter urisRangeToListFilter(final Serializable firstUri, final Serializable lastUri) {
		final String filterValue = new StringBuilder()
				.append("docId").append(":{") //{ for exclude min
				.append(firstUri != null ? escapeStringId(firstUri) : "*")
				.append(" TO ")
				.append(lastUri != null ? escapeStringId(lastUri) : "*")
				.append("]")
				.toString();
		return ListFilter.of(filterValue);
	}

	private static Serializable escapeStringId(final Serializable id) {
		if (id instanceof String) {
			return "\"" + id + "\"";
		}
		return id;
	}

}
