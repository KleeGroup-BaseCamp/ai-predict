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

package io.vertigo.ai.mlmodel;

import java.util.List;

import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.PredictResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.core.node.component.Manager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;

/**
 * Gestionnaire des modèles de machine learning.
 */
public interface ModelManager extends Manager {

	/**
	 * Transmet des données à une API de prediction et parse la réponse.
	 * @param data Liste d'objet à envoyer pour une prédiction
	 * @param modelName Nom du modèle
	 * @param version Version du modèle
	 * @return une PredictResponse
 	 */
	<K extends KeyConcept, D extends DtObject> PredictResponse predict(List<? extends DtObject> data, String modelName, Integer version);
	
	/**
	 * Déclenche le réentrainement d'un modèle.
	 * @param modelName Nom du modèle
	 * @param version Version du modèle
	 * @return une TrainResponse
 	 */
	TrainResponse train(String modelName, Integer version);
	
	/**
	 * Déclenche le scoring d'un modèle.
	 * @param modelName Nom du modèle
	 * @param version Version du modèle
	 * @return une AIPredictScoreResponse
 	 */
	ScoreResponse score(String modelName, Integer version);

	/**
	 * Supprime un modèle
	 * @param modelName Nom du modèle
	 * @param version Version du modèle
	 * @return le code http de la réponse du serveur
 	 */
	Integer delete(String modelName, Integer version);

	/**
	 * Active un modèle
	 * @param modelName Nom du modèle
	 * @param version Version du modèle
	 * @return le code http de la réponse du serveur
	 */
	Integer activate(String modelName, Integer version);

}
