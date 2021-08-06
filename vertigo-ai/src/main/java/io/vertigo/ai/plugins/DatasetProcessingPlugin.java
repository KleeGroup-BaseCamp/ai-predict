package io.vertigo.ai.plugins;

import java.util.Map;

import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.datamodel.structure.model.Entity;

public final class DatasetProcessingPlugin
		implements
			io.vertigo.ai.structure.processor.DatasetProcessingPlugin {

	@Override
	public <E extends Entity> io.vertigo.ai.structure.dataset.Dataset<E> select(
			io.vertigo.ai.structure.dataset.Dataset<E> dataset,
			Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends Entity> io.vertigo.ai.structure.dataset.Dataset<E> join(
			io.vertigo.ai.structure.dataset.Dataset<E> dataset,
			Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
