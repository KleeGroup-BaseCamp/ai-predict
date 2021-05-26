package io.vertigo.ai.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.vertigo.ai.example.data.models.EventType;
import io.vertigo.ai.example.data.models.Location;
import io.vertigo.ai.example.data.models.LogFeature;
import io.vertigo.ai.example.data.models.ResourceType;
import io.vertigo.ai.example.data.models.SeverityType;
import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.train.data.Iris;

public class Preprocessing {

	public static void process(List<Location> locations, List<SeverityType> severityTypes, List<LogFeature> logFeatures, List<EventType> eventTypes, List<ResourceType> resourceTypes) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		// Convert all List to Datasets
		Dataset locationDataset = new Dataset(Location.class, locations);
		Dataset severityDataset = new Dataset(SeverityType.class, severityTypes);
		Dataset featureDataset = new Dataset(LogFeature.class, logFeatures);
		Dataset eventDataset = new Dataset(EventType.class, eventTypes);
		Dataset resourceDataset = new Dataset(ResourceType.class, resourceTypes);
		
		Function<String, Double> log = new Function<String, Double>() {

			@Override
			public Double apply(String t) {
				Double d = Double.valueOf(t);
				return Math.log10(1+d);
			}
			
		};
		
		Dataset logFeatureDataset = featureDataset.apply("volume", log);
				
		Dataset eventById = eventDataset.groupBy("id", "count").select("id", "id_count").rename("id_count", "event_per_id");
		Dataset resourceCount = resourceDataset.groupBy("resourceType", "count").select("resourceType", "resourceType_count");
		Dataset locationCount = locationDataset.groupBy("location", "count").select("location", "location_count");
		Dataset logFeatureById = logFeatureDataset.groupBy("id", "count", "min", "max", "mean", "sum", "std").select("id", "id_count", "volume_min", "volume_max", "volume_mean", "volume_sum", "volume_std").rename("id_count", "feature_count");
		
		Dataset resourceJoined = resourceDataset.join(resourceCount, "resourceType", "resourceType", "left").except("serialVersionUID");
		Dataset eventJoined = eventDataset.join(eventById, "id", "id", "left").except("serialVersionUID");
		Dataset locationJoined = locationDataset.join(locationCount, "location", "location", "left").except("serialVersionUID");
		Dataset featureJoined = featureDataset.join(logFeatureById, "id", "id", "left").except("serialVersionUID");
		
		Dataset train = locationJoined.join(eventJoined, "id", "id", "left").join(featureJoined, "id", "id", "left").join(resourceJoined, "id", "id", "left").join(severityDataset, "id", "id", "left").distinct().except("serialVersionUID");
		
		train.forEach(row -> System.out.println(row));
		System.out.println(train.columns().size());
		System.out.println(train.count());
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		List<Location> locations = readLocation("./src/main/resources/io/vertigo/ai/datageneration/train.csv");
		List<EventType> eventType = readEvent("./src/main/resources/io/vertigo/ai/datageneration/event_type.csv");
		List<LogFeature> features = readFeature("./src/main/resources/io/vertigo/ai/datageneration/log_feature.csv");
		List<ResourceType> resources = readResource("./src/main/resources/io/vertigo/ai/datageneration/resource_type.csv");
		List<SeverityType> severities = readSeverity("./src/main/resources/io/vertigo/ai/datageneration/severity_type.csv");
		process(locations, severities, features, eventType, resources);
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
	
}
