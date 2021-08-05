package io.vertigo.ai.impl.structure.dataset;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;

public final class DatasetImpl<E extends Entity> implements Dataset<E> {
	
	private DtDefinition itemDtDefinition;
	private List<E> data;
	
	public DatasetImpl(DtDefinition itemDtDefinition) {
		this.itemDtDefinition = itemDtDefinition;
		this.data = null;
	}
	
	@Override
	public DtDefinition getItemDefinition() {
		return itemDtDefinition;
	}

	@Override
	public void load(List<E> data) {
		this.data = data;
	}

	@Override
	public List<E> collect() {
		return data;
	}

}
