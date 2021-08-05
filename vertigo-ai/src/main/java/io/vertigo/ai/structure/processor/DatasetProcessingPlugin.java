package io.vertigo.ai.structure.processor;

import java.util.Map;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.Entity;

public interface DatasetProcessingPlugin extends Plugin {

	<E extends Entity> Dataset<E> select(Dataset<E> dataset, Map<String, Object> params);
	
	<E extends Entity> Dataset<E> join(Dataset<E> dataset, Map<String, Object> params);
	
}
