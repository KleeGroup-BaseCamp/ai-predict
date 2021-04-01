package io.vertigo.ai.data.domain.boston;

import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.ai.data.domain.TestDatabase;
import io.vertigo.datamodel.structure.model.UID;

public class BostonDatabase extends TestDatabase<BostonItem>{
	
	private final List<BostonItem> items;
	
	public BostonDatabase() {
		items = List.of(
				createItem(Long.valueOf(1), 0.00632, 18.0, 2.31, 0.0, 0.538, 6.575, 65.2, 4.0900, 1.0, 296.0, 15.3, 396.90, 4.98),
				createItem(Long.valueOf(2), 0.02731, 0.0, 7.07, 0.0, 0.469, 6.421, 78.9, 4.9671, 2.0, 242.0, 17.8, 396.90, 9.14),
				createItem(Long.valueOf(3), 0.02729, 0.0, 7.07, 0.0, 0.469, 7.185, 61.1, 4.9671, 2.0, 242.0, 17.8, 392.83, 4.03),
				createItem(Long.valueOf(4), 0.03237, 0.0, 2.18, 0.0, 0.458, 6.998, 45.8, 6.0622, 3.0, 222.0, 18.7, 394.63, 2.94));
	}
	
	private static BostonItem createItem(final Long Id,
			final Double crim,
			final Double zn,
			final Double indus,
			final Double chas,
			final Double nox,
			final Double rm,
			final Double age,
			final Double dis,
			final Double rad,
			final Double tax,
			final Double ptratio,
			final Double b,
			final Double lstat) {
		final BostonItem item = new BostonItem();
		item.setId(Id);
		item.setCrim(crim);
		item.setZn(zn);
		item.setIndus(indus);
		item.setChas(chas);
		item.setNox(nox);
		item.setRm(rm);
		item.setAge(age);
		item.setDis(dis);
		item.setRad(rad);
		item.setTax(tax);
		item.setPtRatio(ptratio);
		item.setB(b);
		item.setLstat(lstat);
		//-----
		return item;
	}
	
	public long size() {
		return items.size();
	}
	
	public List<BostonItem> getAllItems() {
		return items;
	}
	
	public List<Long> getAllIds() {
		return items.stream().map(BostonItem::getId).collect(Collectors.toList());
	}
	
	public List<UID<BostonItem>> getAllUIDs(){
		return items.stream().map(BostonItem::getUID).collect(Collectors.toList());
	}
}
