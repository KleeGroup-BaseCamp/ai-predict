package io.vertigo.ai.structure.processor;

import java.util.Map;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

public interface DatasetProcessingPlugin extends Plugin {

	/**
	 * Execute the projection processor
	 * @param <E> the dataset type
	 * @param dataset the dataset to process
	 * @param params the parameters of the processor
	 * @return the resulting dataset
	 */
	<E extends Entity> Dataset<E> select(Dataset<E> dataset, Map<String, Object> params);
	
	/**
	 * Execute the join processor
	 * @param <E> the dataset type
	 * @param dataset the dataset to process
	 * @param params the parameters of the processor
	 * @return the resulting dataset
	 */
	<E extends Entity> Dataset<E> join(Dataset<E> dataset, Map<String, Object> params);

	/**
	 * Execute the agregation processor
	 * @param <E> the dataset type
	 * @param dataset the dataset to process
	 * @param params the parameters of the processor
	 * @return the resulting dataset
	 */
	<E extends Entity> Dataset<E> group(Dataset<E> dataset, Map<String, Object> params);

	/**
	 * Build the dataset from an intermediate dataset
	 * @param <E> the dataset type
	 * @param inputDataset the dataset to process
	 * @param outputDataset the output dataset
	 * @return the outputDataset dataset
	 */
	<E extends Entity, F extends DtObject> Dataset<E> build(Dataset<E> inputDataset, Dataset<F> outputDataset);
}
