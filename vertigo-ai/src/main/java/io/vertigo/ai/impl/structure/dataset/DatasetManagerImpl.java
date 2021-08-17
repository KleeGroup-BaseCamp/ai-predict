package io.vertigo.ai.impl.structure.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.ai.impl.structure.processor.ProcessorBuilderImpl;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.ai.structure.processor.ProcessorTypes;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

public final class DatasetManagerImpl implements DatasetManager {

	private DatasetProcessingPlugin datasetProcessingPlugin;
	
	@Inject
	public DatasetManagerImpl(
			final DatasetProcessingPlugin datasetProcessingPlugin) {
		this.datasetProcessingPlugin = datasetProcessingPlugin;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProcessorBuilder createBuilder() {
		return new ProcessorBuilderImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends Entity, F extends DtObject> Dataset<?> executeProcessing(Dataset<E> inputDataset,
			Dataset<F> outputDataset,
			List<Processor> processors) {
		
		List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
		Map<String, Object> filterParam = new HashMap<String, Object>();
		
		Dataset<E> processDataset = inputDataset;
		
		int count = 0;
		for (Processor processor : processors) {
			ProcessorTypes type = processor.getProcessorType();
			Map<String, Object> params = processor.getProcessorParameters();
			
			switch (type) {
				case SORT:
					sortList.add(params);
					break;
				case FILTER:
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
						case SELECT:
							processDataset = datasetProcessingPlugin.select(processDataset, params);
							filterParam.clear();
							sortList.clear();
							break;
						case JOIN:
							processDataset = datasetProcessingPlugin.join(processDataset, params);
							filterParam.clear();
							sortList.clear();
							break;
						case GROUPBY:
							processDataset = datasetProcessingPlugin.group(processDataset, params);
							filterParam.clear();
							sortList.clear();
						default :
							break;
					}
			}
			
		}
		processDataset = datasetProcessingPlugin.build(processDataset, outputDataset);
		return processDataset;
	}
	
}
