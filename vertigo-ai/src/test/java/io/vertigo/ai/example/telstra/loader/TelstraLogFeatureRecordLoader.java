package io.vertigo.ai.example.telstra.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.domain.LogFeature;
import io.vertigo.ai.example.telstra.domain.LogFeatureTrain;
import io.vertigo.ai.example.telstra.services.TelstraServices;
import io.vertigo.ai.impl.structure.record.loader.AbstractSqlRecordLoader;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.task.TaskManager;

public final class TelstraLogFeatureRecordLoader extends AbstractSqlRecordLoader<Serializable, LogFeature, LogFeatureTrain> {

	private TelstraServices telstraServices;
	
	@Inject
	public TelstraLogFeatureRecordLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			TelstraServices telstraServices) {
		super(taskManager, transactionManager);
		this.telstraServices = telstraServices;
	}

	@Override
	public List<Record<LogFeature, LogFeatureTrain>> loadData(final RecordChunk<LogFeature> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsLogFeature", RecordDefinition.class);
		final List<Long> logFeatureIds = getIdsFromRecord(recordChunk);
		final DtList<LogFeatureTrain> logFeatureList = telstraServices.loadLogFeature(logFeatureIds);
		final List<Record<LogFeature, LogFeatureTrain>> logFeatureDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final LogFeatureTrain logFeature : logFeatureList) {
			logFeatureDatasets.add(Record.<LogFeature, LogFeatureTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), logFeature.getId()), logFeature));
		}
		return logFeatureDatasets;
	}

	@Override
	public void removeData() {
		telstraServices.removeLogFeatureTrain();
	}

	private List<Long> getIdsFromRecord(RecordChunk<LogFeature> recordChunk) {
		final List<Long> logFeatureIds = new ArrayList<>();
		for (final UID<LogFeature> uid : recordChunk.getAllUIDs()) {
			logFeatureIds.add((Long) uid.getId());
		}
		return logFeatureIds;
	}

	@Override
	public void insertData(Collection<Record<LogFeature, LogFeatureTrain>> datasets) {
		telstraServices.insertLogFeatureTrain(datasets);
	}

	@Override
	public void removeData(long id) {
		telstraServices.removeLogFeatureTrain(id);
	}

}
