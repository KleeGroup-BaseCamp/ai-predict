package io.vertigo.ai.impl.structure.dataset;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.DtObject;

public final class DatasetImpl<E extends DtObject> implements Dataset<E> {
	
	private DtDefinition itemDtDefinition;
	private List<E> data;
	
	/**
	 * Constructor
	 * @param itemDtDefinition the dataset object definition
	 */
	public DatasetImpl(DtDefinition itemDtDefinition) {
		this.itemDtDefinition = itemDtDefinition;
		this.data = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DtDefinition getItemDefinition() {
		return itemDtDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(List<E> data) {
		this.data = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> collect() {
		return data;
	}

}
