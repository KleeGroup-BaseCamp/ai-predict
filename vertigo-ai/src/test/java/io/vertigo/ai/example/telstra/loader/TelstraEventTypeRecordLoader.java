package io.vertigo.ai.example.telstra.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.domain.EventType;
import io.vertigo.ai.example.telstra.domain.EventTypeTrain;
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

public final class TelstraEventTypeRecordLoader extends AbstractSqlRecordLoader<Serializable, EventType, EventTypeTrain> {

	private TelstraServices telstraServices;
	
	@Inject
	public TelstraEventTypeRecordLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			TelstraServices telstraServices) {
		super(taskManager, transactionManager);
		this.telstraServices = telstraServices;
	}

	
	@Override
	public List<Record<EventType, EventTypeTrain>> loadData(final RecordChunk<EventType> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsEventType", RecordDefinition.class);
		final List<Long> eventTypeIds = getIdsFromRecord(recordChunk);
		final DtList<EventTypeTrain> eventTypeList = telstraServices.loadEventType(eventTypeIds);
		final List<Record<EventType, EventTypeTrain>> EventTypeDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final EventTypeTrain eventType : eventTypeList) {
			EventTypeDatasets.add(Record.<EventType, EventTypeTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), eventType.getId()), eventType));
		}
		return EventTypeDatasets;
	}


	private List<Long> getIdsFromRecord(RecordChunk<EventType> recordChunk) {
		final List<Long> eventTypeIds = new ArrayList<>();
		for (final UID<EventType> uid : recordChunk.getAllUIDs()) {
			eventTypeIds.add((Long) uid.getId());
		}
		return eventTypeIds;
	}

	@Override
	public void insertData(Collection<Record<EventType, EventTypeTrain>> datasets) {
		telstraServices.insertEventTypeTrain(datasets);
	}

	@Override
	public void removeData(long id) {
		telstraServices.removeEventTypeTrain(id);
		
	}

	@Override
	public void removeData() {
		telstraServices.removeEventTypeTrain();
	}
}
