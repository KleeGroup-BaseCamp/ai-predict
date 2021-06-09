package io.vertigo.ai.example.train;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.ai.example.Preprocessing;
import io.vertigo.ai.example.data.models.Train;
import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.models.Row;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.database.sql.SqlManager;

public abstract class AbstractExampleTrainManagerTest {

	@Inject
	protected SqlManager dataBaseManager;
	
	@Inject
	protected PredictionManager predictionManager;
	
	private AutoCloseableNode node;
	private SparkConf conf;
	private JavaSparkContext sc;
	
	@BeforeEach
	public final void setUp() throws Exception {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		createSparkConf();
		createSparkContext();
		createDatas();
	}
	
	@AfterEach
	public final void tearDown() throws Exception {
		sc.close();
		if (node != null) {
				node.close();
			}
		}

	protected abstract NodeConfig buildNodeConfig();
	
	private void createSparkConf() throws UnknownHostException {
        SparkConf conf = new SparkConf();
        conf.setAppName("Java API demo");
        conf.setMaster("spark://127.0.0.1:7077");
        
        String localhost = InetAddress.getLocalHost().getHostAddress();
        conf.set("spark.driver.host", localhost);

        conf.set("spark.cassandra.connection.host", localhost);
        conf.set("spark.cassandra.auth.username", "cassandra");            
        conf.set("spark.cassandra.auth.password", "cassandra");
        
        this.conf = conf;
	}
	
	private void createSparkContext() {
		JavaSparkContext sc = new JavaSparkContext(conf);
		String userHome = System.getProperty("user.home");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/datastax/spark/spark-cassandra-connector_2.11/2.5.2/spark-cassandra-connector_2.11-2.5.2.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/datastax/spark/spark-cassandra-connector-driver_2.11/2.5.2/spark-cassandra-connector-driver_2.11-2.5.2.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/datastax/oss/java-driver-core-shaded/4.10.0/java-driver-core-shaded-4.10.0.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/datastax/oss/java-driver-shaded-guava/25.1-jre-graal-sub-1/java-driver-shaded-guava-25.1-jre-graal-sub-1.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/typesafe/config/1.4.1/config-1.4.1.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/com/datastax/oss/native-protocol/1.4.12/native-protocol-1.4.12.jar");
        sc.addJar("file:///"+userHome+"/.m2/repository/org/reactivestreams/reactive-streams/1.0.3/reactive-streams-1.0.3.jar");

        sc.addJar("./src/main/resources/io/vertigo/ai/models/telstra-0.0.1-SNAPSHOT.jar");
        
        this.sc = sc;
	}
	
	private HashMap<String,Object> createConfig(String configName) throws JsonParseException, JsonMappingException, IOException {
		File jsonSource = new File("./src/main/resources/io/vertigo/ai/configs/"+ configName + ".json");
		@SuppressWarnings("unchecked")
		HashMap<String,Object> config = new ObjectMapper().readValue(jsonSource , HashMap.class);
		return config;
	}
	
	private void createDatas() throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		CassandraConnector connector = CassandraConnector.apply(sc.getConf());

        // Prepare the schema
        try (CqlSession session = connector.openSession()) {
        	session.execute("CREATE KEYSPACE IF NOT EXISTS traindb WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
        	session.execute("DROP TABLE IF EXISTS traindb.telstra");
            session.execute("CREATE TABLE traindb.telstra (id DOUBLE PRIMARY KEY, location TEXT, logFeature TEXT, severityType TEXT, volume DOUBLE, volumeMean DOUBLE, volumeSum DOUBLE, volumeMin DOUBLE, volumeMax DOUBLE, volumeStd DOUBLE, resourceTypeCount DOUBLE, severityFault DOUBLE, featureCount DOUBLE, resourceType TEXT, eventPerId DOUBLE, eventType TEXT, locationCount DOUBLE)");
        } 
        // Prepare the schema
        Dataset dataset = Preprocessing.runPreprocessing();
        List<Row> rows = dataset.collect();
        List<Train> records = new ArrayList<>();
        rows.forEach(row -> {
			try {
				records.add(row.toObject(Train.class));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
        System.out.println(records.get(0).getLocation());
        System.out.println(records.get(0).getLocation());
        System.out.println(records.get(0).getLocation());
        System.out.println(records.get(0).getLocation());
        System.out.println(records.get(0).getLocation());
        System.out.println(records.get(0).getLocation());
        HashMap<String,String> colmap = new HashMap<String,String>();
        colmap.put("eventPerId", "eventperid");
        colmap.put("eventType", "eventtype");
        colmap.put("locationCount", "locationcount");
        colmap.put("logFeature", "logfeature");
        colmap.put("volumeMean", "volumemean");
        colmap.put("volumeSum", "volumesum");
        colmap.put("volumeMin", "volumemin");
        colmap.put("volumeMax", "volumemax");
        colmap.put("volumeStd", "volumestd");
        colmap.put("resourceTypeCount", "resourcetypecount");
        colmap.put("severityFault", "severityfault");
        colmap.put("featureCount", "featurecount");
        colmap.put("resourceType", "resourcetype");
        colmap.put("severityType", "severitytype");
        
        JavaRDD<Train> irisRDD = sc.parallelize(records);
        javaFunctions(irisRDD).writerBuilder("traindb", "telstra", mapToRow(Train.class, colmap)).saveToCassandra();
	}
	
	@Test
	public void testTrain() throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,Object> config = createConfig("telstra_kaggle");
		TrainResponse response = predictionManager.train(config);
		System.out.println(response.getScore().getScoreMean());
		Assertions.assertEquals(1, response.getScore().getScoreMean().compareTo(BigDecimal.valueOf(0.70)));
		predictionManager.delete("telstra", 0);
	}

	
}
