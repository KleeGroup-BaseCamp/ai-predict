package io.vertigo.ai.impl.structure.processor;

import java.util.Map;

import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.datamodel.structure.model.Entity;

public final class ProcessorImpl implements Processor {

	private String type;
	private Map<String, Object> params;
	private int order;
	
	public <E extends Entity> ProcessorImpl(final int processingOrder,final String type, final Map<String, Object> params) {
		this.order = processingOrder;
		this.type = type;
		this.params = params;
	}

	@Override
	public String getProcessorType() {
		return type;
	}

	@Override
	public Map<String, Object> getProcessorParameters() {
		return params;
	}

	@Override
	public int getProcessingOrder() {
		return order;
	}
	

}
