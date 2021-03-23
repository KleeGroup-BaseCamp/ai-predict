package io.vertigo.ai.predict;

import java.util.List;
import java.util.Optional;

public class PredictionEntity {
    
    private List<Object> prediction;
    private Optional<List<List<Float>>> predictionProba;
    private Optional<List<List<List<Double>>>> explanation;

    public List<Object> getPrediction() {
        return prediction;
    }

    public void setPrediction(List<Object> prediction) {
        this.prediction = prediction;
    }

    public Optional<List<List<Float>>> getPredictionProba() {
        return predictionProba;
    }

    public void setPredictionProba(List<List<Float>> predictionProba) {
        this.predictionProba = Optional.ofNullable(predictionProba);
    }

    public Optional<List<List<List<Double>>>> getExplanation() {
        return explanation;
    }

    public void setExplanation(List<List<List<Double>>> explanation) {
        this.explanation = Optional.ofNullable(explanation);
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("prediction : " + this.prediction + ", ");
        stringBuilder.append("predictionProba : " + this.predictionProba + ", ");
        stringBuilder.append("explanation : " + this.explanation);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
