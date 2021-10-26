package io.vertigo.ai.structure.processor.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertigo.ai.impl.structure.processor.ProcessorImpl;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.AgregatorType;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorTypes;
import io.vertigo.ai.structure.processor.Window;

public class DAGBuilder {

	private List<DAGNode> initNodes = new ArrayList<>();
	private DAGNode endNode;
	private int nodeCounter = 0;
	
	public DAGBuilder() {
		super();
	}

	public DAGNode createUnaryDAGOutputNode(Processor processor, DAGNode inNode) {
		
		DAGNode nodeOutput = createDAGNode();
		nodeOutput.setPredecessors(Arrays.asList(inNode));
		nodeOutput.setProcessor(processor);
		
		inNode.getSuccessors().add(nodeOutput);

		return nodeOutput;
	}
	
	public DAGNode createNaryDAGOutputNode(Processor processor, List<DAGNode> inNodes) {
		
		DAGNode nodeOutput = createDAGNode();
		nodeOutput.setPredecessors(inNodes);
		nodeOutput.setProcessor(processor);
		
		for (DAGNode dagNode : inNodes) {
			dagNode.getSuccessors().add(nodeOutput);
		}

		return nodeOutput;
	}
	
	public DAGNode join(final DAGNode left, final DAGNode right, String leftField, String rightField) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("rightDataset", right.getDataset());
		params.put("leftField", leftField);
		params.put("rightField", rightField);
		Processor processor = new ProcessorImpl(left.getId(), ProcessorTypes.JOIN, params);
		
		return createNaryDAGOutputNode(processor, Arrays.asList(left, right));
	}
	
	public DAGNode groupBy(final DAGNode node, final String field, AgregatorType... aggType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("field", field);
		params.put("aggType", aggType);
		Processor processor = new ProcessorImpl(node.getId(), ProcessorTypes.GROUPBY, params);
		
		return createUnaryDAGOutputNode(processor, node);
	}
	
	public DAGNode pivot(final DAGNode node, final String pivotColumn, final List<String> pivotStaticValues, final List<String> rowIdfields, List<Agregator> aggs) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pivotColumn", pivotColumn);
		params.put("pivotStaticValues", pivotStaticValues);
		params.put("rowIdfields", rowIdfields);
		params.put("aggs", aggs);
		Processor processor = new ProcessorImpl(node.getId(), ProcessorTypes.PIVOT, params);
		
		return createUnaryDAGOutputNode(processor, node);
	}
	
	public DAGNode window(final DAGNode node, List<String> fields, List<Window> windows, List<Agregator> aggs) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fields", fields);
		params.put("windows", windows);
		params.put("aggs", aggs);
		Processor processor = new ProcessorImpl(node.getId(), ProcessorTypes.WINDOW, params);
		
		return createUnaryDAGOutputNode(processor, node);
	}
	
	public DAGBuilder addInitNode(DAGNode node) {
		initNodes.add(node);
		return this;
	}
	
	public DAGBuilder setEndNode(DAGNode node) {
		endNode = node;
		endNode.setTerminal(true);
		return this;
	}
	
	private DAGNode createDAGNode(boolean init) {
		DAGNode dagNode = new DAGNode();
		dagNode.setId(nodeCounter++);
		dagNode.setInit(init);
		dagNode.setSuccessors(new ArrayList<>());
		return dagNode;
	}
	
	public DAGNode createDAGNode() {
		return createDAGNode(false);
	}
	
	public DAGNode createDAGInitNode(Dataset dataset) {
		DAGNode dagNode = createDAGNode(true);
		dagNode.setDataset(dataset);
		return dagNode;
	}
	
	public DAG build() {
		DAGNode rootNode = new DAGNode();
		rootNode.setId(nodeCounter++);
		rootNode.setRoot(true);
		rootNode.setSuccessors(initNodes);
		
		DAG dag = new DAG();
		dag.setRootNode(rootNode);
		
		for (DAGNode dagNode : initNodes) {
			dagNode.setPredecessors(Arrays.asList(rootNode));
		}
		
		dag.setInitNodes(initNodes);
		dag.setEndNode(endNode);
		
		return dag;
	}
	
}