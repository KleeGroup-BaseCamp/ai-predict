package io.vertigo.ai.example.telstra.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.domain.Location;
import io.vertigo.ai.example.telstra.domain.LocationTrain;
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

public final class TelstraLocationRecordLoader extends AbstractSqlRecordLoader<Serializable, Location, LocationTrain> {

	private TelstraServices telstraServices;

	@Inject
	public TelstraLocationRecordLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			TelstraServices telstraServices) {
		super(taskManager, transactionManager);
		this.telstraServices = telstraServices;
	}

	@Override
	public List<Record<Location, LocationTrain>> loadData(final RecordChunk<Location> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsLocation", RecordDefinition.class);
		final List<Long> locationIds = getIdsFromRecord(recordChunk);
		final DtList<LocationTrain> locationList = telstraServices.loadLocation(locationIds);
		final List<Record<Location, LocationTrain>> locationDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final LocationTrain location : locationList) {
			locationDatasets.add(Record.<Location, LocationTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), location.getId()), location));
		}
		return locationDatasets;
	}

	@Override
	public void removeData() {
		telstraServices.removeLocationTrain();
	}

	private List<Long> getIdsFromRecord(RecordChunk<Location> recordChunk) {
		final List<Long> locationIds = new ArrayList<>();
		for (final UID<Location> uid : recordChunk.getAllUIDs()) {
			locationIds.add((Long) uid.getId());
		}
		return locationIds;
	}

	@Override
	public void insertData(Collection<Record<Location, LocationTrain>> datasets) {
		telstraServices.insertLocationTrain(datasets);
	}

	@Override
	public void removeData(long id) {
		telstraServices.removeLocationTrain(id);
	}

}
