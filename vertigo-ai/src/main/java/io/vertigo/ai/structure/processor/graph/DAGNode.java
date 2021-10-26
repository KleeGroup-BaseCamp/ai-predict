package io.vertigo.ai.structure.processor.graph;

import java.util.List;

import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Processor;

public class DAGNode {
	
	private int id;
	private List<DAGNode> predecessors;
	private List<DAGNode> successors;
	
	private Processor processor;
	private boolean isRoot;
	private boolean isInit;
	private boolean isTerminal;
	private Dataset dataset;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<DAGNode> getPredecessors() {
		return predecessors;
	}
	
	public void setPredecessors(List<DAGNode> predecessors) {
		this.predecessors = predecessors;
	}
	
	public List<DAGNode> getSuccessors() {
		return successors;
	}
	
	public void setSuccessors(List<DAGNode> successors) {
		this.successors = successors;
	}
	
	public Processor getProcessor() {
		return processor;
	}
	
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
	public boolean isRoot() {
		return isRoot;
	}
	
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public boolean isInit() {
		return isInit;
	}
	
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	
	public boolean isTerminal() {
		return isTerminal;
	}
	
	public void setTerminal(boolean isTerminal) {
		this.isTerminal = isTerminal;
	}
	
	public Dataset getDataset() {
		return dataset;
	}
	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
}
