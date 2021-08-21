package io.vertigo.ai.example.iris;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.structure.record.definitions.RecordDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public class ItemDefinitionProvider implements Component, DefinitionProvider {

	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();
		definitions.add(
				new RecordDefinitionSupplier("DsIris")
				.withKeyConcept("DtIris")
				.withDatasetDtDefinition("DtIrisTrain")
				.withLoaderId("RecordLoader"));
		return definitions;
	}

}
