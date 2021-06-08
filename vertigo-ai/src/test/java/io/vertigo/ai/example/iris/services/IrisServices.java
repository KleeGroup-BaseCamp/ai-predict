package io.vertigo.ai.example.iris.services;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.datamodel.structure.model.DtList;

public class IrisServices {

	@Inject
	IrisDAO irisDAO;
	
	public DtList<Iris> loadIris(List<Long> irisIds) {
		return irisDAO.loadIris(irisIds);
	}

}
