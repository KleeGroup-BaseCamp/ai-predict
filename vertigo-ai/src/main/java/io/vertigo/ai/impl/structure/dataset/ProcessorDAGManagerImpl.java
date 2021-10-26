package io.vertigo.ai.impl.structure.dataset;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorDAGManager;
import io.vertigo.ai.structure.processor.ProcessorTypes;
import io.vertigo.ai.structure.processor.graph.DAG;
import io.vertigo.ai.structure.processor.graph.DAGBuilder;
import io.vertigo.ai.structure.processor.graph.DAGNode;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

public final class ProcessorDAGManagerImpl implements ProcessorDAGManager {

	private DatasetProcessingPlugin datasetProcessingPlugin;
	
	private VTransactionManager transactionManager;

	@Inject
	public ProcessorDAGManagerImpl(final DatasetProcessingPlugin datasetProcessingPlugin, final VTransactionManager transactionManager) {
		this.datasetProcessingPlugin = datasetProcessingPlugin;
		this.transactionManager = transactionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DAGBuilder createBuilder() {
		return new DAGBuilder();
	}

	@Override
	public <E extends Entity> Dataset<?> executeProcessing(DAG dag, Dataset<E> outputDataset) {

		//Parcours en largeur
		Set<DAGNode> visited = new HashSet<>();
		Queue<DAGNode> toVisit = new ArrayDeque<>();

		visited.addAll(dag.getInitNodes());
		
		List<DAGNode> entryNodes = dag.getInitNodes()
				.stream()
				.filter(dn -> !dn.getSuccessors().isEmpty())
				.flatMap(dn -> dn.getSuccessors().stream())
				.filter(dn -> visited.containsAll(dn.getPredecessors()))
				.collect(Collectors.toList());

		toVisit.addAll(entryNodes);

		while (!toVisit.isEmpty()) {
			DAGNode dagNode = toVisit.remove();

			if (!visited.contains(dagNode)) {

				Processor p = dagNode.getProcessor();
				List<DAGNode> inputNodes = dagNode.getPredecessors();
				Dataset result = executeDAGProcessing(inputNodes, p);
				dagNode.setDataset(result);
				visited.add(dagNode);

				if (!dagNode.isTerminal() ) {
					List<DAGNode> nodeToVisit = dagNode.getSuccessors()
							.stream()
							.filter(node -> visited.containsAll(node.getPredecessors()))
							.collect(Collectors.toList());
					toVisit.addAll(nodeToVisit);
				}

			}
			toVisit.removeAll(visited);
		}
		
		return datasetProcessingPlugin.build(dag.getEndNode().getDataset(), outputDataset);
	}

	public <E extends Entity, F extends DtObject> Dataset<?> executeDAGProcessing(List<DAGNode> inputNodes, Processor processor) {

		List<Map<String, Object>> sortList = new ArrayList<Map<String, Object>>();
		Map<String, Object> filterParam = new HashMap<String, Object>();

		//FIXME: Manage more than 2 DS without param Map
		DAGNode mainInputNode = inputNodes.get(0);
		
		Dataset<E> processDataset = mainInputNode.getDataset();

		int count = 0;
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
				params.put("order", mainInputNode.getId());
				switch (type) {
					case SELECT:
						processDataset = datasetProcessingPlugin.select(processDataset, params);
						filterParam.clear();
						sortList.clear();
						break;
					case JOIN:
						//FIXME: Manage more than 2 DS without param Map
						DAGNode rightInputNode = inputNodes.get(1);
						params.put("rightDataset", rightInputNode.getDataset());
						
						processDataset = datasetProcessingPlugin.join(processDataset, params);
						filterParam.clear();
						sortList.clear();
						break;
					case GROUPBY:
						processDataset = datasetProcessingPlugin.group(processDataset, params);
						filterParam.clear();
						sortList.clear();
						break;
					case PIVOT:
						processDataset = datasetProcessingPlugin.pivot(processDataset, params);
						filterParam.clear();
						sortList.clear();
						break;
					case WINDOW:
						processDataset = datasetProcessingPlugin.window(processDataset, params);
						filterParam.clear();
						sortList.clear();
						break;
					default:
						break;
				}

		}

		return processDataset;
	}

}
