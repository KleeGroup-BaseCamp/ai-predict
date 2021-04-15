package io.vertigo.ai.plugins;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.impl.PredictionPlugin;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.ai.train.models.ScoreResponse;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;



public class AIPredictPluginImpl implements PredictionPlugin {
	
	private static final Logger LOG = LogManager.getLogger(AIPredictPluginImpl.class);
	
	private String server;
	
	@Inject
	public AIPredictPluginImpl(@ParamValue("server.name") String serverName) {
		this.server = serverName;
	}
	
	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"predict/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(data.toString(), MediaType.APPLICATION_JSON));
		PredictResponse output = response.readEntity(PredictResponse.class);	
		response.close();
		return output;
		
	}
	
	@Override
	public TrainResponse train(
			HashMap<String,Object> config) {
	
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"deploy-train/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(config, MediaType.APPLICATION_JSON));
		TrainResponse output = response.readEntity(TrainResponse.class);	
		response.close();
		return output;
	}

	@Override
	public ScoreResponse score(
			String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"score/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(null);
		ScoreResponse output = response.readEntity(ScoreResponse.class);
		response.close();
		return output;
	}

	@Override
	public Integer delete(String modelName, Integer version) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(server+"delete-train/"+modelName+"/"+version+"/");
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.delete();
		Integer output = response.getStatus();
		response.close();
		return output;
	}
	
}
