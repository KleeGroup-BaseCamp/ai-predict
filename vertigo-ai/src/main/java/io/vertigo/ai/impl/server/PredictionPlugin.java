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

import java.util.List;

import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.core.node.component.Plugin;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Plugin d'appel aux API des serveurs d'ntrainement et de prediction.
 */
public interface PredictionPlugin extends Plugin {

	/**
	 * Retourne une prédiction à partir d'une liste d'objet
	 * @param <D> Type d'objet
	 * @param data liste d'objet
	 * @param modelName nom du modèle à utiliser
	 * @param version version du modèle à utiliser
	 * @return une PredictResponse contenant une prédiction et une explication
	 */
	<D extends DtObject> PredictResponse predict(List<D> data, String modelName, Integer version);
	
	/**
	 * Lance l'entrainement d'un modèle
	 * @param <K> 
	 * @param modelName nom du modèle à entrainer
	 * @param version version du modèle à entrainer
	 * @return une TrainResponse
	 */
	TrainResponse train(String modelName, Integer version);
	
	/**
	 * Lance le scoring d'un modèle
	 * @param <K> 
	 * @param modelName nom du modèle à scorer
	 * @param version version du modèle à scorer
	 * @return une AIPredictScoreResponse
	 */
	ScoreResponse score(String modelName, Integer version);

	/**
	 * Lance la suppression d'un modèle
	 * @param <K> 
	 * @param modelName nom du modèle à supprimer
	 * @param version version du modèle à supprimer
	 * @return le code http de la réponse serveur
	 */
	Integer delete(String modelName, Integer version);
	
	
	/**
	 * 
	 * @param modelName
	 * @param version
	 * @return
	 */
	void activate(String modelName, Integer version);

}
