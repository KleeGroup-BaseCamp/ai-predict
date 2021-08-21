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

package io.vertigo.ai;

import io.vertigo.ai.impl.server.PredictionManagerImpl;
import io.vertigo.ai.impl.structure.dataset.DatasetManagerImpl;
import io.vertigo.ai.impl.structure.record.RecordManagerImpl;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.plugins.AIPredictClientWebServices;
import io.vertigo.ai.plugins.AIPredictPluginImpl;
import io.vertigo.ai.plugins.SqlDatasetProcessingPluginImpl;
import io.vertigo.ai.server.domain.AISmartTypes;
import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.record.RecordManager;
import io.vertigo.core.node.config.DefinitionProviderConfig;
import io.vertigo.core.node.config.Feature;
import io.vertigo.core.node.config.Features;
import io.vertigo.core.param.Param;
import io.vertigo.datamodel.impl.smarttype.ModelDefinitionProvider;

/**
 * Defines AI features.
 *
 */
public class AIFeatures extends Features<AIFeatures>{

	/**
	 * Constructor.
	 */
	public AIFeatures() {
		super("vertigo-ai");
	}

	@Override
	protected void buildFeatures() {
		getModuleConfigBuilder().addComponent(ModelManager.class, PredictionManagerImpl.class);
		getModuleConfigBuilder().addComponent(RecordManager.class, RecordManagerImpl.class);
		getModuleConfigBuilder().addComponent(DatasetManager.class, DatasetManagerImpl.class);
		getModuleConfigBuilder().addAmplifier(AIPredictClientWebServices.class);
		getModuleConfigBuilder().addDefinitionProvider(DefinitionProviderConfig.builder(ModelDefinitionProvider.class)
				.addDefinitionResource("smarttypes", AISmartTypes.class.getName())
				.addDefinitionResource("dtobjects", "io.vertigo.ai.server.domain.DtDefinitions")
				.build());
	}
	

	@Feature("predict.AIPredict")
	public AIFeatures withAIPredictBackend(final Param... params) {
		getModuleConfigBuilder().addPlugin(AIPredictPluginImpl.class, params);
		return this;
	}
	
	@Feature("processing.SqlProcessing")
	public AIFeatures withSqlProcessing(final Param...params) {
		getModuleConfigBuilder().addPlugin(SqlDatasetProcessingPluginImpl.class, params);
		return this;
	}

}
