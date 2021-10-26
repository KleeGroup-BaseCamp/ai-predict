package io.vertigo.ai.structure.dataset.models;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.row.models.Row;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.model.UID;

public class DatasetOpeOld implements Serializable, Iterable<Row> {

	private static final long serialVersionUID = 1L;
	private List<DatasetField> columns;
	private List<Row> data;
	private DatasetDefinition datasetDefinition = null;
	private List<Class<?>> numericFields = Arrays.asList(Integer.class, long.class, int.class, Long.class, float.class, Double.class, double.class, Float.class);
	
	
    /**
     * Construct an empty dataset for a specified item with a datasetDefinition.
     */
	public DatasetOpeOld(final Class<?> itemClass, final DatasetDefinition datasetDefinition) {
		this(Arrays.asList(itemClass.getDeclaredFields()), Collections.<Row>emptyList(), datasetDefinition);
	}
	
    /**
     * Construct an empty dataset for a specified item.
     */
	public DatasetOpeOld(final Class<?> itemClass) {
		this(Arrays.asList(itemClass.getDeclaredFields()), Collections.<Row>emptyList(), null);
	}
	
    /**
     * Construct an dataset.
     */
	public <I> DatasetOpeOld(final Class<I> itemClass, final List<I> data)  {
		this(Arrays.asList(itemClass.getDeclaredFields()), data, null);
	}
	
    /**
     * Construct an dataset with dataset definition.
     */
	public <I> DatasetOpeOld(final Class<I> itemClass, final List<I> data, final DatasetDefinition datasetDefinition)  {
		this(Arrays.asList(itemClass.getDeclaredFields()), data, datasetDefinition);
	}
	
    /**
     * Construct an empty dataset with specified columns.
     */
	public DatasetOpeOld(final List<? extends AccessibleObject> columns) {
		this(columns, Collections.<Row>emptyList(), null);
	}
	
    /**
     * Construct an empty dataset with specified columns and dataset definition.
     */
	public DatasetOpeOld(final List<? extends AccessibleObject> columns, final DatasetDefinition datasetDefinition) {
		this(columns, Collections.<Row>emptyList(), datasetDefinition);
	}
	
    /**
     * Construct an dataset.
     */
	public <I> DatasetOpeOld(final List<? extends AccessibleObject> columns, final List<I> data) {
		this(columns, data, null);
	}
	
    /**
     * Construct an dataset.
     */
	@SuppressWarnings("unchecked")
	public <I> DatasetOpeOld(final List<? extends AccessibleObject> columns, final List<I> data, final DatasetDefinition datasetDefinition) {
		//if a list of field is provided, converts it to a list of DatasetFields. Else, assumes that a list of DatasetField is provided and copies this list as a mutable list.
		if (Field.class.isInstance(columns.get(0))) {
				this.columns = DatasetField.createDatasetFields((List<Field>) columns);
		} else {
			List<DatasetField> cols = new ArrayList<DatasetField>();
			cols.addAll( (List<DatasetField>) columns);
			this.columns = cols;
		}
		
		//if a list of row is provided as data, copies it as a mutable list. Else, convertw all items of data as row and add them to data.
		if (!data.isEmpty() && Row.class.isInstance(data.get(0))) {
			List<Row> rows = new ArrayList<Row>();
			rows.addAll( (List<Row>) data);
			this.data = rows;
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
	public  DatasetOpeOld(final DatasetOpeOld dataset) {
		this.data = dataset.collect();
		this.columns = dataset.fields();
		this.datasetDefinition = dataset.getDefinition();
	}
	
	public DatasetOpeOld(DtDefinition output) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * DatasetDefinition getter
	 */
	public DatasetDefinition getDefinition() {
		return datasetDefinition;
	}
	
	
	/**
	 * Row UIDs getter. Can be used for chunk usage.
	 */
	public List<UID<?>> getUIDs() {
		List<UID<?>> uids = new ArrayList<UID<?>>();
		forEach(row -> uids.add(row.getUID()));
		return uids;
	}
	
	/**
	 * Append several items to the dataset. These items are wrapped into rows if necessary.
	 * @param newData the items to append
	 * @return this dataset
	 */
	@SuppressWarnings("unchecked")
	public <I> DatasetOpeOld append(final List<I> newData) {
		if (!newData.isEmpty() && Row.class.isInstance(newData.get(0))) {
			data.addAll((List<Row>)newData);
		}
		for (I item : newData) {
			data.add(new Row(item));
		}
		return this;
	}
	
	/**
	 * Append one item to the dataset. This item is wrapped into row if necessary.
	 * @param newData the item to append
	 * @return this dataset
	 */
	public <I> DatasetOpeOld append(final I newData) {
		if (Row.class.isInstance(newData)) {
			data.add((Row) newData);
		} else {
			data.add(new Row(newData));
		}
		return this;
	}
	
	/**
	 * Append several items indexed by the specified index to the dataset to the dataset. These items are wrapped into rows if necessary.
	 * @param index the index where the first item will be appended
	 * @param newData the rows to append
	 * @return this dataset
	 */
	@SuppressWarnings("unchecked")
	public <I> DatasetOpeOld append(final Integer index, final List<I> newData) {
		if (!newData.isEmpty() && Row.class.isInstance(newData.get(0))) {
			data.addAll(index, (List<Row>)newData);
		}
		Integer idx = index;
		for (I item : newData) {
			data.add(idx++, new Row(item));
		}
		return this;
	}
	
	/**
	 * Append one item indexed by the specified index to the dataset to the dataset. This item is wrapped into row if necessary.
	 * @param indexthe index of the row to append
	 * @param newData the row to append
	 * @return this dataset
	 */
	public <I> DatasetOpeOld add(Integer index, I newData) {
		if (Row.class.isInstance(newData)) {
			data.add(index, (Row) newData);
		} else {
			data.add(index, new Row(newData));
		}
		return this;
	}
	
	/**
	 * Remove all duplicated rows of the dataset	
	 * @return the dataset of all distinct rows
	 */
	public DatasetOpeOld distinct() {
		List<Row> unique = data.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(Row::toString))),
                                           ArrayList::new));
		return new DatasetOpeOld(columns, unique);
	}
	
	/**
	 * Select a subset of the dataset using several column names.
	 * @param fieldNames the columns to extract
	 * @return a new dataset containing the selected columns
	 */
	public DatasetOpeOld select(String... fieldNames) {
		List<DatasetField> col =  getFields(fieldNames);
		List<Row> values = getColumns(col);
		return new DatasetOpeOld(col, values);
	}
	
	/**
	 * Select a subset of the dataset excepting a single column name.
	 * @param fieldName the column to except
	 * @return a new dataset
	 */
	public DatasetOpeOld except(String fieldName) {
		List<DatasetField> col = filterFields(fieldName);
		List<Row> values = getColumns(col);
		return new DatasetOpeOld(col, values);
	}
	
	/**
	 * Select a subset of the dataset excepting several column names.
	 * @param fieldNames the columns to except
	 * @return a new dataset
	 */
	public DatasetOpeOld except(String... fieldNames) {
		List<DatasetField> col =  filterFields(fieldNames);
		List<Row> values = getColumns(col);
		return new DatasetOpeOld(col, values);
	}
	
	/**
	 * Rename a column of the dataset
	 * @param currentName the name of the column to rename
	 * @param newName the new name of the column
	 * @return this instance of dataset
	 */
	public DatasetOpeOld rename(String currentName, String newName) {
		DatasetField currentField = getField(currentName);
		DatasetField newField = new DatasetField(currentField);
		newField.setName(newName);
		columns.remove(currentField);
		columns.add(newField);
		for (Row row: data) {
			row.rename(currentField, newField);
		}
		return this;
	}
	
	/**
	 * Get a row of the dataset by its index
	 * @param index the index of the row to extract
	 */
	public Row get(Integer index) {
		if (index < 0 || index >= count()) {
			throw new IllegalArgumentException(index + "not in the dataset bounds.");
		}
		return data.get(index);
	}
	
	/**
	 * Get an Object (item of a row) from the dataset using its index and its column name
	 * @param index the index of the row to extract
	 * @param column the column name of the object to extract
	 */
	public Object get(Integer index, String column) {
		DatasetField field = getField(column);
		if (index < 0 || index >= count()) {
			throw new IllegalArgumentException(index + " not in the dataset bounds.");
		}
		return data.get(index).get(field);		
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
	
	/**
	 * List all the column names of a dataset
	 */
	public List<String> columns() {
		List<String> col = columns.stream().map(field -> field.getName()).collect(Collectors.toList());
		return col;
	}
	
	/**
	 *  List all the DatasetField of the dataset columns
	 */
	public List<DatasetField> fields() {
		return columns;
	}
	
	/**
	 * Get the mean value of a numeric column
	 * @param columnName the name of the column
	 */
	public Double mean(String columnName) {
		DatasetField field = getField(columnName);
		// throws IllegalArgumentException is the column is not numeric. 
		if (!numericFields.contains(field.getType())) {
			throw new IllegalArgumentException(field.getName() + " is not numeric."); 
		}
		List<Row> col = getColumn(field);
		// casts all elements to Double and computes the mean
		Double mean = col.stream()
						.mapToDouble(row -> Double.valueOf(row.get(field).toString()))
						.average()
						.orElse(0.0);
		return mean;
	}
	
	/**
	 * Get the standard deviation of a numeric column
	 * @param columnName the name of the column
	 */
	public Double std(String columnName) {
		DatasetField field = getField(columnName);
		// throws IllegalArgumentException is the column is not numeric. 
				if (!numericFields.contains(field.getType())) {
					throw new IllegalArgumentException(field.getName() + " is not numeric."); 
				}
		List<Row> col = getColumn(field);
		// computes the mean
		Double mean = col.stream()
				.mapToDouble(row -> Double.valueOf(row.get(field).toString()))
				.average()
				.orElse(0.0);
		// casts all elements to Double
		List<Double> doubleList = new ArrayList<Double>();
		col.forEach(row -> doubleList.add(Double.valueOf(row.get(field).toString())));
		// computes the standard deviation
		Double temp = 0.0;
		for (Double d: doubleList)
        {
            temp += Math.pow(d-mean, 2);
        }
		Double std = Math.pow(temp, 0.5)/doubleList.size();
		return std;
	}
	
	/**
	 * Get the max value of a numeric column
	 * @param columnName the name of the column
	 */
	public OptionalDouble max(String columnName) {
		DatasetField field = getField(columnName);
		List<Row> col = getColumn(field);
		OptionalDouble max = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).max();
		return max;
	}
	
	/**
	 * Get the max value of a numeric column
	 * @param columnName the name of the column
	 */
	public OptionalDouble min(String columnName) {
		DatasetField field = getField(columnName);
		List<Row> col = getColumn(field);
		OptionalDouble min = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).min();
		return min;
	}
	
	/**
	 * Get the sum of all value of a numeric column
	 * @param columnName the name of the column
	 */
	public Double sum(String columnName) {
		DatasetField field = getField(columnName);
		List<Row> col = getColumn(field);
		Double sum = col.stream().mapToDouble(d -> Double.valueOf(d.get(field).toString())).sum();
		return sum;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void forEach(Consumer<? super Row> action) {
		data.forEach(action);
	}
	
	/**
	 * Get a dataset that match the predicate
	 * @param predicate a non-interfering, statelesspredicate to apply to each element to determine if itshould be included
	 * @return a new dataset
	 */
	public DatasetOpeOld filter(Predicate<Row> pred) {
		List<Row> newData = data.stream().filter(pred).collect(Collectors.<Row>toList());
		return new DatasetOpeOld(this.columns, newData);
	}
	
	/**
	 * Sorts this list according to the order induced by the specified sortOrder.
	 * @param columnName name of the column use for sorting
	 * @param sortOrder the sorting order of the dataset. Must be one of ASC, ascending, DESC, descending.
	 * @return this dataset
	 */
	public DatasetOpeOld orderBy(String columnName, String sortOrder) {
		DatasetField field = getField(columnName);
		data.sort((Row o1, Row o2)-> {
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

		}});
		return this;
	}
	
	/**
	 * Sorts this list according to the ascending order.
	 * @param columnName name of the column use for sorting
	 * @return this dataset
	 */
	public DatasetOpeOld orderBy(String columnName) {
		return orderBy(columnName, "ASC");		
	}
	
	/**
	 * Sorts this list according to the order induced by the specified Comparator. The sort is stable: this method must notreorder equal elements. 
	 * @param comparator the Comparator used to compare list elements.A null value indicates that the elements' natural ordering should be used.
	 * @return this dataset
	 */
	public DatasetOpeOld orderBy(Comparator<Row> comparator) {
		data.sort((Row o1, Row o2)-> {return comparator.compare(o1, o2);});
		return this;
	}

	/**
	 * Extract subset of row from the dataset.
	 * @param begin the first index to extract
	 * @param end the last index to extract
	 * @return a new dataset
	 */
	public DatasetOpeOld subDataset(int begin, int end) {
		return new DatasetOpeOld(columns, data.subList(begin, end));
	}
	
	/**
	 * Split the dataset into n new dataset
	 * @param n the number of subdatset
	 * @return a list of splitted dataset
	 */
	public List<DatasetOpeOld> split(int n) {
		List<DatasetOpeOld> datasets = new ArrayList<>();
		int size = (int) count();
		int i;
		for (i=0; i<size/n; i++) {
			datasets.add(
					subDataset(i*n, (i+1)*n)
					);
		}
		if (i*n != size) {
			datasets.add(
					subDataset(i*n, size)
					);
		}
		return datasets;
	}
	
	/**
	 * Joins two dataset on the given keys (leftColumnName, rightColumnName).
	 * @param other the dataset to join with
	 * @param leftColumnName the name of the left key column
	 * @param rigthColumnNamethe name of the right key column
	 * @param how the join type. Must be one of left, right, inner, full.
	 * @returna new dataset
	 */
	public DatasetOpeOld join(DatasetOpeOld other, String leftColumnName, String rigthColumnName, String how) throws IllegalArgumentException, IllegalAccessException {
		DatasetOpeOld joinedDataset = Combining.join(this, other, leftColumnName, rigthColumnName, how);
		return joinedDataset;
	}

	/**
	 * Group the dataset by the given columnn. Multiple aggregation can be performed to numeric fields. 
	 * @param columnName the name of column usde for grouping.
	 * @param aggType the types of aggreations to perform. Must be one of min, max, sum, count, mean, std.
	 * @return a new dataset
	 */
	public DatasetOpeOld groupBy(String columnName, String... aggType) {
		DatasetField field = getField(columnName);
		return groupBy(field, aggType);
	}

	/**
	 * Group the dataset by the given columnn. Multiple aggregation can be performed to numeric fields. 
	 * @param columnName the name of column usde for grouping.
	 * @param aggType the types of aggreations to perform. Must be one of min, max, sum, count, mean, std.
	 * @return a new dataset
	 */
	private DatasetOpeOld groupBy(DatasetField fieldBy, String... aggType) {
		//Get groups
		DatasetOpeOld groupDataset = new DatasetOpeOld(Arrays.asList(fieldBy));
		HashMap<Object, DatasetOpeOld> map = group(fieldBy);
		List<String> aggTypes = Arrays.asList(aggType);
		
		//if count is required, add the field to the dataset
		DatasetField countField = null;
		
		if (aggTypes.contains("count")) {
			countField = new DatasetField(fieldBy.getName() + "Count", long.class);
			groupDataset.columns.add(countField);
		}
		
		//for each unique key, create a new row
		for (Object key : map.keySet()) {
			DatasetOpeOld dataset = map.get(key);
			Row row = new Row();
		
			//Fills the row with the aggregation of numeric columns
			for (DatasetField field : columns) {
				
				if (fieldBy.equals(field)) {
					row.put(field, key);
				} else if (numericFields.contains(field.getType())) {

					if (aggTypes.contains("mean")) {
						//Initiate a DatasetField and its name
						String fieldName = field.getName() + "Mean";
						DatasetField meanField = null;
						try {
							// if the meanField is already in the dataset
							meanField = groupDataset.getField(fieldName); 
						} catch (IllegalArgumentException e) {
							meanField = new DatasetField(fieldName, Double.class);
							groupDataset.columns.add(meanField);
						}
						row.put(meanField, dataset.mean(field.getName()));
					}
					
					if (aggTypes.contains("sum")) {
						//Initiate a DatasetField and its name
						String fieldName = field.getName() + "Sum";
						DatasetField sumField = null;
						try {
							// if the sumField is already in the dataset
							sumField = groupDataset.getField(fieldName); 
						} catch (IllegalArgumentException e) {
							sumField = new DatasetField(fieldName, Double.class);
							groupDataset.columns.add(sumField);
						}
						row.put(sumField, dataset.sum(field.getName()));
					}
					
					if (aggTypes.contains("count")) {
						row.put(countField, dataset.count());
					}
					
					if (aggTypes.contains("std")) {
						//Initiate a DatasetField and its name
						String fieldName = field.getName() + "Std";
						DatasetField stdField = null;
						try {
							// if the stdField is already in the dataset
							stdField = groupDataset.getField(fieldName); 
						} catch (IllegalArgumentException e) {
							stdField = new DatasetField(fieldName, Double.class);
							groupDataset.columns.add(stdField);
						}
						row.put(stdField, dataset.std(field.getName()));
					}
					
					if (aggTypes.contains("min")) {
						//Initiate a DatasetField and its name
						String fieldName = field.getName() + "Min";
						DatasetField minField = null;
						try {
							// if the minField is already in the dataset
							minField = groupDataset.getField(fieldName); 
						} catch (IllegalArgumentException e) {
							minField = new DatasetField(fieldName, Double.class);
							groupDataset.columns.add(minField);
						}
						row.put(minField, dataset.min(field.getName()).getAsDouble());
					}
					
					if (aggTypes.contains("max")) {
						//Initiate a DatasetField and its name
						String fieldName = field.getName() + "Max";
						DatasetField maxField = null;
						try {
							// if the maxField is already in the dataset
							maxField = groupDataset.getField(fieldName); 
						} catch (IllegalArgumentException e) {
							maxField = new DatasetField(fieldName, Double.class);
							groupDataset.columns.add(maxField);
						}
						row.put(maxField, dataset.max(field.getName()).getAsDouble());
					}
				}
			}
			groupDataset.append(row);
			
		}
			
		return groupDataset;
	}
	
	/**
	 * Get a map of datasets that share common column value. The map key corresponds to this value and the map value t the dataset.
	 * @param fieldBy the field of the column used for grouping the dataset. The resulting map keys correpond to all the distinct value of this column.
	 * @return a map
	 */
	public HashMap<Object, DatasetOpeOld> group(DatasetField fieldBy) {
		//Get groups
		HashMap<Object, DatasetOpeOld> map = new HashMap<Object, DatasetOpeOld>();
		for (Row item : data) {
			Object itemField = item.get(fieldBy);
			if (!map.containsKey(itemField)) {
				List<Row> newData = new ArrayList<Row>();
				newData.add(item);
				DatasetOpeOld dataset = new DatasetOpeOld(columns, newData);
				map.put(itemField, dataset);
			} else {
				map.get(itemField).append(item);
			}
		}
		return map;
	}
	
	/**
	 * Apply a function to all the element of a given column
	 * @param colNames the name of the column
	 * @param function the function argument
	 * @param inputClass the class of the input. Must match the current DatasetField type.
	 * @param outputClass the class of the input. Will be the new type of the DatasetField.
	 * @return this dataset
	 */
	@SuppressWarnings("unchecked")
	public <I, O> DatasetOpeOld apply(String colName, Function<I, O> function, Class<I> inputClass, Class<O> outputClass) {
		DatasetField field = getField(colName);
		if (! field.getType().equals(inputClass)) {
			throw new IllegalArgumentException("The input type " + inputClass.getCanonicalName() + " does not match the " + colName + " type.");
		}
		field.setType(outputClass);
		for (Row row : data) {
			row.replace(field, function.apply((I) row.get(field)));
		}
		return this;
	}

	/**
	 * Remove a row using its index
	 * @param index the index of the row to remove
	 * @return this dataset
	 */
	public DatasetOpeOld remove(Integer index) {
		data.remove((int) index);
		return this;
	}
	
	/**
	 * Remove several rows using their index
	 * @param indexes the index of the rows to remove
	 * @return this dataset
	 */
	public DatasetOpeOld remove(Integer... indexes) {
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
	public DatasetOpeOld remove(Row item) {
		data.remove(item);
		return this;
	}
	
	/**
	 * Remove several rows using their values
	 * @param items the value of the rows to remove
	 * @return this dataset
	 */
	public DatasetOpeOld remove(Row... items) {
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
	
	/**
	 * Check if a value is contained in the dataset
	 * @param row 
	 * @return True if the dataset contains the row
	 */
	public Boolean contains(Row row) {
		return data.contains(row);
	}
	
	/**
	 * Check if the dataset is empty
	 * @return True if empty
	 */
	public Boolean isEmpty() {
		return data.isEmpty();
	}
	
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
	
	/**
	 * Get the data of a given column from the dataset.
	 * @param field the DatasetField associated to the column to extract
	 * @return a list of mono columned rows
	 */
	private List<Row> getColumn(DatasetField field) {
		//get the field from all items
		List<Row> values = new ArrayList<Row>();
		List<DatasetField> fields = new ArrayList<DatasetField>();
		fields.add(field);
		for (Row row: data) {
			values.add(row.get(fields));
		}
		return values;
	}
	
	/**
	 * Get the data of multiple given columns from the dataset.
	 * @param field the DatasetField associated to the column to extract
	 * @return a list of multi columned rows
	 */
	private List<Row> getColumns(List<DatasetField> fields) {
		List<Row> values = new ArrayList<Row>();
		for (Row row: data) {
			values.add(row.get(fields));
		}
		return values;
	}

	/**
	 * Get the data of columns from the dataset excepting given ones.
	 * @param fieldNames
	 * @return
	 */
	private List<DatasetField> filterFields(String... fieldNames) {
		List<String> fieldNamesList = Arrays.asList(fieldNames);
		List<DatasetField> filteredList = columns.stream()
		        .filter(item -> !fieldNamesList.contains(item.getName()))
		        .collect(Collectors.toList());
		return filteredList;
	}
	
	/**
	 * Get the DatasetField associated to the given fieldName
	 * @param fieldName the name of the field to get
	 * @return a DatasetField
	 */
	public DatasetField getField(String fieldName) {
		DatasetField resField = null;
		for (DatasetField field : columns) {
			if (field.getName().equals(fieldName)) {
				resField = field;
				break;
			}
		}
		if (resField == null) {
			throw new IllegalArgumentException(fieldName + " not found in the columns of the dataset.");
		}
		return resField;
	}
	
	/**
	 * Get several DatasetField associated to given fieldNames
	 * @param fieldName the name of the field to get
	 * @return a DatasetField
	 */
	private List<DatasetField> getFields(String... fieldNames) {
		List<String> fieldNamesList = Arrays.asList(fieldNames);
		List<DatasetField> fieldList = new ArrayList<DatasetField>();
		for (DatasetField field : columns) {
			if (fieldNamesList.contains(field.getName())) {
				fieldList.add(field);
			}
		}
		return fieldList;
	}
	
	@Override
	public Iterator<Row> iterator() {
		return data.iterator();
	}
}
