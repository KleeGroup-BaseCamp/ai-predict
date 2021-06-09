package io.vertigo.ai.example.iris.services;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.dao.IrisTrainDAO;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.example.train.TrainPAO;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class IrisServices implements Component {

	@Inject
	private IrisTrainDAO irisTrainDAO;

	@Inject
	private TrainPAO trainPAO;
	
	public DtList<IrisTrain> loadIris(List<Long> irisIds) {
		return irisTrainDAO.loadIris(irisIds);
	}

	public void removeIrisTrain() {
		trainPAO.removeIris();
	}

	public void insertIrisTrain(Collection<Dataset<Iris, IrisTrain>> iris) {
		DtList<IrisTrain> irisTrain = new DtList<>(IrisTrain.class);
		
		for (Dataset<Iris,IrisTrain> dataset : iris) {
			irisTrain.add(dataset.getRecordDtObject());
		}
		
		irisTrainDAO.bulkCreateIris(irisTrain);
	}
	
}
