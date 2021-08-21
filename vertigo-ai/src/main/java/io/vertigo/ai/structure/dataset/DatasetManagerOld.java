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

package io.vertigo.ai.structure.dataset;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Gestionnaire des datasets en mémoire.
 */
public interface DatasetManagerOld extends Manager{
	
	/**
	 * Find RowDefinition for a keyConcept. It must be one and only one RowDefinition.
	 * @param keyConceptClass keyConcept class
	 * @return RowDefinition for this keyConcept (not null)
	 */
	RowDefinition findFirstRowDefinitionByKeyConcept(final Class<? extends KeyConcept> keyConceptClass);
	
	/**
	 * Find DatasetDefinition for a keyConcept. It must be one and only one DatasetDefinition.
	 * @param dtName Name of the DatasetDefinition
	 * @return DatasetDefinition for this keyConcept (not null)
	 */
	DatasetDefinition findDatasetDefinition(final String dtName);
}
