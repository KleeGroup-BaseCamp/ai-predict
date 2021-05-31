package io.vertigo.ai.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.example.data.models.EventType;
import io.vertigo.ai.example.data.models.Location;
import io.vertigo.ai.example.data.models.LogFeature;
import io.vertigo.ai.example.data.models.ResourceType;
import io.vertigo.ai.example.data.models.SeverityType;
import io.vertigo.ai.structure.dataset.models.Dataset;

public class Preprocessing {

	public static Dataset process(List<Location> locations, List<SeverityType> severityTypes, List<LogFeature> logFeatures, List<EventType> eventTypes, List<ResourceType> resourceTypes) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		// Convert all List to Datasets
		Dataset locationDataset = new Dataset(Location.class, locations);
		Dataset severityDataset = new Dataset(SeverityType.class, severityTypes);
		Dataset featureDataset = new Dataset(LogFeature.class, logFeatures);
		Dataset eventDataset = new Dataset(EventType.class, eventTypes);
		Dataset resourceDataset = new Dataset(ResourceType.class, resourceTypes);
		
		Function<Integer, Double> log = new Function<Integer, Double>() {

			@Override
			public Double apply(Integer t) {
				return Math.log10(1+t);
			}
			
		};
		
		Dataset logFeatureDataset = featureDataset.apply("volume", log, Integer.class, Double.class);
				
		Dataset eventById = eventDataset.groupBy("id", "count").select("id", "idCount").rename("idCount", "eventPerId");
		Dataset resourceCount = resourceDataset.groupBy("resourceType", "count").select("resourceType", "resourceTypeCount");
		Dataset locationCount = locationDataset.groupBy("location", "count").select("location", "locationCount");
		Dataset logFeatureById = logFeatureDataset.groupBy("id", "count", "min", "max", "mean", "sum", "std").select("id", "idCount", "volumeMin", "volumeMax", "volumeMean", "volumeSum", "volumeStd").rename("idCount", "featureCount");
		
		Dataset resourceJoined = resourceDataset.join(resourceCount, "resourceType", "resourceType", "left").except("serialVersionUID");
		Dataset eventJoined = eventDataset.join(eventById, "id", "id", "left").except("serialVersionUID");
		Dataset locationJoined = locationDataset.join(locationCount, "location", "location", "left").except("serialVersionUID");
		Dataset featureJoined = featureDataset.join(logFeatureById, "id", "id", "left").except("serialVersionUID");
		
		Dataset train = locationJoined.join(eventJoined, "id", "id", "left").join(featureJoined, "id", "id", "left").join(resourceJoined, "id", "id", "left").join(severityDataset, "id", "id", "left").distinct().except("serialVersionUID");
		return train;
	}
	
	public static Dataset runPreprocessing() throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		List<Location> locations = readLocation("./src/main/resources/io/vertigo/ai/datageneration/train.csv");
		List<EventType> eventType = readEvent("./src/main/resources/io/vertigo/ai/datageneration/event_type.csv");
		List<LogFeature> features = readFeature("./src/main/resources/io/vertigo/ai/datageneration/log_feature.csv");
		List<ResourceType> resources = readResource("./src/main/resources/io/vertigo/ai/datageneration/resource_type.csv");
		List<SeverityType> severities = readSeverity("./src/main/resources/io/vertigo/ai/datageneration/severity_type.csv");
		return process(locations, severities, features, eventType, resources);
	}
	
	private static List<Location> readLocation(String path) throws IOException {
		 List<Location> records = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            String line;
	            long id = 0;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (id!=0) {
		                Location location = new Location();
		                location.setId(Integer.valueOf(values[0]));
		                location.setLocation(values[1]);
		                location.setSeverityFault(Integer.valueOf(values[2]));
		                records.add(location);
	                }
	                id++;
	            }
	        }
	        return records;
	}
	
	private static List<SeverityType> readSeverity(String path) throws IOException {
		 List<SeverityType> records = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            String line;
	            long id = 0;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (id!=0) {
	                	SeverityType severity = new SeverityType();
	                	severity.setId(Integer.valueOf(values[0]));
		                severity.setSeverityType(values[1]);
		                records.add(severity);
	                }
	                id++;
	            }
	        }
	        return records;
	}

	private static List<EventType> readEvent(String path) throws IOException {
		 List<EventType> records = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            String line;
	            long id = 0;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (id!=0) {
	                	EventType event = new EventType();
	                	event.setId(Integer.valueOf(values[0]));
	                	event.setEventType(values[1]);
		                records.add(event);
	                }
	                id++;
	            }
	        }
	        return records;
	}
	
	private static List<ResourceType> readResource(String path) throws IOException {
		 List<ResourceType> records = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            String line;
	            long id = 0;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (id!=0) {
	                	ResourceType resource = new ResourceType();
	                	resource.setId(Integer.valueOf(values[0]));
	                	resource.setResourceType(values[1]);
		                records.add(resource);
	                }
	                id++;
	            }
	        }
	        return records;
	}
	
	private static List<LogFeature> readFeature(String path) throws IOException {
		 List<LogFeature> records = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	            String line;
	            long id = 0;
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (id!=0) {
	                	LogFeature feature = new LogFeature();
	                	feature.setId(Integer.valueOf(values[0]));
	                	feature.setLogFeature(values[1]);
	                	feature.setVolume(Integer.valueOf(values[2]));
		                records.add(feature);
	                }
	                id++;
	            }
	        }
	        return records;
	}
	
	@Test
	public void testPreprocessing() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		Dataset train = runPreprocessing();
		Dataset filteredTrain = train.filter(p -> (int) p.get("id") == 10422);
		// Checks dimensions
		Assertions.assertEquals(61839, train.count());
		Assertions.assertEquals(17, train.columns().size());
		// Checks one element
		Assertions.assertEquals(10422, filteredTrain.get(0, "id"));
		Assertions.assertEquals("location 584", filteredTrain.get(0, "location"));
		Assertions.assertEquals("feature 73", filteredTrain.get(0, "logFeature"));
		Assertions.assertEquals("severity_type 2", filteredTrain.get(0, "severityType"));
		Assertions.assertEquals(0.3010299956639812, filteredTrain.get(0, "volume"));
		Assertions.assertEquals(0.3010299956639812, filteredTrain.get(0, "volumeMean"));
		Assertions.assertEquals(0.3010299956639812, filteredTrain.get(0, "volumeSum"));
		Assertions.assertEquals(0.3010299956639812, filteredTrain.get(0, "volumeMin"));
		Assertions.assertEquals(0.3010299956639812, filteredTrain.get(0, "volumeMax"));
		Assertions.assertEquals(0.0, filteredTrain.get(0, "volumeStd"));
		Assertions.assertEquals((long)10268, filteredTrain.get(0, "resourceTypeCount"));
		Assertions.assertEquals(0, filteredTrain.get(0, "severityFault"));
		Assertions.assertEquals((long)1, filteredTrain.get(0, "featureCount"));
		Assertions.assertEquals("resource_type 8", filteredTrain.get(0, "resourceType"));
		Assertions.assertEquals((long)1, filteredTrain.get(0, "eventPerId"));
		Assertions.assertEquals("event_type 11", filteredTrain.get(0, "eventType"));
		Assertions.assertEquals((long)25, filteredTrain.get(0, "locationCount"));
	}
	
}
