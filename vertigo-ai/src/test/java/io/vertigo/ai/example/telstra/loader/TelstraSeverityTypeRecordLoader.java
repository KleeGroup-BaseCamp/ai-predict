package io.vertigo.ai.example.telstra.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.domain.SeverityType;
import io.vertigo.ai.example.telstra.domain.SeverityTypeTrain;
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

public final class TelstraSeverityTypeRecordLoader extends AbstractSqlRecordLoader<Serializable, SeverityType, SeverityTypeTrain> {

	private TelstraServices telstraServices;

	@Inject
	public TelstraSeverityTypeRecordLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			TelstraServices telstraServices) {
		super(taskManager, transactionManager);
		this.telstraServices = telstraServices;
	}

	@Override
	public List<Record<SeverityType, SeverityTypeTrain>> loadData(final RecordChunk<SeverityType> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsSeverityType", RecordDefinition.class);
		final List<Long> severityTypeIds = getIdsFromRecord(recordChunk);
		final DtList<SeverityTypeTrain> SeverityTypeList = telstraServices.loadSeverityType(severityTypeIds);
		final List<Record<SeverityType, SeverityTypeTrain>> severityTypeDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final SeverityTypeTrain severityType : SeverityTypeList) {
			severityTypeDatasets.add(Record.<SeverityType, SeverityTypeTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), severityType.getId()), severityType));
		}
		return severityTypeDatasets;
	}

	@Override
	public void removeData() {
		telstraServices.removeSeverityTypeTrain();
	}

	private List<Long> getIdsFromRecord(RecordChunk<SeverityType> recordChunk) {
		final List<Long> severityTypeIds = new ArrayList<>();
		for (final UID<SeverityType> uid : recordChunk.getAllUIDs()) {
			severityTypeIds.add((Long) uid.getId());
		}
		return severityTypeIds;
	}

	@Override
	public void insertData(Collection<Record<SeverityType, SeverityTypeTrain>> datasets) {
		telstraServices.insertSeverityTypeTrain(datasets);
	}

	@Override
	public void removeData(long id) {
		telstraServices.removeSeverityTypeTrain(id);
	}

}
