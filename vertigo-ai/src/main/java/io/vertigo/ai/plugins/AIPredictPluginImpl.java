package io.vertigo.ai.plugins;

import java.util.List;
import javax.inject.Inject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import io.vertigo.ai.impl.PredictionPlugin;
import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.param.ParamValue;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class AIPredictPluginImpl implements PredictionPlugin{
	
	private String server;
	
	@Inject
	public AIPredictPluginImpl(@ParamValue("server.name") String serverURL) {
		this.server = serverURL;
	}
	
	@Override
	public <K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version) {
		
		ClientConfig clientConfig = new DefaultClientConfig();
		 
        clientConfig.getFeatures().put(
                JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        Client client = Client.create(clientConfig);

        WebResource webResource = client
                .resource(server+modelName+"/"+version.toString()+"/");

        ClientResponse response = webResource.accept("application/json")
                .type("application/json").post(ClientResponse.class, data.toString());

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        PredictResponse output = response.getEntity(PredictResponse.class);
        //response.close();
        
		return output;
		
	}
	
}
