package io.vertigo.ai.structure.dataset.definitions;

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

@DefinitionPrefix(DatasetDefinition.PREFIX)
public class DatasetDefinition extends AbstractDefinition {
	
	public static final String PREFIX = "Ds";

	private final DtDefinition datasetDtDefinition;
	private final Boolean streamed;
	
	protected DatasetDefinition(String name, DtDefinition datasetDtDefinition, Boolean streamed) {
		super(name);
		this.datasetDtDefinition = datasetDtDefinition;
		this.streamed = streamed;
	}

	public DtDefinition getDatasetDtDefinition() {
		return datasetDtDefinition;
	}

	public Boolean getStreamed() {
		return streamed;
	}
}
