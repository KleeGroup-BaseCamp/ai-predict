package io.vertigo.ai.structure.dataset;

import java.util.List;

import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.DtObject;

public interface Dataset<E extends DtObject> {
	
	/**
	 * Get item definition
	 * @return DtDefinition
	 */
	DtDefinition getItemDefinition();
	
	/**
	 * Collect attached data for in memory processing 
	 * @param <O>
	 * @param data
	 */
	List<E> collect();

	void load(List<E> data);

}
