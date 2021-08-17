package io.vertigo.ai.impl.structure.processor;

import java.util.Map;

import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorTypes;
import io.vertigo.datamodel.structure.model.Entity;

public final class ProcessorImpl implements Processor {

	private ProcessorTypes type;
	private Map<String, Object> params;
	private int order;
	
	/**
	 * Constructor
	 * @param <E>
	 * @param processingOrder the index of the processor in the processing list
	 * @param type the type of processor
	 * @param params the processor parameters
	 */
	public <E extends Entity> ProcessorImpl(final int processingOrder,final ProcessorTypes type, final Map<String, Object> params) {
		this.order = processingOrder;
		this.type = type;
		this.params = params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessorTypes getProcessorType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProcessorParameters() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getProcessingOrder() {
		return order;
	}
	

}
