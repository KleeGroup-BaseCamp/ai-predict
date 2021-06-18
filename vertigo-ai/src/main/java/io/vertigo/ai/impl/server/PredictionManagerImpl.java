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

package io.vertigo.ai.impl.server;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.server.models.AIPredictScoreResponse;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Implémentation standard du gestionnaire des API des serveurs d'entrainement et de prédiction.
 */
public class PredictionManagerImpl implements ModelManager {
	
	private final PredictionPlugin predictionPlugin;
	
	/**
	 * Constructor.
	 * @param predictionPlugin the predictionPlugin
	 */
	@Inject
	public PredictionManagerImpl(PredictionPlugin predictionPlugin) {
		this.predictionPlugin = predictionPlugin;
	}

	/** {@inheritDoc} */
	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version) {
		return predictionPlugin.predict(data, modelName, version);
	}
	
	/** {@inheritDoc} */
	@Override
	public  TrainResponse train(String modelName, Integer version) {
		return predictionPlugin.train(modelName, version);
	}

	/** {@inheritDoc} */
	@Override
	public AIPredictScoreResponse score(String modelName, Integer version) {
		return predictionPlugin.score(modelName, version);
	}

	/** {@inheritDoc} */
	@Override
	public Integer delete(String modelName, Integer version) {
		return predictionPlugin.delete(modelName, version);
	}
	
}
