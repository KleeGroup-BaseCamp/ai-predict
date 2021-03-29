package io.vertigo.ai.data;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinitionSupplier;
import io.vertigo.ai.datasets.definitions.DatasetDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public class ItemPredictClient implements Component, DefinitionProvider {

	
	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();
		definitions.add(
				new DatasetItemDefinitionSupplier("DsIItem")
				.withKeyConceptDtDefinition("DtItem")
				.withDatasetItemDtDefinition("DtItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsDatasetObject")
				.withDatasetDtDefinition("DtDatasetObject"));
		return definitions;
	}

}
