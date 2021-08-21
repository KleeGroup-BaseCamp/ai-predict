package io.vertigo.ai.impl.structure.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.ai.structure.processor.ProcessorTypes;
import io.vertigo.ai.structure.processor.SortOrder;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;

public final class ProcessorBuilderImpl implements ProcessorBuilder {

	private List<Processor> processors;
	
	/*
	 * Constructor
	 */
	public <E extends Entity> ProcessorBuilderImpl() {
		processors = new ArrayList<Processor>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Processor> build() {
		return processors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity> ProcessorBuilder sort(String sortField, SortOrder sortOrder) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sortField", sortField);
		params.put("sortOrder", sortOrder);
		processors.add(new ProcessorImpl(processors.size(), ProcessorTypes.SORT, params));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity> ProcessorBuilder filter(Criteria<E> criteria) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("criteria", criteria);
		processors.add(new ProcessorImpl(processors.size(), ProcessorTypes.FILTER, params));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity> ProcessorBuilder join(Dataset<E> rightDataset, String onRightField, String onLeftField) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rightDataset", rightDataset);
		params.put("leftField", onLeftField);
		params.put("rightField", onRightField);
		processors.add(new ProcessorImpl(processors.size(), ProcessorTypes.JOIN, params));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity> ProcessorBuilder select(String requestedFields) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestedFields", requestedFields);
		processors.add(new ProcessorImpl(processors.size(), ProcessorTypes.SELECT, params));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity> ProcessorBuilder groupBy(String field, Agregator... aggType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("field", field);
		params.put("aggType", aggType);
		processors.add(new ProcessorImpl(processors.size(), ProcessorTypes.GROUPBY, params));
		return this;
	}

}
