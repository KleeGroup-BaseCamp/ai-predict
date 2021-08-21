package io.vertigo.ai.structure.row.models;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.vertigo.ai.structure.dataset.models.DatasetField;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Objet d'échange avec le dataset.
 */
public class Row implements DtObject {

	private static final long serialVersionUID = 1L;

	private Map<DatasetField, Object> map;
	private UID<?> uid = null;
	private RowDefinition rowDefinition = null;
    
	/**
	 * Construct a row based on an HashMap
	 */
	public Row(Map<DatasetField, Object> item) { 
		map = item;
	}
	
	/**
	 * Construct an empty row
	 */
	public Row() {
		this.map = new HashMap<DatasetField, Object>();
	}

	/**
	 * Construct a row based on an HashMap with an UID and a RowDefinition for Vertigo Applications
	 */
	public Row(Map<DatasetField, Object> item, UID<?> uid, RowDefinition rowDefinition) {
		map = item;
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	/**
	 * Construct an empty row with an UID and a RowDefinition for Vertigo Applications
	 */
	public Row(UID<?> uid, RowDefinition rowDefinition) {
		this.map = new HashMap<DatasetField, Object>();
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	/**
	 * Build a row based on an object with an UID and a RowDefinition for Vertigo Applications
	 */
	public <I> Row(I item, UID<?> uid, RowDefinition rowDefinition) {
		this(item);
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	/**
	 * Build a row based on an object
	 * TODO : use the same DatasetFields for the whole dataset instead of building new ones for each row.
	 */
	public <I> Row(I item) {
		// if the object is a row, copy it
		if (Row.class.isInstance(item)) {
			Row row = (Row) item;
			map = row.collect();
		} else {
			// else get the item class
			Class<?> clazz = item.getClass();
			//get its fields
			Field[] fields = clazz.getDeclaredFields();
			//initiate the map
			Map<DatasetField, Object> mapItem = new HashMap<DatasetField, Object>();
			// fr each field, set it accessible and add it to the map
			for (Field field : fields) {
				if (field.getName() != "this$0") {
					field.setAccessible(true);
					try {
						mapItem.put(new DatasetField(field), field.get(item));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						mapItem.put(new DatasetField(field), null);
					}
				}
			}
			map = mapItem;
		}
	}
	
	/**
	 * Get the RowDefinition
	 */
	public RowDefinition getDefinition() {
		return rowDefinition;
	}
	
	/**
	 * get this row UID
	 */
	public UID<?> getUID() {
		return uid;
	}
	
	/**
	 * Rename a DatasetField
	 * @param currentField 
	 * @param newField
	 * @return
	 */
	public Row rename(DatasetField currentField, DatasetField newField) {
		Object o = remove(currentField);
		map.put(newField, o);
		
		return this;
	}
	
	/**
	 * Collect the HashMap associated to the row
	 * @return a map of all item in the row
	 */
	public Map<DatasetField, Object> collect() {
		return map;
	}
	
	/**
	 * Get the element linked to the field in the row
	 * @param field the DatasetField of the element
	 * @return an object
	 */
	public Object get(DatasetField field) {
		return get(field.getName());
	}
	
	/**
	 * Get the element linked to the field in the row
	 * @param field with the same name as the DatasetField the element is linked to
	 * @return an object
	 */
	public Object get(Field field) {
		return get(field.getName());
	}
	
	/**
	 * Get the element linked to the field in the row
	 * @param fieldName the name of the DatasetField the element is linked to
	 * @return an object
	 */
	public Object get(String fieldName) {
		List<DatasetField> fields = map.keySet().stream().filter(p -> p.getName().equals(fieldName)).collect(Collectors.toList());
		return map.get(fields.get(0));
	}
	
	/**
	 * Associates the specified value with the specified key in this row.If the map previously contained a mapping for the key, the oldvalue is replaced.
	 * @param key the DatasetField with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return
	 */
	public Object put(DatasetField key, Object value) {
		return map.put(key, value);
	}
	
	/**
	 * Copies all of the mappings from the specified map to this row.These mappings will replace any mappings that this row had forany of the keys currently in the specified map.
	 * @param item mappings to be stored in this map
	 */
	public void putAll(Map<DatasetField, Object> item) {
		map.putAll(item);
	}
	
	/**
	 * Copies all of the element from the specified row to this row.These elements will replace any elements that this row had forany of the keys currently in the specified row.
	 * @param item rows to be stored in this map
	 */
	public void putAll(Row item) {
		Map<DatasetField, Object> other = item.collect();
		map.putAll(other);
	}
	
	/**
	 * Joins two row
	 * @param item the row to join this row with
	 * @return a new row
	 */
	public Row join(Row item) {
		Map<DatasetField, Object> other = item.collect();
		Map<DatasetField, Object> mapCopy = new HashMap<DatasetField, Object>(map);
		other.putAll(mapCopy);
		return new Row(other);
		
	}

	/**
	 * Removes the mapping for the specified DatasetField from this row if present.
	 * @param key DatasetField whose mapping is to be removed from the map
	 * @return the previous value associated with DatasetField, or null if there was no mapping for DatasetField.(A null return can also indicate that the mappreviously associated null with DatasetField.)
	 */
	public Object remove(DatasetField key) {
		return map.remove(key);
	}
	
	/**
	 * Removes the entry for the specified DatasetField only if it is currently mapped to the specified value.
	 * @param key DatasetField with which the specified value is associated
	 * @param value value expected to be associated with the specified DatasetField
	 * @return true if the value was removed
	 */
	public boolean remove(DatasetField key, Object value) {
		return map.remove(key, value);
	}
	
	/**
	 * Returns a Set view of the keys contained in this row.The set is backed by the row, so changes to the row are
	 * reflected in the set, and vice-versa. If the row is modified
	 * while an iteration over the set is in progress (except throughthe iterator's own remove operation), the results ofthe iteration are undefined.
	 * The set supports element removal,which removes the corresponding mapping from the map, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear
	 * operations. It does not support the add or addAlloperations.
	 * @return a set view of the DatasetFields contained in this row
	 */
	public Set<DatasetField> keySet() {
		return map.keySet();
	}
	
	/**
	 * If the specified DatasetField is not already associated with a value or is
	 * associated with null, associates it with the given non-null value.
	 * Otherwise, replaces the associated value with the results of the given
	 * remapping function, or removes if the result is null. 
	 * @param key  DatasetField with which the resulting value is to be associated
	 * @param value  the non-null value to be merged with the existing valueas sociated with the DatasetField or, if no existing value or a null value
	 * is associated with the DatasetField, to be associated with the DatasetField
	 * @param function the remapping function to recompute a value ifpresent
	 * @return the new value associated with the specified key, or null if novalue is associated with the key
	 */
	public Object merge(DatasetField key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> function) {
		return map.merge(key, value, function);
	}
	
	/**
	 * Performs the given action for each entry in this row until all entries
	 * have been processed or the action throws an exception. Unless
	 * otherwise specified by the implementing class, actions are performed in
	 * the order of entry set iteration (if an iteration order is specified.)
	 * Exceptions thrown by the action are relayed to the caller.
	 * @param consumer The action to be performed for each entry
	 */
	public void forEach(BiConsumer<? super DatasetField,? super Object> consumer) {
		map.forEach(consumer);
	}
	
	/**
	 * Replaces the entry for the specified DatasetField only if it is currently mapped to some value.
	 * @param field DatasetField with which the specified value is associated
	 * @param value value to be associated with the specified DatasetField
	 */
	public void replace(DatasetField field, Object value) {
		map.replace(fields().get(field.getName()), value);
	}
	
	/**
	 * Returns a string representation of this row.
	 */
	public String toString() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		map.keySet().forEach(item -> serialized.put(item.getName(), map.get(item)));
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		map.keySet().forEach(item -> builder.append("\""+item.getName()+"\":"+map.get(item)+","));
		builder.deleteCharAt(builder.length()-1);
		builder.append("}");
		return builder.toString();
	}
	
	
	/**
	 * Get multiple values associated to specified DatasetFields from this row.
	 * @param fields DatasetField to et for this row.
	 * @return a new row with the specified DatasetField
	 */
	public Row get(List<DatasetField> fields) {
		Map<DatasetField, Object> newMap = new HashMap<DatasetField, Object>();
		for (DatasetField field: fields) {
			newMap.put(field, get(field));
		}
		return new Row(newMap);
	}

	/**
	 * Compares two items by converting them to string. The two strings are compared lexicographically.
	 * @param other Row with the element to compare with
	 * @param field DatasetField of the elements to compare
	 * @return
	 */
	public int compareTo(Row other, DatasetField field) {
		return get(field).toString().compareTo(other.get(field).toString());
	}
	
	private Map<String, DatasetField> fields() {
		Map<String, DatasetField> fieldMap = new HashMap<String, DatasetField>();
		Set<DatasetField> fields = map.keySet();
		fields.forEach(field -> fieldMap.put(field.getName(), field));
		return fieldMap;
	}
	
	/**
	 * Extract a specified object from the Row
	 * @param clazz Class of the object
	 * @return a object
	 */
	public <I> I toObject(Class<I> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		Constructor<?>[] ctors = clazz.getDeclaredConstructors();
		Constructor<?> ctor = ctors[0];
		for (Constructor<?> ct: ctors) {
		    ctor = ct;
		    if (ctor.getGenericParameterTypes().length == 0) {
		    	break;
		    }
		}
		ctor.setAccessible(true);
		@SuppressWarnings("unchecked")
		I newObject = (I) ctor.newInstance();
		
		List<Field> clazzFields = Arrays.asList(clazz.getDeclaredFields());
		Map<String, DatasetField> rowFields = fields();
		for (Field field : clazzFields) {
			String key = field.getName();
			if (rowFields.containsKey(key)) {
				field.setAccessible(true);
				field.set(newObject, get(field));
			}
					
		}
		return newObject;
	}
}