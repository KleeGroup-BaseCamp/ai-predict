package io.vertigo.ai.structure.dataset.models;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.models.Row;
import io.vertigo.datamodel.structure.model.UID;

public class Dataset implements Serializable, Iterable<Row> {

	private static final long serialVersionUID = 1L;
	private List<Field> columns;
	private List<Row> data;
	private DatasetDefinition datasetDefinition = null;
	
	
    /**
     * Construct an empty dataset for a specified item with a datasetDefinition.
     */
	public Dataset(final Class<?> itemClass, final DatasetDefinition datasetDefinition) {
		this(Arrays.asList(itemClass.getDeclaredFields()), Collections.<Row>emptyList(), datasetDefinition);
	}
	
    /**
     * Construct an empty dataset for a specified item.
     */
	public Dataset(final Class<?> itemClass) {
		this(Arrays.asList(itemClass.getDeclaredFields()), Collections.<Row>emptyList(), null);
	}
	
    /**
     * Construct an dataset.
     */
	public <I> Dataset(final Class<I> itemClass, final List<I> data)  {
		this(Arrays.asList(itemClass.getDeclaredFields()), data, null);
	}
	
    /**
     * Construct an dataset with dataset definition.
     */
	public <I> Dataset(final Class<I> itemClass, final List<I> data, final DatasetDefinition datasetDefinition)  {
		this(Arrays.asList(itemClass.getDeclaredFields()), data, datasetDefinition);
	}
	
    /**
     * Construct an empty dataset with specified columns.
     */
	public Dataset(final List<Field> columns) {
		this(columns, Collections.<Row>emptyList(), null);
	}
	
    /**
     * Construct an empty dataset with specified columns and dataset definition.
     */
	public Dataset(final List<Field> columns, final DatasetDefinition datasetDefinition) {
		this(columns, Collections.<Row>emptyList(), datasetDefinition);
	}
	
    /**
     * Construct an dataset.
     */
	public <I> Dataset(final List<Field> columns, final List<I> data) {
		this(columns, data, null);
	}
	
	
    /**
     * Construct an dataset.
     */
	@SuppressWarnings("unchecked")
	public <I> Dataset(final List<Field> columns, final List<I> data, final DatasetDefinition datasetDefinition) {
		this.columns = new ArrayList<>(columns);
		if (!data.isEmpty() && Row.class.isInstance(data.get(0))) {
			this.data = (List<Row>) data;
		} else {
			this.data = new ArrayList<Row>();
			for (I item : data) {
				this.data.add(new Row(item));
			}
		}
		this.datasetDefinition = datasetDefinition;
		
	}
	
	/**
	 * Copy an existing dataset
	 * @param dataset the dataset to copy
	 */
	public  Dataset(Dataset dataset) {
		this.data = dataset.collect();
		this.columns = dataset.fields();
		this.datasetDefinition = dataset.getDefinition();
	}
	
	
	// DatasetDefinition getter
	
	public DatasetDefinition getDefinition() {
		return datasetDefinition;
	}
	
	// Row UIDs getter
	public List<UID<?>> getUIDs() {
		List<UID<?>> uids = new ArrayList<UID<?>>();
		forEach(row -> uids.add(row.getUID()));
		return uids;
	}
	// Append methods
	
	/**
	 * Append several rows to the dataset.
	 * @param newData the rows to append
	 * @return this dataset
	 */
	public <I> Dataset append(List<I> newData) {
		data.addAll(generateRows(newData));
		return this;
	}
	
	/**
	 * Append one row to the dataset.
	 * @param newData the row to append
	 * @return this dataset
	 */
	public <I> Dataset append(I newData) {
		if (Row.class.isInstance(newData)) {
			data.add((Row) newData);
		} else {
			data.add(new Row(newData));
		}
		return this;
	}
	
	/**
	 * Append several rows indexed by the specified index to the dataset.
	 * @param index the index of the first row to append
	 * @param newData the rows to append
	 * @return this dataset
	 */
	public <I> Dataset append(Integer index, List<I> newData) {
		data.addAll(index, generateRows(newData));
		return this;
	}
	
	/**
	 * Append one row indexed by the specified index to the dataset.
	 * @param indexthe index of the row to append
	 * @param newData the row to append
	 * @return this dataset
	 */
	public <I> Dataset append(Integer index, I newData) {
		if (Row.class.isInstance(newData)) {
			data.add(index, (Row) newData);
		} else {
			data.add(index, new Row(newData));
		}
		return this;
	}
	
	// Columns selection methods
	/**
	 * Select a subset of the dataset using a single column name.
	 * @param fieldName the column to extract
	 * @return a new dataset
	 */
	public Dataset select(String fieldName) {
		List<Field> col =  getFields(fieldName);
		List<Row> values = getColumns(col);
		return new Dataset(col, values);
	}
	
	/**
	 * Select a subset of the dataset using several column names.
	 * @param fieldNames the columns to extract
	 * @return a new dataset
	 */
	public Dataset select(String... fieldNames) {
		List<Field> col =  getFields(fieldNames);
		List<Row> values = getColumns(col);
		return new Dataset(col, values);
	}
	
	// Column exceptions methods
	
	/**
	 * Select a subset of the dataset excepting a single column name.
	 * @param fieldName the column to except
	 * @return a new dataset
	 */
	public Dataset except(String fieldName) {
		List<Field> col = filterFields(fieldName);
		List<Row> values = getColumns(col);
		return new Dataset(col, values);
	}
	
	/**
	 * Select a subset of the dataset excepting several column names.
	 * @param fieldNames the columns to except
	 * @return a new dataset
	 */
	public Dataset except(String... fieldNames) {
		List<Field> col =  filterFields(fieldNames);
		List<Row> values = getColumns(col);
		return new Dataset(col, values);
	}
	
	// Data getter
	/**
	 * Get a row of the dataset by its index
	 * @param index the index of the row to extract
	 */
	public Row get(Integer index) {
		return data.get(index);
	}
	
	/**
	 * Get an Object from the dataset using its index and its column
	 * @param index the index of the row to extract
	 * @param column the column name of the row to extract
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Object get(Integer index, String column) {
		Row row = data.get(index);
		Field field = getField(column);
		return row.get(field);		
	}
	
	/**
	 * Get the first row of the dataset
	 */
	public Row first() {
		return data.get(0);
	}
	
	/**
	 * Get all the data as a list
	 */
	public List<Row> collect() {
		return data;
	}
	
	/**
	 * Get all the n first data of the dataset as a list
	 */
	public List<Row> head(int n) {
		List<Row> header = new ArrayList<>();
		for (int i=0; i<n; i++) {
			header.add(get(i));
		}
		return header;
	}
	
	// Columns list
	/**
	 * List all the column names of a dataset
	 */
	public List<String> columns() {
		List<String> col = columns.stream().map(field -> field.getName()).collect(Collectors.toList());
		return col;
	}
	
	public List<Field> fields() {
		return columns;
	}
	
	/**
	 * List all the column names of a dataset
	 */
	public Map<String, Class<?>> dTypes() {
		Map<String, Class<?>> dtypeMap = new HashMap<>();
		columns.forEach(field -> dtypeMap.put(field.getName(), field.getType()));
		return dtypeMap;
	}
	
	//Columns stats

	public Double mean(String columnName) {
		List<Row> col = getColumn(columnName);
		Field field = getField(columnName);
		List<Double> doubleList = new ArrayList<Double>();
		col.forEach(row -> doubleList.add(Double.valueOf(row.get(field).toString())));
		Double mean = doubleList.stream().mapToDouble(d -> d).average().orElse(0.0);
		return mean;
	}
	
	public OptionalDouble max(String columnName) {
		List<Row> col = getColumn(columnName);
		Field field = getField(columnName);
		OptionalDouble max = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).max();
		return max;
	}
	
	public OptionalDouble min(String columnName) {
		List<Row> col = getColumn(columnName);
		Field field = getField(columnName);
		OptionalDouble min = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).min();
		return min;
	}
	public Double sum(String columnName) {
		List<Row> col = getColumn(columnName);
		Field field = getField(columnName);
		Double sum = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).sum();
		return sum;
	}
	
	// For Each
	public void forEach(Consumer<? super Row> action) {
		data.forEach(action);
	}
	//Filter rows
	
	public Dataset filter(Predicate<Row> pred) {
		List<Row> newData = data.stream().filter(pred).collect(Collectors.<Row>toList());
		return new Dataset(this.columns, newData);
	}
	
	// OrderBy
	
	/**
	 * @param columnName name of the column use for sorting
	 * @param sortOrder the sorting order of the dataset. Must be one of ASC, ascending, DESC, descending.
	 * @return this dataset
	 */
	public Dataset orderBy(String columnName, String sortOrder) {
		Field field = getField(columnName);
		data.sort((Row o1, Row o2)-> {
			try {
				switch(sortOrder) {
				case "ASC" :
					return o1.compareTo(o2, field);
				case "ascending":
					return o1.compareTo(o2, field);
				case "DESC" :
					return - o1.compareTo(o2, field);
				case "descending":
					return - o1.compareTo(o2, field); 
				default:
					throw new IllegalArgumentException("sortOrder badly configured. Must be ASC, ascending, DESC, descending.");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return 0;
			}
		});
		return this;
	}
		
	public Dataset orderBy(String columnName) {
		return orderBy(columnName, "ASC");		
	}
	
	public Dataset orderBy(Comparator<Row> comparator) {
		data.sort((Row o1, Row o2)-> {return comparator.compare(o1, o2);});
		return this;
	}

	//Split and subDataset methods
	public Dataset subDataset(int begin, int end) {
		return new Dataset(columns, data.subList(begin, end));
	}
	
	public List<Dataset> split(int n) {
		List<Dataset> datasets = new ArrayList<>();
		int size = (int) count();
		int i;
		for (i=0; i<size/n; i++) {
			datasets.add(
					new Dataset(columns, data.subList(i*n, (i+1)*n))
					);
		}
		if (i*n != size) {
			datasets.add(
					new Dataset(columns, data.subList(i*n, size))
					);
		}
		return datasets;
	}
	
	// Join
	
	public Dataset join(Dataset other, String leftColumnName, String rigthColumnName, String how) throws IllegalArgumentException, IllegalAccessException {
		Dataset joinedDataset = Combining.join(this, other, how, leftColumnName, rigthColumnName);
		return joinedDataset;
	}
	// Group by
	
	public Dataset groupBy(String columnName, String aggType) throws IllegalArgumentException, IllegalAccessException {
		Field field = getField(columnName);
		return groupBy(field, aggType);
	}
	
	private Dataset groupBy(Field fieldBy, String aggType) throws IllegalArgumentException, IllegalAccessException {
		//Get groups
		Dataset groupDataset = new Dataset(columns);
		HashMap<Object, Dataset> map = group(fieldBy);
				
		
		for (Object key : map.keySet()) {
			Row row = new Row();
			Dataset dataset = map.get(key);
			
			for (Field field : columns) {
				if (fieldBy.equals(field)) {
					row.put(field, key);
				} else {
					switch (aggType) {
						case "mean":
							row.put(field, dataset.mean(field.getName()));
							break;
						case "sum":
							row.put(field, dataset.sum(field.getName()));
							break;
					}
				}
			}
			groupDataset.append(row);
		}
		
		return groupDataset;
	}
	
	public HashMap<Object, Dataset> group(Field fieldBy) throws IllegalArgumentException, IllegalAccessException {
		//Get groups
		HashMap<Object, Dataset> map = new HashMap<Object, Dataset>();
		for (Row item : data) {
			Object itemField = item.get(fieldBy);
			if (!map.containsKey(itemField)) {
				List<Row> newData = new ArrayList<Row>();
				newData.add(item);
				Dataset dataset = new Dataset(columns, newData);
				map.put(itemField, dataset);
			} else {
				map.get(itemField).append(item);
			}
		}
		return map;
	}
	
	//Drop and remove functions
	
	/**
	 * Remove a row using its index
	 * @param index the index of the row to remove
	 * @return this dataset
	 */
	public Dataset remove(Integer index) {
		data.remove((int) index);
		return this;
	}
	
	/**
	 * Remove several rows using their index
	 * @param indexes the index of the rows to remove
	 * @return this dataset
	 */
	public Dataset remove(Integer... indexes) {
		for (int index : indexes) {
			remove(index);
		}
		return this;
	}
	
	/**
	 * Remove a row using its value
	 * @param item the value of the row to remove
	 * @return this dataset
	 */
	public Dataset remove(Row item) {
		data.remove(item);
		return this;
	}
	
	/**
	 * Remove several rows using their values
	 * @param items the value of the rows to remove
	 * @return this dataset
	 */
	public Dataset remove(Row... items) {
		for (Row item : items) {
			remove(item);
		}
		return this;
	}
	
	/**
	 * Drop a row using its index
	 * @param index the index of the row to drop
	 * @return the dropped item
	 */
	public Row drop(Integer index) {
		return data.remove((int) index);
	}
	
	/**
	 * Drop several rows using their index
	 * @param indexes the index of the rows to drop
	 * @return the dropped items
	 */
	public List<Row> drop(Integer... indexes) {
		List<Row> removedItems = new ArrayList<Row>();
		for (int index : indexes) {
			removedItems.add(drop(index));
		}
		return removedItems;
	}
	
	/**
	 * Drop a row using its value
	 * @param item the value of the row to drop
	 * @return True if the item is successfully dropped
	 */
	public Boolean drop(Row item) {
		return data.remove(item);
	}
	
	/**
	 * Drop several rows using their value
	 * @param item the value of the row to drop
	 * @return List of boolean, true if the item is successfully dropped
	 */
	public List<Boolean> drop(Row... items) {
		List<Boolean> removedItems = new ArrayList<Boolean>();
		for (Row item : items) {
			removedItems.add(drop(item));
		}
		return removedItems;
	}
	
	// Contains method
	
	/**
	 * Check if a value is contained in the dataset
	 * @param row 
	 * @return True if the dataset contains the row
	 */
	public Boolean contains(Row row) {
		return data.contains(row);
	}
	
	// isEmpty method
	/**
	 * Check if the dataset is empty
	 * @return True if empty
	 */
	public Boolean isEmpty() {
		return data.isEmpty();
	}
	
	// Count and Shape
	/**
	 * Get the numbers of rows in the dataset
	 */
	public long count() {
		return data.size();
	}
	
	/**
	 * Get the numbers of rows in the dataset and the 
	 */
	public List<Integer> shape() {
		List<Integer> shape = Arrays.asList(
				data.size(),
				columns.size()
				);
		return shape;
	}
	
	// Private methods 
	
	private List<Row> getColumn(String fieldName) {
		//get the required field
		Field field = getField(fieldName);
		//get the field from all items
		List<Row> values = getColumn(field);
		return values;
	}
	
	private List<Row> getColumn(Field field) {
		//get the field from all items
		List<Row> values = new ArrayList<Row>();
		List<Field> fields = new ArrayList<Field>();
		fields.add(field);
		for (Row row: data) {
			values.add(row.get(fields));
		}
		return values;
	}
	
	private List<Row> getColumns(List<Field> fields) {
		List<Row> values = new ArrayList<Row>();
		for (Row row: data) {
			values.add(row.get(fields));
		}
		return values;
	}
	

	//filter fields
	
	private List<Field> filterFields(String... fieldNames) {
		List<String> fieldNamesList = Arrays.asList(fieldNames);
		List<Field> filteredList = columns.stream()
		        .filter(item -> !fieldNamesList.contains(item.getName()))
		        .collect(Collectors.toList());
		return filteredList;
	}
	
	private List<Field> filterFields(String fieldNames) {
		List<String> fieldNamesList = Arrays.asList(fieldNames);
		List<Field> filteredList = columns.stream()
		        .filter(item -> !fieldNamesList.contains(item.getName()))
		        .collect(Collectors.toList());
		return filteredList;
	}
	
	// get Field
	public Field getField(String fieldName) {
		Field resField = null;
		for (Field field : columns) {
			if (field.getName() == fieldName) {
				resField = field;
				break;
			}
		}
		return resField;
	}
	
	private List<Field> getFields(String... fieldNames) {
		List<String> fieldNamesList = Arrays.asList(fieldNames);
		List<Field> fieldList = new ArrayList<Field>();
		for (Field field : columns) {
			if (fieldNamesList.contains(field.getName())) {
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	
	@SuppressWarnings("unchecked")
	private <I> List<Row> generateRows(List<I> newData) {
		if (!data.isEmpty() && Row.class.isInstance(data.get(0))) {
			return (List<Row>) newData;
		}
		List<Row> newRows = new ArrayList<Row>();
		for (I item : newData) {
			newRows.add(new Row(item));
		}
		return newRows;
	}
	
	@Override
	public Iterator<Row> iterator() {
		return data.iterator();
	}
}
