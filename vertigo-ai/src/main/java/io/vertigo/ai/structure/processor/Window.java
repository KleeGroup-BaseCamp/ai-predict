package io.vertigo.ai.structure.processor;


import java.util.List;
import java.util.Optional;

public class Window {
	   
	private String id;
	private List<String> partitionField;
	private List<OrderField> orderFields;
	private Optional<SlidingWindow> slidingWindow;
	
	public Window(String id, List<String> partitionField, List<OrderField> orderFields, Optional<SlidingWindow> slidingWindow) {
		super();
		this.id = id;
		this.partitionField = partitionField;
		this.orderFields = orderFields;
		this.slidingWindow = slidingWindow;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getPartitionField() {
		return partitionField;
	}
	
	public void setPartitionField(List<String> partitionField) {
		this.partitionField = partitionField;
	}
	
	public List<OrderField> getOrderFields() {
		return orderFields;
	}

	public void setOrderFields(List<OrderField> orderFields) {
		this.orderFields = orderFields;
	}

	public Optional<SlidingWindow> getSlidingWindow() {
		return slidingWindow;
	}
	
	public void setSlidingWindow(Optional<SlidingWindow> slidingWindow) {
		this.slidingWindow = slidingWindow;
	}
	
}