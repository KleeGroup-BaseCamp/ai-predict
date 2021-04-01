package io.vertigo.ai.data.domain;

import java.util.List;

public abstract class TestDatabase<T extends TestItems> {

	private final List<T> items;
	
	public TestDatabase() {
		items = null;
	}
	
	public long size() {
		return items.size();
	}
	
	public List<T> getAllItems() {
		return items;
	}
}
