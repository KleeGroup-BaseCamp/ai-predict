package io.vertigo.ai.datasetItems.definitions;

import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

public final class DatasetItemDefinitionSupplier implements DefinitionSupplier {

	private final String myName;
	private String myKeyConceptDtDefinitionName;
	private String myDatasetItemDtDefinitionName;
	private String myDatasetLoaderId;
	
	public DatasetItemDefinitionSupplier(final String name) {
		this.myName = name;
	}
	
	@Override
	public Definition get(DefinitionSpace definitionSpace) {
		
		final DtDefinition keyConceptDtDefinition = definitionSpace.resolve(myKeyConceptDtDefinitionName, DtDefinition.class);
		final DtDefinition itemDtDefinition = definitionSpace.resolve(myDatasetItemDtDefinitionName, DtDefinition.class);


		return new DatasetItemDefinition(
				myName,
				itemDtDefinition,
				false,
				myDatasetLoaderId,
				keyConceptDtDefinition);
	}
	
	public DatasetItemDefinitionSupplier withDatasetItemDtDefinition(final String datasetItemDtDefinitionName) {
		this.myDatasetItemDtDefinitionName = datasetItemDtDefinitionName;
		return this;

	}
	
	public DatasetItemDefinitionSupplier withKeyConceptDtDefinition(final String myKeyConceptDtDefinitionName) {
		this.myKeyConceptDtDefinitionName = myKeyConceptDtDefinitionName;
		return this;

	}
	
	public DatasetItemDefinitionSupplier withLoaderId(final String myDatasetLoaderId) {
		this.myDatasetLoaderId = myDatasetLoaderId;
		return this;
	}
}
