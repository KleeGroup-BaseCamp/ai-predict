package io.vertigo.ai.example.telstra.services;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.dao.EventTypeTrainDAO;
import io.vertigo.ai.example.telstra.dao.LocationDAO;
import io.vertigo.ai.example.telstra.dao.LocationTrainDAO;
import io.vertigo.ai.example.telstra.dao.LogFeatureTrainDAO;
import io.vertigo.ai.example.telstra.dao.ResourceTypeTrainDAO;
import io.vertigo.ai.example.telstra.dao.SeverityTypeTrainDAO;
import io.vertigo.ai.example.telstra.domain.EventType;
import io.vertigo.ai.example.telstra.domain.EventTypeTrain;
import io.vertigo.ai.example.telstra.domain.Location;
import io.vertigo.ai.example.telstra.domain.LocationTrain;
import io.vertigo.ai.example.telstra.domain.LogFeature;
import io.vertigo.ai.example.telstra.domain.LogFeatureTrain;
import io.vertigo.ai.example.telstra.domain.ResourceType;
import io.vertigo.ai.example.telstra.domain.ResourceTypeTrain;
import io.vertigo.ai.example.telstra.domain.SeverityType;
import io.vertigo.ai.example.telstra.domain.SeverityTypeTrain;
import io.vertigo.ai.example.telstra.train.TrainPAO;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class TelstraServices implements Component {

	@Inject
	private LocationTrainDAO locationTrainDAO;

	@Inject
	private EventTypeTrainDAO eventTypeTrainDAO;

	@Inject
	private LogFeatureTrainDAO logFeatureTrainDAO;

	@Inject
	private SeverityTypeTrainDAO severityTypeTrainDAO;

	@Inject
	private ResourceTypeTrainDAO resourceTypeTrainDAO;

	@Inject
	private TrainPAO trainPAO;
	
	@Inject
	private LocationDAO locationDAO;

	// Location
	
	public DtList<LocationTrain> loadLocation(List<Long> locationIds) {
		return locationTrainDAO.loadLocation(locationIds);
	}

	public void removeLocationTrain(long id) {
		locationTrainDAO.delete(id);
	}
	
	public void removeLocationTrain() {
		trainPAO.removeLocationTrain();
	}

	public void insertLocationTrain(Collection<Record<Location, LocationTrain>> locations) {
		DtList<LocationTrain> locationTrain = new DtList<>(LocationTrain.class);

		for (Record<Location,LocationTrain> loc : locations) {
			locationTrain.add(loc.getRecordDtObject());
		}

		locationTrainDAO.bulkCreateLocationTrain(locationTrain);
	}

	public void createLocation(Location entity) {
		locationDAO.create(entity);
	}

	public void updateLocation(Location entity) {
		locationDAO.update(entity);
	}

	public Location get(long id) {
		return locationDAO.get(id);
	}

	// EventType
	
	public DtList<EventTypeTrain> loadEventType(List<Long> eventTypeIds) {
		return eventTypeTrainDAO.loadEventType(eventTypeIds);
	}

	public void insertEventTypeTrain(Collection<Record<EventType, EventTypeTrain>> eventTypes) {
		DtList<EventTypeTrain> eventTypeTrain = new DtList<>(EventTypeTrain.class);

		for (Record<EventType, EventTypeTrain> loc : eventTypes) {
			eventTypeTrain.add(loc.getRecordDtObject());
		}

		eventTypeTrainDAO.bulkCreateEventTypeTrain(eventTypeTrain);
	}
	
	public void removeEventTypeTrain(long id) {
		eventTypeTrainDAO.delete(id);
	}
	
	public void removeEventTypeTrain() {
		trainPAO.removeEventTypeTrain();
	}

	// LogFeature
	
	public DtList<LogFeatureTrain> loadLogFeature(List<Long> LogFeatureIds) {
		return logFeatureTrainDAO.loadLogFeature(LogFeatureIds);
	}

	public void insertLogFeatureTrain(Collection<Record<LogFeature, LogFeatureTrain>> LogFeatures) {
		DtList<LogFeatureTrain> logFeatureTrainTrain = new DtList<>(LogFeatureTrain.class);

		for (Record<LogFeature, LogFeatureTrain> loc : LogFeatures) {
			logFeatureTrainTrain.add(loc.getRecordDtObject());
		}

		logFeatureTrainDAO.bulkCreateLogFeatureTrain(logFeatureTrainTrain);
	}
	
	public void removeLogFeatureTrain(long id) {
		logFeatureTrainDAO.delete(id);
	}

	public void removeLogFeatureTrain() {
		trainPAO.removeLogFeatureTrain();
	}
	
	// SeverityType
	
	public DtList<SeverityTypeTrain> loadSeverityType(List<Long> severityTypeIds) {
		return severityTypeTrainDAO.loadSeverityType(severityTypeIds);
	}

	public void insertSeverityTypeTrain(Collection<Record<SeverityType, SeverityTypeTrain>> severityTypes) {
		DtList<SeverityTypeTrain> severityTypeTrainTrain = new DtList<>(SeverityTypeTrain.class);

		for (Record<SeverityType, SeverityTypeTrain> loc : severityTypes) {
			severityTypeTrainTrain.add(loc.getRecordDtObject());
		}

		severityTypeTrainDAO.bulkCreateSeverityTypeTrain(severityTypeTrainTrain);
	}
	
	public void removeSeverityTypeTrain(long id) {
		severityTypeTrainDAO.delete(id);
	}

	public void removeSeverityTypeTrain() {
		trainPAO.removeSeverityTypeTrain();
	}
	
	// ResourceType
	
	public DtList<ResourceTypeTrain> loadResourceType(List<Long> resourceTypeIds) {
		return resourceTypeTrainDAO.loadResourceType(resourceTypeIds);
	}

	public void insertResourceTypeTrain(Collection<Record<ResourceType, ResourceTypeTrain>> resourceTypes) {
		DtList<ResourceTypeTrain> resourceTypeTrainTrain = new DtList<>(ResourceTypeTrain.class);

		for (Record<ResourceType, ResourceTypeTrain> loc : resourceTypes) {
			resourceTypeTrainTrain.add(loc.getRecordDtObject());
		}

		resourceTypeTrainDAO.bulkCreateResourceTypeTrain(resourceTypeTrainTrain);
	}
	
	public void removeResourceTypeTrain(long id) {
		resourceTypeTrainDAO.delete(id);
	}

	public void removeResourceTypeTrain() {
		trainPAO.removeResourceTypeTrain();
	}

}
