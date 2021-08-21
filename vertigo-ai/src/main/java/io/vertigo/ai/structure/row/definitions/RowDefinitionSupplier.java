package io.vertigo.ai.structure.row.definitions;

import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

public final class RowDefinitionSupplier implements DefinitionSupplier {

	private final String myName;
	private String myKeyConceptDtDefinitionName;
	private String myDatasetItemDtDefinitionName;
	private String myDatasetLoaderId;
	
	public RowDefinitionSupplier(final String name) {
		this.myName = name;
	}

	public Definition get(DefinitionSpace definitionSpace) {
		
		final DtDefinition keyConceptDtDefinition = definitionSpace.resolve(myKeyConceptDtDefinitionName, DtDefinition.class);
		final DtDefinition itemDtDefinition = definitionSpace.resolve(myDatasetItemDtDefinitionName, DtDefinition.class);
		
		return new RowDefinition(
				myName,
				itemDtDefinition,
				myDatasetLoaderId,
				keyConceptDtDefinition);
	}
	
	public RowDefinitionSupplier withDatasetItemDtDefinition(final String datasetItemDtDefinitionName) {
		this.myDatasetItemDtDefinitionName = datasetItemDtDefinitionName;
		return this;

	}
	
	public RowDefinitionSupplier withKeyConceptDtDefinition(final String myKeyConceptDtDefinitionName) {
		this.myKeyConceptDtDefinitionName = myKeyConceptDtDefinitionName;
		return this;

	}
	
	public RowDefinitionSupplier withLoaderId(final String myDatasetLoaderId) {
		this.myDatasetLoaderId = myDatasetLoaderId;
		return this;
	}

}
