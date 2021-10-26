package io.vertigo.ai.structure.processor;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.graph.DAG;
import io.vertigo.ai.structure.processor.graph.DAGBuilder;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

public interface ProcessorDAGManager extends Manager {
	
	String MAIN_DATA_SPACE_NAME = "main";

	/**
	 * Initialize a ProcessorBuilder
	 * @return a ProcessorBuilder
	 */
	DAGBuilder createBuilder();
	
	/**
	 * 
	 * @param <E>
	 * @param <F>
	 * @param dag
	 * @return
	 */
	<E extends Entity> Dataset<?> executeProcessing(DAG dag, Dataset<E> outputDataset);
}
