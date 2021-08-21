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

package io.vertigo.ai.plugins;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.impl.server.PredictionPlugin;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.structure.model.DtObject;

/**
 * Implémentation standard du plugin de connection à AIPredict.
 */
public class AIPredictPluginImpl implements PredictionPlugin {
		
	private String server;
	
	@Inject
	private AIPredictClientWebServices aIPredictClientWebServices;
	
	@Inject
	public AIPredictPluginImpl(@ParamValue("server.name") String serverName) {
		this.server = serverName;
	}
	
	/** {@inheritDoc} */
	@Override
	public <D extends DtObject> PredictResponse predict(List<D> data, String modelName, Integer version) {
		return aIPredictClientWebServices.predict((List<DtObject>) data, modelName, version);
	}
	
	/** {@inheritDoc} */
	@Override
	public TrainResponse train(String modelName, Integer version) {		
		return aIPredictClientWebServices.train(modelName, version);
	}

	/** {@inheritDoc} */
	@Override
	public ScoreResponse score(String modelName, Integer version) {
		return aIPredictClientWebServices.score(modelName, version);
	}

	/** {@inheritDoc} */
	@Override
	public Integer delete(String modelName, Integer version) {
		return aIPredictClientWebServices.delete(modelName, version);
	}
	
	/** {@inheritDoc} */
	@Override
	public void activate(String modelName, Integer version) {
		aIPredictClientWebServices.activate(modelName, version);
	}
	
}
