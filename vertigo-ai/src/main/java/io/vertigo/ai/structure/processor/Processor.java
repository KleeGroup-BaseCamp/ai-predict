package io.vertigo.ai.structure.processor;

import java.util.Map;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.datamodel.structure.model.Entity;

public interface Processor {

	String getProcessorType();
	
	Map<String, Object> getProcessorParameters();
	
	int getProcessingOrder();
}
