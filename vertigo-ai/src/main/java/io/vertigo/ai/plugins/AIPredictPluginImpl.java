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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.vertigo.ai.impl.server.PredictionPlugin;
import io.vertigo.ai.server.models.AIPredictScoreResponse;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Implémentation standard du plugin de connection à AIPredict.
 */
public class AIPredictPluginImpl implements PredictionPlugin {
		
	private String server;
	
	@Inject
	public AIPredictPluginImpl(@ParamValue("server.name") String serverName) {
		this.server = serverName;
	}
	
	/** {@inheritDoc} */
	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<D> data, String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server + "api/predict/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Entity<String> test = Entity.entity(data.toString(), MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(test);
		PredictResponse output = response.readEntity(PredictResponse.class);	
		response.close();
		return output;
	}
	
	/** {@inheritDoc} */
	@Override
	public TrainResponse train(String modelName, Integer version) {		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"api/train/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(null);
		TrainResponse output = response.readEntity(TrainResponse.class);	
		response.close();
		return output;
	}

	/** {@inheritDoc} */
	@Override
	public AIPredictScoreResponse score(String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"api/score/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(null);
		AIPredictScoreResponse output = response.readEntity(AIPredictScoreResponse.class);
		response.close();
		return output;
	}

	/** {@inheritDoc} */
	@Override
	public Integer delete(String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"api/delete-train/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.delete();
		Integer output = response.getStatus();
		response.close();
		return output;
	}
	
	/** {@inheritDoc} */
	@Override
	public Integer activate(String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"api/activate/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Entity<String> test = Entity.entity("", MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.put(test);
		Integer output = response.getStatus();
		response.close();
		return output;
	}
	
}
