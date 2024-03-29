package io.vertigo.ai.example.domain;

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
		/** Objet de données Era. */
		Era(io.vertigo.ai.example.heroes.domain.Era.class),
		/** Objet de données EventType. */
		EventType(io.vertigo.ai.example.telstra.domain.EventType.class),
		/** Objet de données EventTypeTrain. */
		EventTypeTrain(io.vertigo.ai.example.telstra.domain.EventTypeTrain.class),
		/** Objet de données Faction. */
		Faction(io.vertigo.ai.example.heroes.domain.Faction.class),
		/** Objet de données FactionCount. */
		FactionCount(io.vertigo.ai.example.heroes.domain.FactionCount.class),
		/** Objet de données Heroe. */
		Heroe(io.vertigo.ai.example.heroes.domain.Heroe.class),
		/** Objet de données Iris. */
		Iris(io.vertigo.ai.example.iris.domain.Iris.class),
		/** Objet de données IrisTrain. */
		IrisTrain(io.vertigo.ai.example.iris.domain.IrisTrain.class),
		/** Objet de données Location. */
		Location(io.vertigo.ai.example.telstra.domain.Location.class),
		/** Objet de données LocationTrain. */
		LocationTrain(io.vertigo.ai.example.telstra.domain.LocationTrain.class),
		/** Objet de données LogFeature. */
		LogFeature(io.vertigo.ai.example.telstra.domain.LogFeature.class),
		/** Objet de données LogFeatureTrain. */
		LogFeatureTrain(io.vertigo.ai.example.telstra.domain.LogFeatureTrain.class),
		/** Objet de données ResourceType. */
		ResourceType(io.vertigo.ai.example.telstra.domain.ResourceType.class),
		/** Objet de données ResourceTypeTrain. */
		ResourceTypeTrain(io.vertigo.ai.example.telstra.domain.ResourceTypeTrain.class),
		/** Objet de données SeverityType. */
		SeverityType(io.vertigo.ai.example.telstra.domain.SeverityType.class),
		/** Objet de données SeverityTypeTrain. */
		SeverityTypeTrain(io.vertigo.ai.example.telstra.domain.SeverityTypeTrain.class),
		/** Objet de données TelstraTrain. */
		TelstraTrain(io.vertigo.ai.example.telstra.domain.TelstraTrain.class)		;

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
	 * Enumération des champs de Era.
	 */
	public enum EraFields implements DtFieldName<io.vertigo.ai.example.heroes.domain.Era> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Era ID'. */
		eraId,
		/** Propriété 'Name'. */
		eraName	}

	/**
	 * Enumération des champs de EventType.
	 */
	public enum EventTypeFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.EventType> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Event Type'. */
		eventType	}

	/**
	 * Enumération des champs de EventTypeTrain.
	 */
	public enum EventTypeTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.EventTypeTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Event Type'. */
		eventType	}

	/**
	 * Enumération des champs de Faction.
	 */
	public enum FactionFields implements DtFieldName<io.vertigo.ai.example.heroes.domain.Faction> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Faction ID'. */
		factionId,
		/** Propriété 'Name'. */
		factionName,
		/** Propriété 'Era ID'. */
		era	}

	/**
	 * Enumération des champs de FactionCount.
	 */
	public enum FactionCountFields implements DtFieldName<io.vertigo.ai.example.heroes.domain.FactionCount> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Name'. */
		factionName,
		/** Propriété 'Heroes per faction'. */
		countFactionName	}

	/**
	 * Enumération des champs de Heroe.
	 */
	public enum HeroeFields implements DtFieldName<io.vertigo.ai.example.heroes.domain.Heroe> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Name'. */
		heroeName,
		/** Propriété 'Job'. */
		job,
		/** Propriété 'Faction ID'. */
		faction	}

	/**
	 * Enumération des champs de Iris.
	 */
	public enum IrisFields implements DtFieldName<io.vertigo.ai.example.iris.domain.Iris> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Sepal Lenght'. */
		sepalLength,
		/** Propriété 'Sepal Width'. */
		sepalWidth,
		/** Propriété 'Petal Lenght'. */
		petalLength,
		/** Propriété 'Petal Width'. */
		petalWidth,
		/** Propriété 'Label'. */
		variety	}

	/**
	 * Enumération des champs de IrisTrain.
	 */
	public enum IrisTrainFields implements DtFieldName<io.vertigo.ai.example.iris.domain.IrisTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Sepal Lenght'. */
		sepalLength,
		/** Propriété 'Sepal Width'. */
		sepalWidth,
		/** Propriété 'Petal Lenght'. */
		petalLength,
		/** Propriété 'Petal Width'. */
		petalWidth,
		/** Propriété 'Label'. */
		variety	}

	/**
	 * Enumération des champs de Location.
	 */
	public enum LocationFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.Location> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Location'. */
		location,
		/** Propriété 'Severity Fault'. */
		severityFault	}

	/**
	 * Enumération des champs de LocationTrain.
	 */
	public enum LocationTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.LocationTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Location'. */
		location,
		/** Propriété 'Severity Fault'. */
		severityFault	}

	/**
	 * Enumération des champs de LogFeature.
	 */
	public enum LogFeatureFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.LogFeature> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Log Feature'. */
		logFeature,
		/** Propriété 'Volume'. */
		volume	}

	/**
	 * Enumération des champs de LogFeatureTrain.
	 */
	public enum LogFeatureTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.LogFeatureTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Log Feature'. */
		logFeature,
		/** Propriété 'Volume'. */
		volume	}

	/**
	 * Enumération des champs de ResourceType.
	 */
	public enum ResourceTypeFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.ResourceType> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Resource Type'. */
		resourceType	}

	/**
	 * Enumération des champs de ResourceTypeTrain.
	 */
	public enum ResourceTypeTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.ResourceTypeTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Resource Type'. */
		resourceType	}

	/**
	 * Enumération des champs de SeverityType.
	 */
	public enum SeverityTypeFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.SeverityType> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Severity Type'. */
		severityType	}

	/**
	 * Enumération des champs de SeverityTypeTrain.
	 */
	public enum SeverityTypeTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.SeverityTypeTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Severity Type'. */
		severityType	}

	/**
	 * Enumération des champs de TelstraTrain.
	 */
	public enum TelstraTrainFields implements DtFieldName<io.vertigo.ai.example.telstra.domain.TelstraTrain> {
		/** Propriété 'ID'. */
		id,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'code_count'. */
		codeCount,
		/** Propriété 'Log Feature'. */
		logFeature,
		/** Propriété 'Volume'. */
		volume,
		/** Propriété 'Severity Fault'. */
		severityFault,
		/** Propriété 'win_location_volume_sum'. */
		winLocationVolumeSum,
		/** Propriété 'win_location_volume_avg'. */
		winLocationVolumeAvg,
		/** Propriété 'win_location_volume_min'. */
		winLocationVolumeMin,
		/** Propriété 'win_location_volume_max'. */
		winLocationVolumeMax,
		/** Propriété 'win_location_volume_count'. */
		winLocationVolumeCount,
		/** Propriété 'count_feature_204'. */
		countFeature204,
		/** Propriété 'count_feature_205'. */
		countFeature205,
		/** Propriété 'severity_type'. */
		severityType,
		/** Propriété 'sum_feature_204_volume'. */
		sumFeature204Volume,
		/** Propriété 'sum_feature_205_volume'. */
		sumFeature205Volume,
		/** Propriété 'avg_feature_204_volume'. */
		avgFeature204Volume,
		/** Propriété 'avg_feature_205_volume'. */
		avgFeature205Volume,
		/** Propriété 'min_feature_204_volume'. */
		minFeature204Volume,
		/** Propriété 'min_feature_205_volume'. */
		minFeature205Volume,
		/** Propriété 'max_feature_204_volume'. */
		maxFeature204Volume,
		/** Propriété 'max_feature_205_volume'. */
		maxFeature205Volume,
		/** Propriété 'count_feature_204_volume'. */
		countFeature204Volume,
		/** Propriété 'count_feature_205_volume'. */
		countFeature205Volume	}

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
