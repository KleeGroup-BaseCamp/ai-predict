package io.vertigo.ai.predict.data;

import java.util.Arrays;
import java.util.Iterator;

import io.vertigo.ai.predict.data.domain.DatasetObject;
import io.vertigo.ai.predict.data.domain.boston.BostonItem;
import io.vertigo.ai.predict.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.predict.data.domain.iris.IrisItem;
import io.vertigo.ai.server.models.Explanation;
import io.vertigo.ai.server.models.Predict;
import io.vertigo.ai.server.models.PredictResponse;

public final class DtDefinitions implements Iterable<Class<?>> {
	@Override
	public Iterator<Class<?>> iterator() {
		return Arrays.asList(new Class<?>[] {
				IrisItem.class, BostonItem.class, DatasetObject.class, Predict.class, PredictResponse.class, Explanation.class, BostonRegressionItem.class
		}).iterator();
	}
}