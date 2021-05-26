package io.vertigo.ai.structure.dataset.models;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertigo.ai.structure.row.models.Row;

public class Combining {

	public static Dataset join(Dataset left, Dataset right, String how, String onLeft, String onRight) throws IllegalArgumentException, IllegalAccessException {
		DatasetField leftField = left.getField(onLeft);
		DatasetField rightField = right.getField(onRight);
		List<DatasetField> columns = Stream.concat(left.fields().stream(), right.fields().stream())
                .collect(Collectors.toList());
		columns.remove(rightField);
		switch (how) {
		case "inner":
			return innerJoin(left, right, columns, leftField, rightField);
		case "left":
			return leftJoin(left, right, columns, leftField, rightField);
		case "right":
			return leftJoin(right, left, columns, rightField, leftField);
		case "full":
			return fullJoin(right, left, columns, rightField, leftField);
		default:
			return null;
		}
	}
	
	public static Dataset innerJoin(Dataset left, Dataset right, List<DatasetField> columns, DatasetField leftField, DatasetField rightField) throws IllegalArgumentException, IllegalAccessException {
		HashMap<Object, Dataset> rightMap = right.group(rightField);
		Dataset dataset = new Dataset(columns);
		for (Row leftRow : left) {
			Object key = leftRow.get(leftField);
			if (rightMap.containsKey(key)) {
				for (Row rightRow : rightMap.get(key)) {
					Row item = new Row(leftRow);
					Row rightItem = new Row(rightRow);
					rightItem.remove(rightField);
					item.putAll(rightRow);
					dataset.append(item);
				}
				
			}
		}
		return dataset;	
	}
	
	public static Dataset leftJoin(Dataset left, Dataset right, List<DatasetField> columns, DatasetField leftField, DatasetField rightField) throws IllegalArgumentException, IllegalAccessException {
		HashMap<Object, Dataset> rightMap = right.group(rightField);
		Dataset dataset = new Dataset(columns);
		
		for (Row leftRow : left) {
			Object key = leftRow.get(leftField);
			if (rightMap.containsKey(key)) {
				
				for (Row rightRow : rightMap.get(key)) {
					Row newRow = new Row();
					leftRow.forEach((k, v) -> newRow.put(k, v));
					rightRow.forEach((k, v) -> {if(!k.equals(rightField)){newRow.put(k, v);}});
					dataset.append(newRow);
				}
			} else {
				HashMap<DatasetField, Object> rightRowContent = new HashMap<DatasetField, Object>();
				for (DatasetField field: right.fields()) {
					if (! field.equals(rightField)) {
						rightRowContent.put(field, null);
					}
				}
				Row newRow = new Row();
				leftRow.forEach((k, v) -> newRow.put(k, v));
				rightRowContent.forEach((k, v) -> newRow.put(k, v));
				dataset.append(newRow);
			}
		}
		return dataset;	
	}
	
	public static Dataset fullJoin(Dataset left, Dataset right, List<DatasetField> columns, DatasetField leftField, DatasetField rightField) throws IllegalArgumentException, IllegalAccessException {
		HashMap<Object, Dataset> rightMap = right.group(rightField);
		Dataset dataset = new Dataset(columns);
		for (Row leftRow : left) {
			Object key = leftRow.get(leftField);
			if (rightMap.containsKey(key)) {
				for (Row rightRow : rightMap.get(key)) {
					Row item = new Row(leftRow);
					Row rightItem = new Row(rightRow);
					rightItem.remove(rightField);
					Row row = item.join(rightRow);
					dataset.append(row);
				}
			} else {
				HashMap<DatasetField, Object> rightRowContent = new HashMap<DatasetField, Object>();
				for (DatasetField field: right.fields()) {
					if (! field.equals(rightField)) {
						rightRowContent.put(field, null);
					}
				}
				Row item = new Row(leftRow);
				Row rightRow = new Row(rightRowContent);
				Row row = item.join(rightRow);
				dataset.append(row);
			}
			rightMap.remove(key);
		}
		
		for (Object key : rightMap.keySet()) {
			Dataset rightDataset = rightMap.get(key);
			for (Row rightRow: rightDataset) {
				HashMap<DatasetField, Object> leftRowContent = new HashMap<DatasetField, Object>();
				for (DatasetField field: left.fields()) {
					if (! field.equals(leftField)) {
						leftRowContent.put(field, null);
					}
				}
				Row leftRow = new Row(leftRowContent);
				Row item = new Row(leftRow);
				Row row = item.join(rightRow);
				dataset.append(row);
			}
		}
		return dataset;	
	}
}
