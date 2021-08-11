package io.vertigo.ai.impl.structure.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.ai.impl.structure.processor.ProcessorBuilderImpl;
import io.vertigo.ai.structure.DatasetManager;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.datamodel.structure.model.Entity;

public final class DatasetManagerImpl implements DatasetManager {

	private DatasetProcessingPlugin datasetProcessingPlugin;
	
	@Inject
	public DatasetManagerImpl(
			final DatasetProcessingPlugin datasetProcessingPlugin) {
		this.datasetProcessingPlugin = datasetProcessingPlugin;
		
	}
	
	@Override
	public ProcessorBuilder createBuilder() {
		return new ProcessorBuilderImpl();
	}

	@Override
	public <E extends Entity> Dataset<?> executeProcessing(Dataset<E> dataset,
			List<Processor> processors) {
		
		List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
		Map<String, Object> filterParam = new HashMap<String, Object>();
		
		Dataset<?> processDataset = dataset;
		
		int count = 0;
		for (Processor processor : processors) {
			String type = processor.getProcessorType();
			Map<String, Object> params = processor.getProcessorParameters();
			
			switch (type) {
				case "sort":
					sortList.add(params);
					break;
				case "filter":
					filterParam = params;
					break;
				default:
					if (!sortList.isEmpty()) {
						params.put("sort", sortList);
					}
					if (!filterParam.isEmpty()) {
						params.put("filter", filterParam);
					}
					params.put("order", count++);
					switch (type) {
						case "select":
							processDataset = datasetProcessingPlugin.select(processDataset, params);
							filterParam.clear();
							sortList.clear();
							break;
						case "join":
							processDataset = datasetProcessingPlugin.join(processDataset, params);
							filterParam.clear();
							sortList.clear();
							break;
					}
			}
			
		}
		return processDataset;
	}
	
}
