package io.vertigo.ai.example.telstra.data.datageneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.example.telstra.dao.EventTypeDAO;
import io.vertigo.ai.example.telstra.dao.LocationDAO;
import io.vertigo.ai.example.telstra.dao.LogFeatureDAO;
import io.vertigo.ai.example.telstra.dao.ResourceTypeDAO;
import io.vertigo.ai.example.telstra.dao.SeverityTypeDAO;
import io.vertigo.ai.example.telstra.domain.EventType;
import io.vertigo.ai.example.telstra.domain.Location;
import io.vertigo.ai.example.telstra.domain.LogFeature;
import io.vertigo.ai.example.telstra.domain.ResourceType;
import io.vertigo.ai.example.telstra.domain.SeverityType;
import io.vertigo.ai.utils.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.database.migration.MigrationManager;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;

@Transactional
public class TelstraGenerator implements Component {
	private static final String EVENT_TYPE_CSV_FILE_PATH = "io/vertigo/ai/example/telstra/datageneration/event_type.csv";
	private static final String LOG_FEATURE_CSV_FILE_PATH = "io/vertigo/ai/example/telstra/datageneration/log_feature.csv";
	private static final String RESOURCE_TYPE_CSV_FILE_PATH = "io/vertigo/ai/example/telstra/datageneration/resource_type.csv";
	private static final String SEVERITY_TYPE_CSV_FILE_PATH = "io/vertigo/ai/example/telstra/datageneration/severity_type.csv";
	private static final String TELSTRA_LOCATION_CSV_FILE_PATH = "io/vertigo/ai/example/telstra/datageneration/train.csv";


	private static final int EVENT_TYPE_CSV_FILE_COLUMN_NUMBER = 2;
	private static final int LOG_FEATURE_CSV_FILE_COLUMN_NUMBER = 3;
	private static final int RESOURCE_TYPE_CSV_FILE_COLUMN_NUMBER = 2;
	private static final int SEVERITY_TYPE_CSV_FILE_COLUMN_NUMBER = 2;
	private static final int TELSTRA_LOCATION_FAULT_TYPE_CSV_FILE_COLUMN_NUMBER = 3;

	@Inject
	private EventTypeDAO eventTypeDAO;

	@Inject
	private LogFeatureDAO logFeatureDAO;

	@Inject
	private SeverityTypeDAO severityTypeDAO;

	@Inject
	private LocationDAO locationDAO;

	@Inject
	private ResourceTypeDAO resourceTypeDAO;

	@Inject
	private MigrationManager migrationManager;

	@Inject
	private ResourceManager resourceManager;
	
	private List<EventType> eventTypeBuff = new ArrayList<>();
	private List<LogFeature> logFeatureBuff = new ArrayList<>();
	private List<SeverityType> severityTypeBuff = new ArrayList<>();
	private List<ResourceType> resourceTypeBuff = new ArrayList<>();
	private List<Location> locationBuff = new ArrayList<>();
	

	private void consumeEventType(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == EVENT_TYPE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Event Types", csvFilePath);
		//---

		EventType eventType = new EventType();
		eventType.setCode(Integer.parseInt(record[0]));
		eventType.setEventType(record[1]);
		
		DtList<EventType> dtListToSave =  new DtList<>(EventType.class);
		synchronized (eventTypeBuff) {
			addRecord(eventType, eventTypeBuff, dtListToSave);
		}
		saveEventType(dtListToSave);
		
	}
	
	public void finishEventType() {
		DtList<EventType> dtListToSave =  new DtList<>(EventType.class);
		synchronized (eventTypeBuff) {
			flushBufferRecordToOverflow(eventTypeBuff, dtListToSave);
		}
		saveEventType(dtListToSave);
	}

	private void saveEventType(DtList<EventType> dtListToSave) {
		if (!dtListToSave.isEmpty()) {
			eventTypeDAO.bulkCreateEventType(dtListToSave);
		}
	}

	private void consumeLogFeature(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == LOG_FEATURE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Event Types", csvFilePath);
		//---

		LogFeature logFeature = new LogFeature();
		logFeature.setCode(Integer.parseInt(record[0]));
		logFeature.setLogFeature(record[1]);
		logFeature.setVolume(Integer.parseInt(record[2]));

		DtList<LogFeature> dtListToSave =  new DtList<>(LogFeature.class);
		synchronized (logFeatureBuff) {
			addRecord(logFeature, eventTypeBuff, dtListToSave);
		}
		saveLogFeature(dtListToSave);
	}

	
	public void finishLogFeature() {
		DtList<LogFeature> dtListToSave =  new DtList<>(LogFeature.class);
		synchronized (logFeatureBuff) {
			flushBufferRecordToOverflow(logFeatureBuff, dtListToSave);
		}
		saveLogFeature(dtListToSave);
	}

	private void saveLogFeature(DtList<LogFeature> dtListToSave) {
		if (!dtListToSave.isEmpty()) {
			logFeatureDAO.bulkCreateLogFeature(dtListToSave);
		}
	}


	private void consumeResourceType(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == RESOURCE_TYPE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Event Types", csvFilePath);
		//---

		ResourceType resourceType = new ResourceType();
		resourceType.setCode(Integer.parseInt(record[0]));
		resourceType.setResourceType(record[1]);

		DtList<ResourceType> dtListToSave =  new DtList<>(ResourceType.class);
		synchronized (resourceTypeBuff) {
			addRecord(resourceType, resourceTypeBuff, dtListToSave);
		}
		saveResourceType(dtListToSave);
	}

	public void finishResourceType() {
		DtList<ResourceType> dtListToSave =  new DtList<>(ResourceType.class);
		synchronized (resourceTypeBuff) {
			flushBufferRecordToOverflow(resourceTypeBuff, dtListToSave);
		}
		saveResourceType(dtListToSave);
	}
	
	private void saveResourceType(DtList<ResourceType> dtListToSave) {
		if (!dtListToSave.isEmpty()) {
			resourceTypeDAO.bulkCreateResourceType(dtListToSave);
		}
	}

	private void consumeSeverityType(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == SEVERITY_TYPE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Event Types", csvFilePath);
		//---

		SeverityType severityType = new SeverityType();
		severityType.setCode(Integer.parseInt(record[0]));
		severityType.setSeverityType(record[1]);

		DtList<SeverityType> dtListToSave =  new DtList<>(SeverityType.class);
		synchronized (severityTypeBuff) {
			addRecord(severityType, severityTypeBuff, dtListToSave);
		}
		saveSeverityType(dtListToSave);
	}

	public void finishSeverityType() {
		DtList<SeverityType> dtListToSave =  new DtList<>(SeverityType.class);
		synchronized (severityTypeBuff) {
			flushBufferRecordToOverflow(severityTypeBuff, dtListToSave);
		}
		saveSeverityType(dtListToSave);
	}
	
	private void saveSeverityType(DtList<SeverityType> dtListToSave) {
		if (!dtListToSave.isEmpty()) {
			severityTypeDAO.bulkCreateSeverityType(dtListToSave);
		}
	}

	private void consumeTelstraLocationFault(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == TELSTRA_LOCATION_FAULT_TYPE_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Event Types", csvFilePath);
		//---

		Location location = new Location();
		location.setCode(Integer.parseInt(record[0]));
		location.setLocation(record[1]);
		location.setSeverityFault(Integer.parseInt(record[2]));

		DtList<Location> dtListToSave =  new DtList<>(Location.class);
		synchronized (locationBuff) {
			addRecord(location, locationBuff, dtListToSave);
		}
		saveLocation(dtListToSave);
	}
	
	public void finishLocation() {
		DtList<Location> dtListToSave =  new DtList<>(Location.class);
		synchronized (locationBuff) {
			flushBufferRecordToOverflow(locationBuff, dtListToSave);
		}
		saveLocation(dtListToSave);
	}

	private void saveLocation(DtList<Location> dtListToSave) {
		if (!dtListToSave.isEmpty()) {
			locationDAO.bulkCreateLocation(dtListToSave);
		}
	}

	public void updateMainSchema() {
		migrationManager.update(SqlManager.MAIN_CONNECTION_PROVIDER_NAME);
	}

	public void updateTrainSchema() {
		migrationManager.update("train");
	}
	
	public void createEventTypeFromCSV() {
		CSVReaderUtil.parseCSV(resourceManager, EVENT_TYPE_CSV_FILE_PATH, this::consumeEventType, this::finishEventType);
	}

	public void createLogFeatureFromCSV() {
		CSVReaderUtil.parseCSV(resourceManager, LOG_FEATURE_CSV_FILE_PATH, this::consumeLogFeature, this::finishLogFeature);
	}

	public void createResourceTypeFromCSV() {
		CSVReaderUtil.parseCSV(resourceManager, RESOURCE_TYPE_CSV_FILE_PATH, this::consumeResourceType, this::finishResourceType);
	}

	public void createSeverityTypeFromCSV() {
		CSVReaderUtil.parseCSV(resourceManager, SEVERITY_TYPE_CSV_FILE_PATH, this::consumeSeverityType, this::finishSeverityType);
	}

	public void createTelstraLocationFaultsFromCSV() {
		CSVReaderUtil.parseCSV(resourceManager, TELSTRA_LOCATION_CSV_FILE_PATH, this::consumeTelstraLocationFault, this::finishLocation);
	}
	
	private void addRecord(DtObject dtObj, List bufferList, DtList dtListOverflow) {
		bufferList.add(dtObj);
		if (bufferList.size() >= 1000) {
			flushBufferRecordToOverflow(bufferList, dtListOverflow);
		}
	}

	private void flushBufferRecordToOverflow(List bufferList, DtList dtListOverflow) {
		dtListOverflow.addAll(bufferList);
		bufferList.clear();
	}
}
