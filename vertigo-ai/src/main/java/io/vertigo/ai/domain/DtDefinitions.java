package io.vertigo.ai.domain;

import java.util.Arrays;
import java.util.Iterator;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.definitions.DtFieldName;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class DtDefinitions implements Iterable<Class<?>> {

	/**
	 * Enumération des DtDefinitions.
	 */
	public enum Definitions {
		/** Objet de données DeployPredict. */
		DeployPredict(io.vertigo.ai.train.models.DeployPredict.class),
		/** Objet de données DeployResponse. */
		DeployResponse(io.vertigo.ai.predict.models.DeployResponse.class),
		/** Objet de données Explanation. */
		Explanation(io.vertigo.ai.predict.models.Explanation.class),
		/** Objet de données Predict. */
		Predict(io.vertigo.ai.predict.models.Predict.class),
		/** Objet de données PredictResponse. */
		PredictResponse(io.vertigo.ai.predict.models.PredictResponse.class),
		/** Objet de données ScoreResponse. */
		ScoreResponse(io.vertigo.ai.train.models.ScoreResponse.class),
		/** Objet de données TrainResponse. */
		TrainResponse(io.vertigo.ai.train.models.TrainResponse.class)		;

		private final Class<?> clazz;

		private Definitions(final Class<?> clazz) {
			this.clazz = clazz;
		}

		/** 
		 * Classe associée.
		 * @return Class d'implémentation de l'objet 
		 */
		public Class<?> getDtClass() {
			return clazz;
		}
	}

	/**
	 * Enumération des champs de DeployPredict.
	 */
	public enum DeployPredictFields implements DtFieldName<io.vertigo.ai.train.models.DeployPredict> {
		/** Propriété 'Status'. */
		status,
		/** Propriété 'Response'. */
		response	}

	/**
	 * Enumération des champs de DeployResponse.
	 */
	public enum DeployResponseFields implements DtFieldName<io.vertigo.ai.predict.models.DeployResponse> {
		/** Propriété 'Model ID'. */
		id,
		/** Propriété 'Model name'. */
		name,
		/** Propriété 'Model version'. */
		version,
		/** Propriété 'Model is activated'. */
		activated,
		/** Propriété 'Model is auto deployed'. */
		auto,
		/** Propriété 'Error message'. */
		error	}

	/**
	 * Enumération des champs de Explanation.
	 */
	public enum ExplanationFields implements DtFieldName<io.vertigo.ai.predict.models.Explanation> {
		/** Propriété 'Explanation Item'. */
		explainFeature	}

	/**
	 * Enumération des champs de Predict.
	 */
	public enum PredictFields implements DtFieldName<io.vertigo.ai.predict.models.Predict> {
		/** Propriété 'Prediction label'. */
		predictionLabel,
		/** Propriété 'Prediction numeric'. */
		predictionNumeric,
		/** Propriété 'Prediction vector'. */
		predictionVector,
		/** Propriété 'Prediction probabilities'. */
		predictionProba,
		/** Propriété '1D Explanation'. */
		explanation1D,
		/** Propriété '2D Explanation'. */
		explanation2D	}

	/**
	 * Enumération des champs de PredictResponse.
	 */
	public enum PredictResponseFields implements DtFieldName<io.vertigo.ai.predict.models.PredictResponse> {
		/** Propriété 'Prediction List'. */
		predictionList	}

	/**
	 * Enumération des champs de ScoreResponse.
	 */
	public enum ScoreResponseFields implements DtFieldName<io.vertigo.ai.train.models.ScoreResponse> {
		/** Propriété 'Model name'. */
		modelName,
		/** Propriété 'Model version'. */
		version,
		/** Propriété 'Model score'. */
		score,
		/** Propriété 'Request duration'. */
		time,
		/** Propriété 'Status'. */
		status	}

	/**
	 * Enumération des champs de TrainResponse.
	 */
	public enum TrainResponseFields implements DtFieldName<io.vertigo.ai.train.models.TrainResponse> {
		/** Propriété 'Request duration'. */
		time,
		/** Propriété 'Model name'. */
		modelName,
		/** Propriété 'Model version'. */
		version,
		/** Propriété 'Model score'. */
		score,
		/** Propriété 'Status'. */
		status,
		/** Propriété 'Response'. */
		response,
		/** Propriété 'Deploy to prediction'. */
		deploy	}

	/** {@inheritDoc} */
	@Override
	public Iterator<Class<?>> iterator() {
		return new Iterator<>() {
			private Iterator<Definitions> it = Arrays.asList(Definitions.values()).iterator();

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			/** {@inheritDoc} */
			@Override
			public Class<?> next() {
				return it.next().getDtClass();
			}
		};
	}
}
