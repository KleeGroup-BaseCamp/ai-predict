package io.vertigo.ai.example.iris.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.impl.structure.record.loader.AbstractSqlRecordLoader;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.task.TaskManager;

public final class IrisDatasetLoader extends AbstractSqlRecordLoader<Serializable, Iris, IrisTrain> {

	private IrisServices irisServices;
	
	@Inject
	public IrisDatasetLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			IrisServices irisServices) {
		super(taskManager, transactionManager);
		this.irisServices = irisServices;
	}

	
	@Override
	public List<Record<Iris, IrisTrain>> loadData(final RecordChunk<Iris> recordChunk) {
		final RecordDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsIris", RecordDefinition.class);
		final List<Long> irisIds = getIdsFromRecord(recordChunk);
		final DtList<IrisTrain> irisList = irisServices.loadIris(irisIds);
		final List<Record<Iris, IrisTrain>> irisDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final IrisTrain iris : irisList) {
			irisDatasets.add(Record.<Iris, IrisTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), iris.getId()), iris));
		}
		return irisDatasets;
	}


	@Override
	public void removeData() {
		irisServices.removeIrisTrain();
	}


	private List<Long> getIdsFromRecord(RecordChunk<Iris> recordChunk) {
		final List<Long> irisIds = new ArrayList<>();
		for (final UID<Iris> uid : recordChunk.getAllUIDs()) {
			irisIds.add((Long) uid.getId());
		}
		return irisIds;
	}


	@Override
	public void insertData(Collection<Record<Iris, IrisTrain>> datasets) {
		irisServices.insertIrisTrain(datasets);
	}


	@Override
	public void removeData(long id) {
		irisServices.removeIrisTrain(id);
		
	}

}
