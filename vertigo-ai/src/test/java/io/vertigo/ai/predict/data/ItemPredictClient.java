package io.vertigo.ai.predict.data;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinitionSupplier;
import io.vertigo.ai.structure.row.definitions.RowDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public class ItemPredictClient implements Component, DefinitionProvider {

	
	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();
		definitions.add(
				new RowDefinitionSupplier("DsRIrisItem")
				.withKeyConceptDtDefinition("DtIrisItem")
				.withDatasetItemDtDefinition("DtIrisItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsIrisDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		definitions.add(
				new RowDefinitionSupplier("DsRBostonItem")
				.withKeyConceptDtDefinition("DtBostonItem")
				.withDatasetItemDtDefinition("DtBostonItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsBostonDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		definitions.add(
				new RowDefinitionSupplier("DsRBostonRegressionItem")
				.withKeyConceptDtDefinition("DtBostonRegressionItem")
				.withDatasetItemDtDefinition("DtBostonRegressionItem")
				.withLoaderId("ItemSearchLoader"));
		definitions.add(
				new DatasetDefinitionSupplier("DsBostonRegressionDataset")
				.withDatasetDtDefinition("DtDatasetObject"));
		return definitions;
	}

}
