package io.vertigo.ai.structure.processor;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.structure.model.Entity;

public interface ProcessorBuilder {

	/**
	 * Build the list of processors from the ProcessorBuilder
	 * @return a list of processor
	 */
	List<Processor> build();
	
	/**
	 * Add a sort processor to the processing list
	 * @param <E> Dataset object type
	 * @param sortField field to sort the dataset by
	 * @param sortOrder sort the dataset by ascending or descending sortField values
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder sort(final String sortField, final SortOrder sortOrder);
	
	/**
	 * Add a filter processor to the processing list
	 * @param <E> Dataset object type
	 * @param criteria the filter criteria
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder filter(final Criteria<E> criteria);
	
	/**
	 * Add a join processor to the processing list
	 * @param <E> Dataset object type
	 * @param rightDataset the Dataset to join with
	 * @param onRightField the rightDataset key field
	 * @param onLeftField the left Dataset key field
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder join(final Dataset<E> rightDataset, final String onRightField, final String onLeftField);
	
	/**
	 * Add a projection processor to the processing list
	 * @param <E> Dataset object type
	 * @param requestedFields requested fields separted with a comma
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder select(final String requestedFields);

	/**
	 * Add a agregation processor to the processing list
	 * @param <E> Dataset object type
	 * @param field the field to group the dataset by
	 * @param aggType the agregation type
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder groupBy(final String field, AgregatorType... aggType);

	
	/**
	 * Add a pivot processor to the processing list
	 * @param <E> Dataset object type
	 * @param field the field to group the dataset by
	 * @param aggType the agregation type
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder pivot(final String pivotColumn, final List<String> pivotStaticValues, final List<String> rowIdfields, List<Agregator> aggs);

	/**
	 * Add a pivot processor to the processing list
	 * @param <E> Dataset object type
	 * @param field the field to group the dataset by
	 * @param aggType the agregation type
	 * @return this ProcessorBuilder
	 */
	<E extends Entity> ProcessorBuilder window(final List<String> fields, final List<Window> windows, List<Agregator> aggs);

}
