package io.vertigo.ai.impl.structure.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;

public final class ProcessorBuilderImpl implements ProcessorBuilder {

	private List<Processor> processors;
	
	public <E extends Entity> ProcessorBuilderImpl() {
		processors = new ArrayList<Processor>();
	}
	
	@Override
	public List<Processor> build() {
		return processors;
	}

	@Override
	public <E extends Entity> ProcessorBuilder sort(String sortField, String sortOrder) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sortField", sortField);
		params.put("sortOrder", sortOrder);
		processors.add(new ProcessorImpl(processors.size(), "sort", params));
		return this;
	}

	@Override
	public <E extends Entity> ProcessorBuilder filter(Criteria<E> criteria) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("criteria", criteria);
		processors.add(new ProcessorImpl(processors.size(), "filter", params));
		return this;
	}

	@Override
	public <E extends Entity> ProcessorBuilder join(Dataset<E> rightDataset, String onRightField, String onLeftField) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rightDataset", rightDataset);
		params.put("leftField", onLeftField);
		params.put("rightField", onRightField);
		processors.add(new ProcessorImpl(processors.size(), "join", params));
		return this;
	}

	@Override
	public <E extends Entity> ProcessorBuilder select(String requestedFields) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("requestedFields", requestedFields);
		processors.add(new ProcessorImpl(processors.size(), "join", params));
		return this;
	}

}
