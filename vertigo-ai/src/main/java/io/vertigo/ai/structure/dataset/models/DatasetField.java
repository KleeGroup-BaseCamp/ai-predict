package io.vertigo.ai.structure.dataset.models;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatasetField extends AccessibleObject{
	
	private String name;
	private Class<?> type;
	
	/**
	 * Build a DatasetField based on an existing field
	 * @param field the original field
	 */
	public DatasetField(Field field) {
		this.name = field.getName();
		this.type = field.getType();
	}
	
	/*
	 * Copy a DatasetField
	 */
	public DatasetField(DatasetField field) {
		this.name = field.getName();
		this.type = field.getType();
	}
	
	/*
	 * Build a Dataset field with a given name and type 
	 */
	public DatasetField(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public static List<DatasetField> createDatasetFields(Collection<Field> fields) {
		List<DatasetField> datasetFields = new ArrayList<DatasetField>();
		fields.forEach(field -> datasetFields.add(new DatasetField(field)));
		return datasetFields;
	}
	
	public boolean equals(DatasetField other) {
		return name.equals(other.getName());
	}
	
	@Override
	public String toString() {
		return name + " (" + type + ")";
	}
}
