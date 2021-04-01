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
				new DatasetItemDefinitionSupplier("DsIIrisItem")
				.withKeyConceptDtDefinition("DtIrisItem")
				.withDatasetItemDtDefinition("DtIrisItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsIrisDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		definitions.add(
				new DatasetItemDefinitionSupplier("DsIBostonItem")
				.withKeyConceptDtDefinition("DtBostonItem")
				.withDatasetItemDtDefinition("DtBostonItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsBostonDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		definitions.add(
				new DatasetItemDefinitionSupplier("DsIBostonRegressionItem")
				.withKeyConceptDtDefinition("DtBostonRegressionItem")
				.withDatasetItemDtDefinition("DtBostonRegressionItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsBostonRegressionDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		return definitions;
	}

}
