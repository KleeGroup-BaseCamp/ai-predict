package io.vertigo.ai.example.heroes;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public class HeroesDefinitionProvider implements Component, DefinitionProvider {

	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();
		definitions.add(
				new DatasetDefinitionSupplier("DsHeroes")
				.withDatasetDtDefinition("DtHeroe"));
		definitions.add(
				new DatasetDefinitionSupplier("DsFactions")
				.withDatasetDtDefinition("DtFaction"));
		definitions.add(
				new DatasetDefinitionSupplier("DsEras")
				.withDatasetDtDefinition("DtEra"));
		return definitions;
	}

}
