package io.vertigo.ai.datasets.definitions;

import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

public final class DatasetDefinitionSupplier implements DefinitionSupplier {

	private String myName;
	private String myDatasetDtDefinitionName;

	public DatasetDefinitionSupplier(final String name) {
		this.myName = name;
	}
	
	@Override
	public Definition get(DefinitionSpace definitionSpace) {
		final DtDefinition datasetDtDefinition = definitionSpace.resolve(myDatasetDtDefinitionName, DtDefinition.class);
		return new DatasetDefinition(myName, datasetDtDefinition, false);
	}
	
	public DatasetDefinitionSupplier withDatasetDtDefinition(final String datasetDtDefinitionName) {
		this.myDatasetDtDefinitionName = datasetDtDefinitionName;
		return this;

	}


}
