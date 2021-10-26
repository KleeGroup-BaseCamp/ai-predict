package io.vertigo.ai.example.telstra;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.vertigo.ai.example.telstra.data.datageneration.TelstraGenerator;
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
import io.vertigo.ai.example.telstra.domain.TelstraTrain;
import io.vertigo.ai.impl.structure.dataset.DatasetImpl;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.server.models.ScoreResponse;
import io.vertigo.ai.server.models.TrainResponse;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.AgregatorType;
import io.vertigo.ai.structure.processor.OrderField;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.ai.structure.processor.ProcessorDAGManager;
import io.vertigo.ai.structure.processor.ProcessorManager;
import io.vertigo.ai.structure.processor.SlidingWindow;
import io.vertigo.ai.structure.processor.SlidingWindowType;
import io.vertigo.ai.structure.processor.SortOrder;
import io.vertigo.ai.structure.processor.Window;
import io.vertigo.ai.structure.processor.graph.DAG;
import io.vertigo.ai.structure.processor.graph.DAGBuilder;
import io.vertigo.ai.structure.processor.graph.DAGNode;
import io.vertigo.ai.structure.record.RecordManager;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public abstract class AbstractTelstraTest {

	private static final String DS_LOCATION = "DsLocation";
	private static final String DS_LOG_FEATURE = "DsLogFeature";
	private static final String DS_SEVERITY_TYPE = "DsSeverityType";
	private static final String DS_RESOURCE_TYPE = "DsResourceType";
	private static final String DS_EVENT_TYPE = "DsEventType";
	
	private static final String DSI_ITEM = "DsTelstra";
	private AutoCloseableNode node;
	
	private RecordDefinition datasetLocationDefinition;
	private RecordDefinition datasetLogFeatureDefinition;
	private RecordDefinition datasetSeverityTypeDefinition;
	private RecordDefinition datasetResourceTypeDefinition;
	private RecordDefinition datasetEventTypeDefinition;

	
	private DtDefinition dtDefinitionLocation;
	private DtDefinition dtDefinitionLogFeature;
	private DtDefinition dtDefinitionSeverityType;
	private DtDefinition dtDefinitionResourceType;
	private DtDefinition dtDefinitionEventType;
	
	private DtDefinition dtDefinitionLocationTrain; 
	private DtDefinition dtDefinitionLogFeatureTrain;

	private DtDefinition dtDefinitionSeverityTypeTrain;
	private DtDefinition dtDefinitionResourceTypeTrain;
	private DtDefinition dtDefinitionEventTypeTrain;
	private DtDefinition dtDefinitionTelstraTrain;
	
	@Inject
	private TelstraGenerator telstraGenerator;
	
	@Inject
	private RecordManager datasetManager;

	@Inject
	private EntityStoreManager entityStoreManager;
	
	@Inject
	private VTransactionManager transactionManager;
	
	@Inject
	private ModelManager modelManager;
	
	@Inject
	private ProcessorManager processorManager;

	@Inject
	private ProcessorDAGManager processorDAGManager;
	
	protected final void init() {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		//Inputs Dataset
		datasetLocationDefinition = definitionSpace.resolve(DS_LOCATION, RecordDefinition.class);
		datasetLogFeatureDefinition = definitionSpace.resolve(DS_LOG_FEATURE, RecordDefinition.class);
		datasetSeverityTypeDefinition = definitionSpace.resolve(DS_SEVERITY_TYPE, RecordDefinition.class);
		datasetResourceTypeDefinition = definitionSpace.resolve(DS_RESOURCE_TYPE, RecordDefinition.class);
		datasetEventTypeDefinition = definitionSpace.resolve(DS_EVENT_TYPE, RecordDefinition.class);
		
		dtDefinitionLocation = DtObjectUtil.findDtDefinition(Location.class);
		dtDefinitionLogFeature = DtObjectUtil.findDtDefinition(LogFeature.class);
		dtDefinitionSeverityType = DtObjectUtil.findDtDefinition(SeverityType.class);
		dtDefinitionResourceType = DtObjectUtil.findDtDefinition(ResourceType.class);
		dtDefinitionEventType = DtObjectUtil.findDtDefinition(EventType.class);
		
		// Inputs Train
		dtDefinitionLocationTrain = DtObjectUtil.findDtDefinition(LocationTrain.class);
		dtDefinitionLogFeatureTrain = DtObjectUtil.findDtDefinition(LogFeatureTrain.class);
		dtDefinitionSeverityTypeTrain = DtObjectUtil.findDtDefinition(SeverityTypeTrain.class);
		dtDefinitionResourceTypeTrain = DtObjectUtil.findDtDefinition(ResourceTypeTrain.class);
		dtDefinitionEventTypeTrain = DtObjectUtil.findDtDefinition(EventTypeTrain.class);
		
		
		// Telstra training, 
		dtDefinitionTelstraTrain = DtObjectUtil.findDtDefinition(TelstraTrain.class);
		
	}
	
	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//--
		doSetUp();
		telstraGenerator.updateMainSchema();
		telstraGenerator.updateTrainSchema();
		
		telstraGenerator.createEventTypeFromCSV();
		telstraGenerator.createLogFeatureFromCSV();
		telstraGenerator.createResourceTypeFromCSV();
		telstraGenerator.createSeverityTypeFromCSV();
		telstraGenerator.createTelstraLocationFaultsFromCSV();
	}
	
	@AfterEach
	public final void tearDown() {
		if (node != null) {
			//irisServices.removeIrisTrain();
			node.close();
		}
	}
	
	protected abstract void doSetUp();

	protected abstract NodeConfig buildNodeConfig();
	
	@Test
	public void testRefreshAll() throws InterruptedException, ExecutionException, TimeoutException {
		
		long appSize;
		long trainSize;
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionLocation);
			trainSize = entityStoreManager.count(dtDefinitionLocationTrain);
		}
		//Assertions.assertNotEquals(appSize, trainSize);

		//on refresh la base de train
		datasetManager.refreshAll(datasetLocationDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetLogFeatureDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetSeverityTypeDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetResourceTypeDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetEventTypeDefinition).get(30, TimeUnit.SECONDS);
		
		Thread.sleep(1_000); //wait index was done
		
		try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
			appSize = entityStoreManager.count(dtDefinitionLocation);
			trainSize = entityStoreManager.count(dtDefinitionLocationTrain);
		}
		Assertions.assertEquals(appSize, trainSize);

	}
	
	
	@Test
	public void testTelstraPivot() throws JsonParseException, JsonMappingException, IOException {
		
		ProcessorBuilder processorBuilder = processorManager.createBuilder();
		
		String pivotColumn = "logFeature";
		final List<String> pivotStaticValues = Arrays.asList("feature 204", "feature 205");
		final List<String> rowIdfields = Arrays.asList("id");
		
		Agregator agg1 = new Agregator(AgregatorType.COUNT);
		Agregator agg2 = new Agregator(AgregatorType.SUM, "volume");
		Agregator agg3 = new Agregator(AgregatorType.AVG, "volume");
		Agregator agg4 = new Agregator(AgregatorType.MIN, "volume");
		Agregator agg5 = new Agregator(AgregatorType.MAX, "volume");
		Agregator agg6 = new Agregator(AgregatorType.COUNT, "volume");

		final List<Agregator> aggs = Arrays.asList(agg1, agg2, agg3, agg4, agg5, agg6);
		
		List<Processor> processors = processorBuilder
				.pivot(pivotColumn, pivotStaticValues, rowIdfields, aggs)
				.build();
		
		Dataset<LogFeatureTrain> logFeature = new DatasetImpl<>(dtDefinitionLogFeatureTrain);
		Dataset<TelstraTrain> telstra = new DatasetImpl<>(dtDefinitionTelstraTrain);

		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			processorManager.executeProcessing(logFeature, telstra, processors);
			transaction.commit();
		};
		
		//Test Train Postgresql
		// TrainResponse trainResponse = modelManager.train("telstra", 0);
		// Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(trainResponse.getScore().getScoreMean()));
		
		//Test Score
		// ScoreResponse scoreResponse = modelManager.score("telstra", 0);
		// Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(scoreResponse.getScore().getScoreMean()));
	}
	
	
	@Test
	public void testSimpleLinearTelstra() throws JsonParseException, JsonMappingException, IOException {
		
		ProcessorBuilder processorBuilder = processorManager.createBuilder();
		
		Agregator agg1 = new Agregator(AgregatorType.COUNT);
		Agregator agg2 = new Agregator(AgregatorType.SUM, "volume");
		Agregator agg3 = new Agregator(AgregatorType.AVG, "volume");
		Agregator agg4 = new Agregator(AgregatorType.MIN, "volume");
		Agregator agg5 = new Agregator(AgregatorType.MAX, "volume");
		Agregator agg6 = new Agregator(AgregatorType.COUNT, "volume");
		
		List<String> selectFields = Arrays.asList("id", "logFeature", "volume");
		List<String> partitionFields = Arrays.asList("location");
		List<OrderField> orderFields = Arrays.asList(new OrderField("id", SortOrder.ASC));
		SlidingWindow sw = new SlidingWindow(SlidingWindowType.ROWS, OptionalInt.of(10), OptionalInt.of(0));
		Window w1 = new Window("winLocation", partitionFields, orderFields, Optional.of(sw));
		
		List<Window> windows = Arrays.asList(w1);
		final List<Agregator> windowAggs = Arrays.asList(agg2, agg3, agg4, agg5, agg6);
		Dataset<LogFeatureTrain> logFeature = new DatasetImpl<>(dtDefinitionLogFeatureTrain);
		
		List<Processor> processors = processorBuilder
				.join(logFeature, "code", "code")
				.window(selectFields, windows, windowAggs)
				.build();

		Dataset<TelstraTrain> telstra = new DatasetImpl<>(dtDefinitionTelstraTrain);
		Dataset<LocationTrain> location = new DatasetImpl<>(dtDefinitionLocationTrain);
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			processorManager.executeProcessing(location, telstra, processors);
			transaction.commit();
		};
		
		//Test Train Postgresql
		TrainResponse trainResponse = modelManager.train("telstra", 0);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(trainResponse.getScore().getScoreMean()));
		
		//Test Score
		ScoreResponse scoreResponse = modelManager.score("telstra", 0);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(scoreResponse.getScore().getScoreMean()));
	}
	
	@Test
	public void testTelstraDAG() throws JsonParseException, JsonMappingException, IOException, InterruptedException, ExecutionException, TimeoutException {
		
		//on refresh la base de train
		datasetManager.refreshAll(datasetLocationDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetLogFeatureDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetSeverityTypeDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetResourceTypeDefinition).get(30, TimeUnit.SECONDS);
		datasetManager.refreshAll(datasetEventTypeDefinition).get(30, TimeUnit.SECONDS);
		
		Thread.sleep(1_000); //wait index was done
		
 		DAGBuilder dagBuilder = processorDAGManager.createBuilder();
		
		Agregator agg1 = new Agregator(AgregatorType.COUNT);
		Agregator agg2 = new Agregator(AgregatorType.SUM, "volume");
		Agregator agg3 = new Agregator(AgregatorType.AVG, "volume");
		Agregator agg4 = new Agregator(AgregatorType.MIN, "volume");
		Agregator agg5 = new Agregator(AgregatorType.MAX, "volume");
		Agregator agg6 = new Agregator(AgregatorType.COUNT, "volume");
		
		List<String> selectFields = Arrays.asList("id", "logFeature", "volume", "code", "severityFault");
		List<String> partitionFields = Arrays.asList("location");
		List<OrderField> orderFields = Arrays.asList(new OrderField("id", SortOrder.ASC));
		SlidingWindow sw = new SlidingWindow(SlidingWindowType.ROWS, OptionalInt.of(10), OptionalInt.of(0));
		Window w1 = new Window("win_location", partitionFields, orderFields, Optional.of(sw));
		
		String pivotColumnLogFeature = "logFeature";
		String pivotColumnEventType = "eventType";
		String pivotColumnResourceType = "resourceType";
		
		final List<String> pivotStaticValues = Arrays.asList("feature 204", "feature 205");
		final List<String> rowIdfields = Arrays.asList("code");
		
		final List<Agregator> pivotAggsLogFeature = Arrays.asList(agg1, agg2, agg3, agg4, agg5, agg6);
		final List<Agregator> pivotAggsEventType = Arrays.asList(agg1);
		final List<Agregator> pivotAggsResourceType = Arrays.asList(agg1);
		
		List<Window> windows = Arrays.asList(w1);
		final List<Agregator> windowAggs = Arrays.asList(agg2, agg3, agg4, agg5, agg6);
		Dataset<LogFeatureTrain> logFeature = new DatasetImpl<>(dtDefinitionLogFeatureTrain);
		
		Dataset<LogFeatureTrain> dsLogFeature = new DatasetImpl<>(dtDefinitionLogFeatureTrain);
		Dataset<LocationTrain> dsLocation = new DatasetImpl<>(dtDefinitionLocationTrain);
		Dataset<EventTypeTrain> dsEventType = new DatasetImpl<>(dtDefinitionEventTypeTrain);
		Dataset<ResourceTypeTrain> dsResourceType = new DatasetImpl<>(dtDefinitionResourceTypeTrain);
		Dataset<SeverityTypeTrain> dsSeverityTypeFeature = new DatasetImpl<>(dtDefinitionSeverityTypeTrain);
		
		DAGNode locationNode = dagBuilder.createDAGInitNode(dsLocation);
		DAGNode logFeatureNode = dagBuilder.createDAGInitNode(dsLogFeature);
		DAGNode eventTypeNode = dagBuilder.createDAGInitNode(dsEventType);
		DAGNode resourceTypeNode = dagBuilder.createDAGInitNode(dsResourceType);
		DAGNode severityTypeNode = dagBuilder.createDAGInitNode(dsSeverityTypeFeature);
		
		dagBuilder.addInitNode(locationNode)
				  .addInitNode(logFeatureNode)
				  .addInitNode(eventTypeNode)
				  .addInitNode(resourceTypeNode)
				  .addInitNode(severityTypeNode);
		
		DAGNode logFeatureLocationJoined = dagBuilder.join(logFeatureNode, locationNode, "code", "code");
		DAGNode logFeatureLocationJoinedWindow = dagBuilder.window(logFeatureLocationJoined, selectFields, windows, windowAggs);
		DAGNode logFeatureLocationGrouped = dagBuilder.groupBy(logFeatureLocationJoinedWindow, "code", AgregatorType.COUNT);
		
		DAGNode logFeatureLocationWinGroupJoined = dagBuilder.join(logFeatureLocationGrouped, logFeatureLocationJoinedWindow, "code", "code");
		
		DAGNode logFeaturePivot = dagBuilder.pivot(logFeatureNode, pivotColumnLogFeature, pivotStaticValues, rowIdfields, pivotAggsLogFeature);
		DAGNode eventTypePivot = dagBuilder.pivot(eventTypeNode, pivotColumnEventType, pivotStaticValues, rowIdfields, pivotAggsEventType);
		DAGNode resourceTypePivot = dagBuilder.pivot(resourceTypeNode, pivotColumnResourceType, pivotStaticValues, rowIdfields, pivotAggsResourceType);

		//DAGNode lfetJoined = dagBuilder.join(logFeatureLocationGrouped, eventTypePivot, "code", "code");
		DAGNode lfetJoined = dagBuilder.join(logFeatureLocationWinGroupJoined, eventTypePivot, "code", "code");
		DAGNode lfetrtJoined = dagBuilder.join(lfetJoined, resourceTypePivot, "code", "code");
		DAGNode lfetrtstJoined = dagBuilder.join(lfetrtJoined, severityTypeNode, "code", "code");
		DAGNode telstraJoined = dagBuilder.join(lfetrtstJoined, logFeaturePivot, "code", "code");
		
		dagBuilder.setEndNode(telstraJoined);
		
		DAG dag = dagBuilder.build();
		
		Dataset<TelstraTrain> telstra = new DatasetImpl<>(dtDefinitionTelstraTrain);
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			processorDAGManager.executeProcessing(dag, telstra);
			transaction.commit();
		};
		
		//Test Train Postgresql
		TrainResponse trainResponse = modelManager.train("telstra", 0);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.70).compareTo(trainResponse.getScore().getScoreMean()));
		
		//Test Score
		ScoreResponse scoreResponse = modelManager.score("telstra", 1);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.70).compareTo(scoreResponse.getScore().getScoreMean()));
	}
	
	private void waitAndExpectIndexation(final long expectedCount, DtDefinition dtDefinition) {
		waitAndExpectIndexation(expectedCount, null, dtDefinition);
	}

	private void waitAndExpectIndexation(final Entity expectedEntity, DtDefinition dtDefinition) {
		waitAndExpectIndexation(-1, expectedEntity, dtDefinition);
	}
	
	private void waitAndExpectIndexation(final long expectedCount, final Entity expectedEntity, DtDefinition dtDefinition) {
		final long time = System.currentTimeMillis();
		long size = -1;
		Entity entity = null;
		try {
			do {
				Thread.sleep(250); //wait index was done

				if (expectedEntity != null) {
					try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
						UID entityURI = expectedEntity.getUID();
						entity = entityStoreManager.readOne(entityURI);
					}
					
					if (entity.equals(expectedEntity)) {
						break; //si l'entité correspond à l'entité attendue on sort.
					}
				} else {
					try (VTransactionWritable tx = transactionManager.createCurrentTransaction()) {
						size = entityStoreManager.count(dtDefinition);
					}
					
					if (size == expectedCount) {
						break; //si le nombre est atteint on sort.
					}
				}

			} while (System.currentTimeMillis() - time < 5000);//timeout 5s
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt(); //si interrupt on relance
		}
		Assertions.assertEquals(expectedCount, size);
	}
}

