package io.vertigo.ai.data.domain.iris;

import java.util.List;

import io.vertigo.ai.data.domain.TestDatabase;

public final class IrisDatabase extends TestDatabase<IrisItem>{
	
	private final List<IrisItem> items;
	
	public IrisDatabase() {
		items = List.of(
				createItem(Long.valueOf(1), 5.1,3.5,1.4,0.2),
				createItem(Long.valueOf(2), 4.9,3.0,1.4,0.2),
				createItem(Long.valueOf(3), 4.7,3.2,1.3,0.2),
				createItem(Long.valueOf(4), 4.6,3.1,1.5,0.2),
				createItem(Long.valueOf(5), 6.9,3.2,5.7,2.3),
				createItem(Long.valueOf(6), 6.4,3.2,4.5,1.5));
	}
	
	private static IrisItem createItem(final Long Id, final Double sepalLength, final Double sepalWidth, final Double petalLength, final Double petalWidth) {
		final IrisItem item = new IrisItem();
		item.setId(Id);
		item.setSepalLength(sepalLength);
		item.setSepalWidth(sepalWidth);
		item.setPetalLength(petalLength);
		item.setPetalWidth(petalWidth);
		//-----
		return item;
	}
	
	public long size() {
		return items.size();
	}
	
	public List<IrisItem> getAllItems() {
		return items;
	}

}
