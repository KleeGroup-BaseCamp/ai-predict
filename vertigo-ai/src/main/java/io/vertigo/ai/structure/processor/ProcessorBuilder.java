package io.vertigo.ai.structure.processor;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;

public interface ProcessorBuilder {

	List<Processor> build();
	
	<E extends Entity> ProcessorBuilder sort(final String sortField, final String sortOrder);
	
	<E extends Entity> ProcessorBuilder filter(final Criteria<E> criteria);
	
	<E extends Entity> ProcessorBuilder join(final Dataset<E> rightDataset, final String onRightField, final String onLeftField);
	
	<E extends Entity> ProcessorBuilder select(final String requestedFields);
}
