package io.vertigo.ai.example.iris.train;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.server.models.AIPredictScoreResponse;
import io.vertigo.ai.server.models.TrainResponse;

public class IrisTrainTest {


	public static void testTrainPostgresql(ModelManager modelManager) throws JsonParseException, JsonMappingException, IOException {
		TrainResponse response = modelManager.train("iris", 2);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(response.getScore().getScoreMean()));
	}
	
	public static void testScore(ModelManager modelManager) {
		AIPredictScoreResponse response = modelManager.score("iris", 2);
		Assertions.assertEquals(-1, BigDecimal.valueOf(0.92).compareTo(response.getScore().getScoreMean()));
	}
}
