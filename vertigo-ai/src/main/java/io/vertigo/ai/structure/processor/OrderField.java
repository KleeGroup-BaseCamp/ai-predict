package io.vertigo.ai.structure.processor;


/**
 * 
 * @author xdurand
 *
 */
public class OrderField {
	
	private String fieldName;
	private SortOrder sortOrder;
	
	public OrderField(String fieldName, SortOrder sortOrder) {
		super();
		this.fieldName = fieldName;
		this.sortOrder = sortOrder;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}