package io.vertigo.ai.structure.record.definitions;

import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

public final class DatasetDefinitionSupplier implements DefinitionSupplier {

	private final String myName;
	private String myKeyConceptDtDefinitionName;
	private String myRecordDtDefinitionName;
	private String myRecordLoaderId;

	public DatasetDefinitionSupplier(final String name) {
		this.myName = name;
	}

	public DatasetDefinitionSupplier withKeyConcept(final String keyConceptDtDefinitionName) {
		this.myKeyConceptDtDefinitionName = keyConceptDtDefinitionName;
		return this;
	}

	public DatasetDefinitionSupplier withDatasetDtDefinition(final String itemDtDefinitionName) {
		this.myRecordDtDefinitionName = itemDtDefinitionName;
		return this;
	}

	public DatasetDefinitionSupplier withLoaderId(final String recordLoaderId) {
		this.myRecordLoaderId = recordLoaderId;
		return this;
	}

	@Override
	public DatasetDefinition get(final DefinitionSpace definitionSpace) {
		final DtDefinition keyConceptDtDefinition = definitionSpace.resolve(myKeyConceptDtDefinitionName, DtDefinition.class);
		final DtDefinition recordDtDefinition = definitionSpace.resolve(myRecordDtDefinitionName, DtDefinition.class);

		return new DatasetDefinition(
				myName,
				keyConceptDtDefinition,
				recordDtDefinition,
				myRecordLoaderId);
	}
	
}
