package io.vertigo.ai.structure.dataset.models;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatasetField extends AccessibleObject{
	
	private String name;
	private Class<?> type;
	private Field field;
	
	public DatasetField(Field field) {
		this.name = field.getName();
		this.type = field.getType();
		this.setField(field);
	}
	
	public DatasetField(DatasetField field) {
		this.name = field.getName();
		this.type = field.getType();
		this.setField(field.getField());
	}
	
	public Object get(Object o) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		return field.get(o);
	}
	
	public void set(Object o, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		field.set(o, value);
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
	
	@Override
	public String toString() {
		return name + " (" + type + ")";
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
	
	public boolean equals(DatasetField other) {
		return name.equals(other.getName());
	}
}
