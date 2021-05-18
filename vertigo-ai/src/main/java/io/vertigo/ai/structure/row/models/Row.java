package io.vertigo.ai.structure.row.models;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Objet d'échange avec le dataset.
 */
public class Row implements Serializable, DtObject {

	private static final long serialVersionUID = 1L;

	private HashMap<Field, Object> map;
	private UID<?> uid = null;
	private RowDefinition rowDefinition = null;
    
	public Row(HashMap<Field, Object> item) {
		map = item;
	}
	
	public Row() {
		this.map = new HashMap<Field, Object>();
	}
	
	public Row(HashMap<Field, Object> item, UID<?> uid, RowDefinition rowDefinition) {
		map = item;
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	public Row(UID<?> uid, RowDefinition rowDefinition) {
		this.map = new HashMap<Field, Object>();
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	public <I> Row(I item, UID<?> uid, RowDefinition rowDefinition) {
		this(item);
		this.uid = uid;
		this.rowDefinition = rowDefinition;
	}
	
	public <I> Row(I item) {
		if (Row.class.isInstance(item)) {
			Row row = (Row) item;
			map = row.collect();
		} else {
			Class<?> clazz = item.getClass();
			Field[] fields = clazz.getDeclaredFields();
			HashMap<Field, Object> mapItem = new HashMap<Field, Object>();
			for (Field field : fields) {
				if (field.getName() != "this$0") {
					field.setAccessible(true);
					try {
						mapItem.put(field, field.get(item));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						mapItem.put(field, null);
					}
				}
			}
			map = mapItem;
		}
	}
	
	public RowDefinition getDefinition() {
		return rowDefinition;
	}
	
	public UID<?> getUID() {
		return uid;
	}
	
	public HashMap<Field, Object> collect() {
		return map;
	}
	
	public Object get(Field field) {
		return map.get(field);
	}
	
	public Object get(String fieldName) {
		List<Field> fields = map.keySet().stream().filter(p -> p.getName()==fieldName).collect(Collectors.toList());
		return map.get(fields.get(0));
	}
	
	public Object put(Field key, Object value) {
		return map.put(key, value);
	}
	
	public void putAll(HashMap<Field, Object> item) {
		map.putAll(item);
	}
	
	public void putAll(Row item) {
		HashMap<Field, Object> other = item.collect();
		map.putAll(other);
	}
	
	public Row join(Row item) {
		HashMap<Field, Object> other = item.collect();
		HashMap<Field, Object> mapCopy = new HashMap<Field, Object>(map);
		other.putAll(mapCopy);
		return new Row(other);
		
	}

	public Object remove(Field key) {
		return map.remove(key);
	}
	
	public boolean remove(Field key, Object value) {
		return map.remove(key, value);
	}
	
	public Set<Field> keySet() {
		return map.keySet();
	}
	
	public Object merge(Field key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> function) {
		return map.merge(key, value, function);
	}
	
	public void forEach(BiConsumer<? super Field,? super Object> consumer) {
		map.forEach(consumer);
	}
	
	public Object replace(Field field, Object value) {
		return map.replace(field, value);
	}
	
	public String toString() {
		HashMap<String, Object> serialized = new HashMap<String, Object>();
		map.keySet().forEach(item -> serialized.put(item.getName(), map.get(item)));
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		map.keySet().forEach(item -> builder.append("\""+item.getName()+"\":"+map.get(item)+","));
		builder.deleteCharAt(builder.length()-1);
		builder.append("}");
		return builder.toString();
	}
	
	public Row get(List<Field> fields) {
		HashMap<Field, Object> newMap = new HashMap<Field, Object>();
		for (Field field: fields) {
			newMap.put(field, get(field));
		}
		return new Row(newMap);
	}

	public int compareTo(Row other, Field field) {
		return get(field).toString().compareTo(other.get(field).toString());
	}
	
	public HashMap<String, Field> fields() {
		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		Set<Field> fields = map.keySet();
		fields.forEach(field -> fieldMap.put(field.getName(), field));
		return fieldMap;
	}
	
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
		HashMap<String, Field> rowFields = fields();
		for (Field field : clazzFields) {
			String key = field.getName();
			if (rowFields.containsKey(key)) {
				rowFields.get(key).set(newObject, get(field));
			}
					
		}
		return newObject;
	}
}