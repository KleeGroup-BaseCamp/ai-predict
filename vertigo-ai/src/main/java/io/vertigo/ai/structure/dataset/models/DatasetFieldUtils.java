package io.vertigo.ai.structure.dataset.models;

import java.lang.reflect.Field;

public class DatasetFieldUtils {

	private Integer count;
	private Double mean;
	private Double sum;
	private Double std;
	private Double max;
	private Double min;
	
	public static DatasetField getMeanField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("mean"));
	}
	
	public static DatasetField getSumField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("sum"));
	}
	
	public static DatasetField getStdField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("std"));
	}
	
	public static DatasetField getCountField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("count"));
	}
	
	public static DatasetField getMinField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("min"));
	}
	
	public static DatasetField getMaxField() throws NoSuchFieldException, SecurityException {
		return new DatasetField(DatasetFieldUtils.class.getDeclaredField("max"));
	}
}
