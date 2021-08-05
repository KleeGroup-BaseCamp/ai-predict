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

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

/**
 * Définition du dataset.
 */
@DefinitionPrefix(DatasetDefinition.PREFIX)
public class DatasetDefinition extends AbstractDefinition {
	
	public static final String PREFIX = "Ds";

	private final DtDefinition itemDtDefinition;
	private final Boolean streamed;
	
	/**
	 * Constructor.
	 * @param name Dataset name
	 * @param itemDtDefinition DtDefinition associé au dataset
	 * @param streamed Boolean idiquant si le dataset est streamé.
	 */
	public DatasetDefinition(String name, DtDefinition itemDtDefinition, Boolean streamed) {
		super(name);
		this.itemDtDefinition = itemDtDefinition;
		this.streamed = streamed;
	}

	/**
	 * Définition de l'objet représentant le contenu du dataset.
	 * @return Définition des champs enregistrés.
	 */
	public DtDefinition getItemDtDefinition() {
		return itemDtDefinition;
	}

	/**
	 * @return True si le dataset est streamé.
	 */
	public Boolean isStreamed() {
		return streamed;
	}
}
