/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2021, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertigo.ai.impl.structure.datasetInMemory;

import java.util.Optional;

import io.vertigo.ai.structure.dataset.DatasetManagerOld;
import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * Implémentation standard du gestionnaire des datasets en mémoire.
 */
public final class DatasetInMemoryManagerImpl implements DatasetManagerOld{


	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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
