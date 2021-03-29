package io.vertigo.ai.predict;

import java.util.List;

import io.vertigo.ai.predict.models.PredictResponse;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

public interface PredictionManager extends Manager {

	/**
	 * Transmet des données à une API de prediction et parse la réponse.
	 */
	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data);
}
