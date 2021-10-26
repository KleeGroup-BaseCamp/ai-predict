package io.vertigo.ai.structure.processor;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

public interface ProcessorManager extends Manager {
	
	String MAIN_DATA_SPACE_NAME = "main";

	/**
	 * Initialize a ProcessorBuilder
	 * @return a ProcessorBuilder
	 */
	ProcessorBuilder createBuilder();
	
	/**
	 * Execute all the processor of a processing list
	 * @param <E> the input dataset item definition
	 * @param <F> the output dataset item definition
	 * @param inputDataset the input dataset
	 * @param outputDataset the output dataset
	 * @param processors the processors list
	 * @return the outputDataset
	 */
	<E extends Entity, F extends DtObject> Dataset<?> executeProcessing(final Dataset<E> inputDataset, Dataset<F> outputDataset, final List<Processor> processors);
	
}
