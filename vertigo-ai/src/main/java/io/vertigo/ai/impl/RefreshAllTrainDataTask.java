package io.vertigo.ai.impl;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private final DatasetDefinition datasetDefinition;

	/**
	 * Constructor.
	 * @param datasetDefinition DataSet definition
	 * @param refreshFuture Future for result
	 */
	public RefreshAllTrainDataTask(DatasetDefinition datasetDefinition, WritableFuture<Long> refreshFuture) {
		Assertion.check()
			.isNotNull(datasetDefinition)
			.isNotNull(refreshFuture);
		
		this.datasetDefinition = datasetDefinition;
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
				LOGGER.info("Refreshing training data of {} started", datasetDefinition.getName());

				recordLoader.removeData();
				for (final RecordChunk<S> recordChunk : recordLoader.chunk(keyConceptClass)) {
					final Collection<Dataset<S, DtObject>> datasets = recordLoader.loadData(recordChunk);

					if (!datasets.isEmpty()) {
						recordLoader.insertData(datasets);
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
				LOGGER.info("Refresh of {} finished in {} ms ({} elements done)", datasetDefinition.getName(), System.currentTimeMillis() - startTime, refreshCount);
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
