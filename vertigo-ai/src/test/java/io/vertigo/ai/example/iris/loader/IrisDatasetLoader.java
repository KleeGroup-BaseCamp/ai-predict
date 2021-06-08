package io.vertigo.ai.example.iris.loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.example.iris.domain.Iris;
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

public final class IrisDatasetLoader extends AbstractSqlRecordLoader<Serializable, Iris, Iris> {

	IrisServices irisServices;
	
	public IrisDatasetLoader(TaskManager taskManager,
			VTransactionManager transactionManager,
			IrisServices irisServices) {
		super(taskManager, transactionManager);
		this.irisServices = irisServices;
	}

	@Override
	public List<Dataset<Iris, Iris>> loadData(final RecordChunk<Iris> recordChunk) {
		final DatasetDefinition datasetDefinition = Node.getNode().getDefinitionSpace().resolve("IdxEquipment", DatasetDefinition.class);
		final List<Long> irisIds = new ArrayList<>();
		for (final UID<Iris> uid : recordChunk.getAllUIDs()) {
			irisIds.add((Long) uid.getId());
		}
		final DtList<Iris> irisList = irisServices.loadIris(irisIds);
		final List<Dataset<Iris, Iris>> irisDatasets = new ArrayList<>(recordChunk.getAllUIDs().size());
		for (final Iris iris : irisList) {
			irisDatasets.add(Dataset.<Iris, Iris> createIndex(datasetDefinition,
					UID.of(datasetDefinition.getKeyConceptDtDefinition(), iris.getId()), iris));
		}
		return irisDatasets;
	}



}
