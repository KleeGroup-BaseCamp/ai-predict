package io.vertigo.ai.dataset;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.models.Row;

public class TestDataset {

	private List<Faction> factionData;
	private List<Hero> heroData;
	
	@BeforeEach
	public void init() {
		factionData = Arrays.asList(
				new Faction(1, "Rebel"),
				new Faction(2, "Empire"),
				new Faction(3, "Republic")
				);
		
		heroData = Arrays.asList(
				new Hero(1, "Luke", 21),
				new Hero(1, "Han", 19),
				new Hero(3, "Obiwan", 18),
				new Hero(1, "Leia", 23),
				new Hero(3, "Anakin", 22),
				new Hero(4, "Rey", 24)
				);
	}
	
	@Test
	public void testCreate() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Assertions.assertEquals(6, dataset.count());
	}
	
	@Test
	public void testAppendOne() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Hero person = new Hero(7, "Finn", 24);
		dataset.append(person);
		Assertions.assertEquals(7, dataset.count());
	}
	
	@Test
	public void testAppendMultiple() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		List<Hero> persons = Arrays.asList(
				new Hero(7, "Jyn", 24),
				new Hero(8, "Ezra", 18)
				);
		dataset.append(persons);
		Assertions.assertEquals(8, dataset.count());
	}
	
	@Test
	public void testSelectOne() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset names = dataset.select("name");
		Assertions.assertEquals(6, names.count());
		Assertions.assertEquals("Luke", names.get(0, "name"));
	}
	
	@Test
	public void testSelectMultiple() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset selected = dataset.select("name", "age");
		Assertions.assertEquals(6, selected.count());
		Assertions.assertEquals("Luke", selected.get(0, "name"));
		Assertions.assertEquals(23, selected.get(3, "age"));
	}
	
	@Test
	public void testExceptOne() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset selected = dataset.except("age");
		Assertions.assertEquals("Luke", selected.get(0, "name"));
		Assertions.assertEquals(3, selected.get(2, "id"));
	}
	
	@Test
	public void testExceptMultiple() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset selected = dataset.except("id", "age");
		Assertions.assertEquals("Luke", selected.get(0, "name"));
	}
	
	@Test
	public void testGetColumns() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		List<String> columns = Arrays.asList("id", "name", "age");
		Assertions.assertEquals(columns, dataset.columns());
	}
	
	@Test
	public void testRemoveOne() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row droppedPerson = dataset.get(0);
		Row nonDroppedPerson = dataset.get(5);
		dataset.remove(0);
		Assertions.assertEquals(5, dataset.count());
		Assertions.assertTrue(!dataset.contains(droppedPerson));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testRemoveMultiple() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row droppedPerson = dataset.get(0);
		Row nonDroppedPerson = dataset.get(5);
		dataset.remove(0, 1, 2);
		Assertions.assertEquals(3, dataset.count());
		Assertions.assertTrue(!dataset.contains(droppedPerson));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testRemoveOneItem() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row droppedPerson = dataset.get(0);
		Row nonDroppedPerson = dataset.get(5);
		dataset.remove(droppedPerson);
		Assertions.assertEquals(5, dataset.count());
		Assertions.assertTrue(!dataset.contains(droppedPerson));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testRemoveMultipleItem() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row droppedPerson1 = dataset.get(0);
		Row droppedPerson2 = dataset.get(1);
		Row droppedPerson3 = dataset.get(2);
		Row nonDroppedPerson = dataset.get(5);
		dataset.remove(droppedPerson1, droppedPerson2, droppedPerson3);
		Assertions.assertEquals(3, dataset.count());
		Assertions.assertTrue(!dataset.contains(droppedPerson1));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testDropOne() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row personToDrop = dataset.get(0);
		Row nonDroppedPerson = dataset.get(5);
		Row droppedPerson = dataset.drop(0);
		Assertions.assertEquals(5, dataset.count());
		Assertions.assertEquals(droppedPerson, personToDrop);
		Assertions.assertTrue(!dataset.contains(personToDrop));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testDropMultiple() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row personToDrop = dataset.get(0);
		Row nonDroppedPerson = dataset.get(5);
		List<Row> droppedPersons = dataset.drop(0, 1, 2);
		Assertions.assertEquals(3, dataset.count());
		Assertions.assertEquals(droppedPersons.get(0), personToDrop);
		Assertions.assertTrue(!dataset.contains(personToDrop));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testDropOneItem() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row personToDrop = dataset.get(0);
		Hero personToNotDrop = new Hero(10, "Sheev", 70);
		Row rowNotToDrop = new Row(personToNotDrop);
		Row nonDroppedPerson = dataset.get(5);
		Boolean dropped = dataset.drop(personToDrop);
		Boolean undropped = dataset.drop(rowNotToDrop);
		Assertions.assertEquals(5, dataset.count());
		Assertions.assertTrue(!dataset.contains(personToDrop));
		Assertions.assertTrue(dropped);
		Assertions.assertTrue(!undropped);
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testDropMultipleItem() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row droppedPerson1 = dataset.get(0);
		Row droppedPerson2 = dataset.get(1);
		Row droppedPerson3 = dataset.get(2);
		Row nonDroppedPerson = dataset.get(5);
		List<Boolean> dropped = dataset.drop(droppedPerson1, droppedPerson2, droppedPerson3);
		Assertions.assertEquals(3, dataset.count());
		Assertions.assertTrue(!dataset.contains(droppedPerson1));
		Assertions.assertTrue(dropped.get(0));
		Assertions.assertTrue(dataset.contains(nonDroppedPerson));
	}
	
	@Test
	public void testDTypes() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Map<String, Class<?>> dtypes = dataset.dTypes();
		Assertions.assertEquals(String.class, dtypes.get("name"));
	}
	
    public static Predicate<Row> isAgeMoreThan(Integer age) {
        return p -> (int) p.get("age") > age;
    }
    
	@Test
	public void testWhere() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Row isFiltered = dataset.get(0);
		Row isNotFiltered = dataset.get(5);
		Dataset filteredDataset = dataset.filter(isAgeMoreThan(21));
		Assertions.assertEquals(3, filteredDataset.count());
		Assertions.assertTrue(!filteredDataset.contains(isFiltered));
		Assertions.assertTrue(filteredDataset.contains(isNotFiltered));
	}
	
	@Test
	public void testOrderByASC() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset sortedDataset = dataset.orderBy("age", "ASC");
		Assertions.assertEquals("Obiwan", sortedDataset.get(0, "name"));
	}
	
	@Test
	public void testOrderByASCList() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset selected = dataset.select("id", "name");
		Dataset sortedDataset = selected.orderBy("name", "ASC");
		Assertions.assertEquals("Anakin", sortedDataset.get(0, "name"));
	}
	
	@Test
	public void testOrderByDESC() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset sortedDataset = dataset.orderBy("age", "DESC");
		Assertions.assertEquals("Rey", sortedDataset.get(0, "name"));
	}
	
	@Test
	public void testOrderByDESCList() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Dataset selected = dataset.select("id", "name");
		Dataset sortedDataset = selected.orderBy("name", "DESC");
		Assertions.assertEquals("Rey", sortedDataset.get(0, "name"));
	}
	
	@Test
	public void testPerfectSplit() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		List<Dataset> splitDatasets= dataset.split(3);
		Assertions.assertEquals(2, splitDatasets.size());
		splitDatasets.forEach(d -> Assertions.assertEquals(3, d.count()));
	}
	
	@Test
	public void testUnperfectSplit() {
		Dataset dataset = new Dataset(Hero.class, heroData);
		List<Dataset> splitDatasets= dataset.split(4);
		Assertions.assertEquals(2, splitDatasets.size());
		Assertions.assertEquals(4, splitDatasets.get(0).count());
		Assertions.assertEquals(2, splitDatasets.get(1).count());
	}
	
	@Test
	public void testGroupBy() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		Hero doppelganger = new Hero(7, "Obiwan", 22);
		dataset.append(doppelganger);
		Dataset groupDataset = dataset.groupBy("name", "mean").orderBy("name");
		Assertions.assertEquals(6, groupDataset.count());
		Assertions.assertEquals(20.0, groupDataset.get(4, "age"));
	}

	@Test
	public void testToObject() throws IllegalArgumentException, IllegalAccessException {
		Dataset dataset = new Dataset(Hero.class, heroData);
		List<Hero> persons = new ArrayList<Hero>();
		dataset.collect().forEach(row -> {
				try {
					persons.add(row.toObject(Hero.class));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
		});
		
		for (int i=0; i<persons.size(); i++) {
			Hero datasetPerson = persons.get(i);
			Hero expectedPerson = heroData.get(i);
			Assertions.assertEquals(expectedPerson.getName(), datasetPerson.getName());
			Assertions.assertEquals(expectedPerson.getId(), datasetPerson.getId());
			Assertions.assertEquals(expectedPerson.getAge(), datasetPerson.getAge());
		}
	}
	
	@Test
	public void testInnerJoin() throws IllegalArgumentException, IllegalAccessException {
		Dataset personDataset = new Dataset(Hero.class, heroData);
		Dataset factionDataset = new Dataset(Faction.class, factionData);
		Dataset joined = personDataset.join(factionDataset, "id", "id", "inner");
		Assertions.assertEquals("Rebel", joined.get(0, "faction"));
	}
	
	@Test
	public void testLeftJoin() throws IllegalArgumentException, IllegalAccessException {
		Dataset personDataset = new Dataset(Hero.class, heroData);
		Dataset factionDataset = new Dataset(Faction.class, factionData);
		Dataset joined = personDataset.join(factionDataset, "id", "id", "left");
		Assertions.assertEquals("Rebel", joined.get(0, "faction"));
		Assertions.assertEquals(null, joined.get(5, "faction"));
	}
	
	@Test
	public void testRightJoin() throws IllegalArgumentException, IllegalAccessException {
		Dataset personDataset = new Dataset(Hero.class, heroData);
		Dataset factionDataset = new Dataset(Faction.class, factionData);
		Dataset joined = personDataset.join(factionDataset, "id", "id", "right");
		Assertions.assertEquals("Rebel", joined.get(0, "faction"));
		Assertions.assertEquals("Luke", joined.get(0, "name"));
	}
	
	@Test
	public void testOutterJoin() throws IllegalArgumentException, IllegalAccessException {
		Dataset personDataset = new Dataset(Hero.class, heroData);
		Dataset factionDataset = new Dataset(Faction.class, factionData);
		Dataset joined = personDataset.join(factionDataset, "id", "id", "full");
		Assertions.assertEquals(7, joined.count());
		Assertions.assertEquals("Luke", joined.get(0, "name"));
		Assertions.assertEquals("Rey", joined.get(6, "name"));
		Assertions.assertEquals("Empire", joined.get(3, "faction"));
	}
}
