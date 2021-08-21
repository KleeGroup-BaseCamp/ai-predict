package io.vertigo.ai.example.iris.predict;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.server.models.PredictResponse;

public class IrisPredictionTest {

	private ModelManager modelManager;
	private List<IrisPredict> dataset;
	
	public IrisPredictionTest(ModelManager modelManager) {
		this.modelManager = modelManager;
		IrisPredict iris1 = new IrisPredict();
		iris1.setPetalLength(BigDecimal.valueOf(0.0));
		iris1.setSepalLength(BigDecimal.valueOf(0.0));
		iris1.setPetalWidth(BigDecimal.valueOf(0.0));
		iris1.setSepalWidth(BigDecimal.valueOf(0.0));
		
		IrisPredict iris2 = new IrisPredict();
		iris2.setPetalLength(BigDecimal.valueOf(1.0));
		iris2.setSepalLength(BigDecimal.valueOf(1.0));
		iris2.setPetalWidth(BigDecimal.valueOf(1.0));
		iris2.setSepalWidth(BigDecimal.valueOf(1.0));
		dataset = Arrays.asList(
				iris1,
				iris2
				);
	}
	
	private PredictResponse testPrediction(final String predictionType) {
		PredictResponse response = modelManager.predict(dataset, "iris"+predictionType, 2);
		return response;
	}
	
	public void testPredictClassification() {
		PredictResponse response = testPrediction("classification");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictClustering() {
		PredictResponse response = testPrediction("clustering");
		Assertions.assertEquals(BigDecimal.ONE, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictKNN() {
		PredictResponse response = testPrediction("knn");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictSVC() {
		PredictResponse response = testPrediction("svc");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}

	
	public void testPredictAllClassifier() {
		for(int i = 0; i < 15 ; i++) {
			modelManager.activate("iris", i);
			PredictResponse response = modelManager.predict(dataset, "iris", i);
			Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
		}
	}
	
	public void testPredictXGBClassifier() {
		modelManager.activate("iris", 9);
		PredictResponse response = modelManager.predict(dataset, "iris", 9);
		Assertions.assertEquals("Setosa", response.getPredictionList().get(0).getPredictionLabel());
	}
	
	public void testPredictLogistic() {
		PredictResponse response = testPrediction("logistic");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictKeras() {
		PredictResponse response = testPrediction("keras");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}

	public void testPredictGradient() {
		PredictResponse response = testPrediction("gradient");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictExtra() {
		PredictResponse response = testPrediction("extra");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictQDAr() {
		PredictResponse response = testPrediction("qda");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictLDA() {
		PredictResponse response = testPrediction("lda");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
	
	public void testPredictMLP() {
		PredictResponse response = testPrediction("mlp");
		Assertions.assertEquals(BigDecimal.ZERO, response.getPredictionList().get(0).getPredictionNumeric());
	}
}