package io.vertigo.ai.data.domain.boston;

import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.ai.data.domain.TestDatabase;
import io.vertigo.datamodel.structure.model.UID;

public class BostonRegressionDatabase extends TestDatabase<BostonRegressionItem>{
	
	private final List<BostonRegressionItem> items;
	
	public BostonRegressionDatabase() {
		items = List.of(
				createItem(Long.valueOf(1), 6.575),
				createItem(Long.valueOf(2), 6.421),
				createItem(Long.valueOf(3), 7.185),
				createItem(Long.valueOf(4), 6.998));
	}
	
	private static BostonRegressionItem createItem(final Long Id, final Double rm) {
		final BostonRegressionItem item = new BostonRegressionItem();
		item.setId(Id);
		item.setRm(rm);
		//-----
		return item;
	}
	
	public long size() {
		return items.size();
	}
	
	public List<BostonRegressionItem> getAllItems() {
		return items;
	}
	
	public List<Long> getAllIds() {
		return items.stream().map(BostonRegressionItem::getId).collect(Collectors.toList());
	}
	
	public List<UID<BostonRegressionItem>> getAllUIDs(){
		return items.stream().map(BostonRegressionItem::getUID).collect(Collectors.toList());
	}
}
