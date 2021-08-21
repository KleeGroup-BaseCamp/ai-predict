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

package io.vertigo.ai.structure.dataset.definitions;

import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

public class DatasetDefinitionSupplier implements DefinitionSupplier {

	private String myName;
	private String myDatasetDtDefinitionName;

	public DatasetDefinitionSupplier(final String name) {
		this.myName = name;
	}
	
	public Definition get(DefinitionSpace definitionSpace) {
		final DtDefinition datasetDtDefinition = definitionSpace.resolve(myDatasetDtDefinitionName, DtDefinition.class);
		return new DatasetDefinition(myName, datasetDtDefinition, false);
	}
	
	public DatasetDefinitionSupplier withDatasetDtDefinition(final String datasetDtDefinitionName) {
		this.myDatasetDtDefinitionName = datasetDtDefinitionName;
		return this;

	}
}
