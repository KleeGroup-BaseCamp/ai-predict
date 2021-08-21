package io.vertigo.ai.plugins;

import java.util.List;

import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.core.node.component.Amplifier;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.impl.webservice.client.WebServiceProxyAnnotation;
import io.vertigo.vega.webservice.stereotype.DELETE;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@WebServiceProxyAnnotation
@PathPrefix("/api")
public interface AIPredictClientWebServices extends Amplifier {
	
	@POST("/predict/{modelName}/{version}/")
	PredictResponse predict(List<DtObject> data, @PathParam("modelName") final String modelName, @PathParam("version") final Integer version);

	@PUT("/activate/{modelName}/{version}/")
	//TODO: ActivateResponse
	void activate(@PathParam("modelName") final String modelName, @PathParam("version") final Integer valueOf);
	
	@POST("/train/{modelName}/{version}/")
	public TrainResponse train(@PathParam("modelName") final String modelName, @PathParam("version") final Integer version);
	
	@POST("/score/{modelName}/{version}/")
	public ScoreResponse score(@PathParam("modelName") final String modelName, @PathParam("version") final Integer version);
	
	@DELETE("/delete-train/{modelName}/{version}/")
	public Integer delete(@PathParam("modelName") final String modelName, @PathParam("version") final Integer version);
	
	
}