package io.vertigo.ai.structure.processor.graph;

import java.util.List;

public class DAG {
	
	private DAGNode rootNode;
	private List<DAGNode> initNodes;
	private DAGNode endNode;

	public DAGNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(DAGNode rootNode) {
		this.rootNode = rootNode;
	}

	public List<DAGNode> getInitNodes() {
		return initNodes;
	}

	public void setInitNodes(List<DAGNode> initNodes) {
		this.initNodes = initNodes;
	}

	public DAGNode getEndNode() {
		return endNode;
	}

	public void setEndNode(DAGNode endNode) {
		this.endNode = endNode;
	}

}