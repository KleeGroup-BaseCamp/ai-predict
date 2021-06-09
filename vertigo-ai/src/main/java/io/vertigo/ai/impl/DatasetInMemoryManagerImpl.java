package io.vertigo.ai.impl;

import java.util.Optional;

import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

public class DatasetInMemoryManagerImpl implements DatasetManager{


	@Override
	public RowDefinition findFirstRowDefinitionByKeyConcept(
			final Class<? extends KeyConcept> keyConceptClass) {
		final Optional<RowDefinition> indexDefinition = findFirstDatasetDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(indexDefinition.isPresent(), "No RowDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return indexDefinition.get();
	}
	
	private static Optional<RowDefinition> findFirstDatasetDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(RowDefinition.class).stream()
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
