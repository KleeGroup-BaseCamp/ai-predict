package io.vertigo.ai.plugins;

import javax.inject.Inject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import io.vertigo.ai.impl.PredictionPlugin;
import io.vertigo.ai.predict.PredictionEntity;
import io.vertigo.core.param.ParamValue;

public class AIPredictPluginImpl implements PredictionPlugin{
	
	private String server;
	@Inject
	public AIPredictPluginImpl(@ParamValue("server.name") String serverURL) {
		this.server = serverURL;
	}
	
	@Override
	public PredictionEntity predict(String data) {
		
		ClientConfig clientConfig = new DefaultClientConfig();
		 
        clientConfig.getFeatures().put(
                JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);

        WebResource webResource = client
                .resource(server);

        ClientResponse response = webResource.accept("application/json")
                .type("application/json").post(ClientResponse.class, data);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        PredictionEntity output = response.getEntity(PredictionEntity.class);


		return output;
	}
	
}
