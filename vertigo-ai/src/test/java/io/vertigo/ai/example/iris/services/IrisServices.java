package io.vertigo.ai.example.iris.services;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.dao.IrisTrainDAO;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.example.iris.domain.IrisTrain;
import io.vertigo.ai.example.train.TrainPAO;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;

@Transactional
public class IrisServices implements Component {

	@Inject
	private IrisTrainDAO irisTrainDAO;

	@Inject
	private TrainPAO trainPAO;
	
	@Inject
	private IrisDAO irisDAO;
	
	public DtList<IrisTrain> loadIris(List<Long> irisIds) {
		return irisTrainDAO.loadIris(irisIds);
	}

	public void removeIrisTrain() {
		trainPAO.removeIris();
	}
	
	public void removeIrisTrain(long id) {
		irisTrainDAO.delete(id);
	}

	public void insertIrisTrain(Collection<Record<Iris, IrisTrain>> iris) {
		DtList<IrisTrain> irisTrain = new DtList<>(IrisTrain.class);
		
		for (Record<Iris,IrisTrain> dataset : iris) {
			irisTrain.add(dataset.getRecordDtObject());
		}
		
		irisTrainDAO.bulkCreateIris(irisTrain);
	}
	
	public void create(Iris entity) {
		irisDAO.create(entity);
	}
	
	public void update(Iris entity) {
		irisDAO.update(entity);
	}
	
	public Iris getFirst() {
		
		return irisDAO.get((long) 1000);
	}
	
}
