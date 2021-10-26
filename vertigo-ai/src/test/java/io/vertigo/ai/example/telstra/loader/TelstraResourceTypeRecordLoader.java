package io.vertigo.ai.example.telstra.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.domain.ResourceType;
import io.vertigo.ai.example.telstra.domain.ResourceTypeTrain;
import io.vertigo.ai.example.telstra.services.TelstraServices;
import io.vertigo.ai.impl.structure.record.loader.AbstractSqlRecordLoader;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.task.TaskManager;

public final class TelstraResourceTypeRecordLoader extends AbstractSqlRecordLoader<Serializable, ResourceType, ResourceTypeTrain> {

	private TelstraServices telstraServices;
	
	@Inject
	public TelstraResourceTypeRecordLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			TelstraServices telstraServices) {
		super(taskManager, transactionManager);
		this.telstraServices = telstraServices;
	}

	@Override
	public List<Record<ResourceType, ResourceTypeTrain>> loadData(final RecordChunk<ResourceType> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsResourceType", RecordDefinition.class);
		final List<Long> severityTypeIds = getIdsFromRecord(recordChunk);
		final DtList<ResourceTypeTrain> resourceTypeList = telstraServices.loadResourceType(severityTypeIds);
		final List<Record<ResourceType, ResourceTypeTrain>> resourceTypeDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final ResourceTypeTrain resourceType : resourceTypeList) {
			resourceTypeDatasets.add(Record.<ResourceType, ResourceTypeTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), resourceType.getId()), resourceType));
		}
		return resourceTypeDatasets;
	}

	@Override
	public void removeData() {
		telstraServices.removeResourceTypeTrain();
	}

	private List<Long> getIdsFromRecord(RecordChunk<ResourceType> recordChunk) {
		final List<Long> resourceTypeIds = new ArrayList<>();
		for (final UID<ResourceType> uid : recordChunk.getAllUIDs()) {
			resourceTypeIds.add((Long) uid.getId());
		}
		return resourceTypeIds;
	}

	@Override
	public void insertData(Collection<Record<ResourceType, ResourceTypeTrain>> datasets) {
		telstraServices.insertResourceTypeTrain(datasets);
	}

	@Override
	public void removeData(long id) {
		telstraServices.removeResourceTypeTrain(id);
	}

}
