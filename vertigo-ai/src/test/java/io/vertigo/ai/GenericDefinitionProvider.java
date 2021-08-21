package io.vertigo.ai;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.structure.record.definitions.RecordDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public final class GenericDefinitionProvider implements Component, DefinitionProvider  {
	
	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();
		definitions.add(
				new RecordDefinitionSupplier("DsRIrisItem")
				.withKeyConcept("DtIris")
				.withDatasetDtDefinition("DtIris")
				.withLoaderId("DatasetLoader"));
		return definitions;
	}


}
