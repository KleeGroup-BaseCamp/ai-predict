package io.vertigo.ai.example.iris.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.example.iris.services.IrisServices;
import io.vertigo.ai.impl.loader.AbstractSqlRecordLoader;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.definitions.RecordChunk;
import io.vertigo.ai.structure.record.models.Dataset;
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
	public List<Dataset<Iris, IrisTrain>> loadData(final RecordChunk<Iris> recordChunk) {
		final DatasetDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("DsIris", DatasetDefinition.class);
		final List<Long> irisIds = getIdsFromRecord(recordChunk);
		final DtList<IrisTrain> irisList = irisServices.loadIris(irisIds);
		final List<Dataset<Iris, IrisTrain>> irisDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final IrisTrain iris : irisList) {
			irisDatasets.add(Dataset.<Iris, IrisTrain> createDataset(datasetDefinition, UID.of(datasetDefinition.getKeyConceptDtDefinition(), iris.getId()), iris));
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
	public void insertData(Collection<Dataset<Iris, IrisTrain>> datasets) {
		irisServices.insertIrisTrain(datasets);
	}


	@Override
	public void removeData(long id) {
		irisServices.removeIrisTrain(id);
		
	}

}
