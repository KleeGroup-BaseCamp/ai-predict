package io.vertigo.ai.structure;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.Entity;

public interface DatasetManager extends Manager{
	
	String MAIN_DATA_SPACE_NAME = "main";

	ProcessorBuilder createBuilder();
	
	<E extends Entity> Dataset<?> executeProcessing(final Dataset<E> dataset, final List<Processor> processor);
}