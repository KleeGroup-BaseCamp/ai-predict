package io.vertigo.ai.impl;

import java.util.Optional;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasets.DatasetManager;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

public class DatasetManagerImpl implements DatasetManager{


	@Override
	public DatasetItemDefinition findFirstDatasetItemDefinitionByKeyConcept(
			final Class<? extends KeyConcept> keyConceptClass) {
		final Optional<DatasetItemDefinition> indexDefinition = findFirstDatasetDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(indexDefinition.isPresent(), "No DatasetItemDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return indexDefinition.get();
	}
	
	private static Optional<DatasetItemDefinition> findFirstDatasetDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetItemDefinition.class).stream()
				.filter(datasetDefinition -> datasetDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.findFirst();
	}

	
	@Override
	public DatasetDefinition findDatasetDefinition(final String dtName) {
		final Optional<DatasetDefinition> datasetDefinition = findDatasetDefinitionOpt(dtName);
		Assertion.check().isTrue(datasetDefinition.isPresent(), "No dataset was defined with this name : {0}", dtName);
		return datasetDefinition.get();
	}

	private static Optional<DatasetDefinition> findDatasetDefinitionOpt(final String dtName) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class).stream()
				.filter(datasetDefinition -> datasetDefinition.getName().equals(dtName))
				.findFirst();
	}
}
