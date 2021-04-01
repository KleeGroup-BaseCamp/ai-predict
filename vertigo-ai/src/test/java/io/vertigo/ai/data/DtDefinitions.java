package io.vertigo.ai.data;

import java.util.Arrays;
import java.util.Iterator;

import io.vertigo.ai.data.domain.DatasetObject;
import io.vertigo.ai.data.domain.boston.BostonItem;
import io.vertigo.ai.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.data.domain.iris.IrisItem;
import io.vertigo.ai.predict.models.Explanation;
import io.vertigo.ai.predict.models.Predict;
import io.vertigo.ai.predict.models.PredictResponse;

public final class DtDefinitions implements Iterable<Class<?>> {
	@Override
	public Iterator<Class<?>> iterator() {
		return Arrays.asList(new Class<?>[] {
				IrisItem.class, BostonItem.class, DatasetObject.class, Predict.class, PredictResponse.class, Explanation.class, BostonRegressionItem.class
		}).iterator();
	}
}