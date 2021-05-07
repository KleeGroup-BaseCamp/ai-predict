package io.vertigo.ai.train.sparkCassandra;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.inject.Inject;

import org.apache.spark.SparkConf;
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

import io.vertigo.ai.predict.PredictionManager;
import io.vertigo.ai.train.models.TrainResponse;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.database.sql.SqlManager;

public abstract class AbstractSparkCassandraTrainManagerTest {
    
	@Inject
	private ResourceManager resourceManager;

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
	

	protected abstract boolean commitRequiredOnSchemaModification();

	@AfterEach
	public final void tearDown() throws Exception {
		if (node != null) {
				dropDatas();
				node.close();
			}
		}
	
	
	protected abstract NodeConfig buildNodeConfig();
	
	private void dropDatas() {
		
	}
	
	private HashMap<String,Object> createConfig(String configName) throws JsonParseException, JsonMappingException, IOException {
		File jsonSource = new File("./src/test/java/io/vertigo/ai/train/configs/"+ configName + ".json");
		@SuppressWarnings("unchecked")
		HashMap<String,Object> config = new ObjectMapper().readValue(jsonSource , HashMap.class);
		return config;
	}
	
	private void createSparkConf() {
        SparkConf conf = new SparkConf();
        conf.setAppName("Java API demo");
        conf.setMaster("spark://127.0.0.1:7077");
        
        conf.set("spark.driver.host", "192.168.1.73");

        conf.set("spark.cassandra.connection.host", "192.168.1.73");
        conf.set("spark.cassandra.auth.username", "cassandra");            
        conf.set("spark.cassandra.auth.password", "cassandra");
        
        this.conf = conf;
	}
	
	private void createSparkContext() {
		JavaSparkContext sc = new JavaSparkContext(conf);
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/datastax/spark/spark-cassandra-connector_2.11/2.5.2/spark-cassandra-connector_2.11-2.5.2.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/datastax/spark/spark-cassandra-connector-driver_2.11/2.5.2/spark-cassandra-connector-driver_2.11-2.5.2.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/datastax/oss/java-driver-core-shaded/4.10.0/java-driver-core-shaded-4.10.0.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/datastax/oss/java-driver-shaded-guava/25.1-jre-graal-sub-1/java-driver-shaded-guava-25.1-jre-graal-sub-1.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/typesafe/config/1.4.1/config-1.4.1.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/com/datastax/oss/native-protocol/1.4.12/native-protocol-1.4.12.jar");
        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/org/reactivestreams/reactive-streams/1.0.3/reactive-streams-1.0.3.jar");

        sc.addJar("file:///C:/Users/dcouillard/.m2/repository/database/cassandra/spark/kafka/dbtest/0.0.1-SNAPSHOT/dbtest-0.0.1-SNAPSHOT.jar");
        
        this.sc = sc;
	}
	
	private void createDatas() {
        CassandraConnector connector = CassandraConnector.apply(sc.getConf());

        // Prepare the schema
        try (CqlSession session = connector.openSession()) {
        	session.execute("CREATE KEYSPACE IF NOT EXISTS traindb WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
        	session.execute("DROP TABLE IF EXISTS traindb.iristest");
            session.execute("CREATE TABLE traindb.iristest (id INT PRIMARY KEY, Sepallength DOUBLE, Sepalwidth DOUBLE, Petallength DOUBLE, Petalwidth DOUBLE, Variety TEXT)");
            session.execute("INSERT INTO traindb.iristest(id, Sepallength, Sepalwidth, Petallength, Petalwidth, Variety) VALUES (1, 2.0, 1.0, 1.0, 1.0, 'test')");
            session.execute("INSERT INTO traindb.iristest(id, Sepallength, Sepalwidth, Petallength, Petalwidth, Variety) VALUES (2, 2.0, 1.0, 1.0, 1.0, 'test')");
            session.execute("INSERT INTO traindb.iristest(id, Sepallength, Sepalwidth, Petallength, Petalwidth, Variety) VALUES (3, 2.0, 1.0, 1.0, 1.0, 'test')");
            session.execute("INSERT INTO traindb.iristest(id, Sepallength, Sepalwidth, Petallength, Petalwidth, Variety) VALUES (4, 2.0, 1.0, 1.0, 1.0, 'test')");
            session.execute("INSERT INTO traindb.iristest(id, Sepallength, Sepalwidth, Petallength, Petalwidth, Variety) VALUES (5, 2.0, 1.0, 1.0, 1.0, 'test')");
        }
	}

	
	@Test
	public void testTrainCassandraSpark() throws JsonParseException, JsonMappingException, IOException {
		HashMap<String,Object> config = createConfig("cassandra_spark");
		TrainResponse response = predictionManager.train(config);
		Assertions.assertEquals(1, response.getScore().getScoreMean().compareTo(BigDecimal.valueOf(0.9)));
	}
	
	@Test
	public void testDelete() {
		long response = predictionManager.delete("iris-classification-cassandra-spark", 1);
		Assertions.assertEquals(204, response);
	}
}
