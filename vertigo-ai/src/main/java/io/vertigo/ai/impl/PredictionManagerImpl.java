package io.vertigo.ai.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

public class PredictionManagerImpl implements PredictionManager {
	
	private final PredictionPlugin predictionPlugin;
	
	@Inject
	public PredictionManagerImpl(PredictionPlugin predictionPlugin) {
		this.predictionPlugin = predictionPlugin;
	}


	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<DatasetItem<K, D>> data) {
		return predictionPlugin.predict(data);
	}


	@Override
	public DatasetItemDefinition findFirstDatasetDefinitionByKeyConcept(
			final Class<? extends KeyConcept> keyConceptClass) {
		final Optional<DatasetItemDefinition> indexDefinition = findFirstDatasetDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(indexDefinition.isPresent(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return indexDefinition.get();
	}
	
	private static Optional<DatasetItemDefinition> findFirstDatasetDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetItemDefinition.class).stream()
				.filter(datasetDefinition -> datasetDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.findFirst();
	}
	
	
}
