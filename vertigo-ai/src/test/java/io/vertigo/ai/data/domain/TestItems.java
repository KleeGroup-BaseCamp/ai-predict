package io.vertigo.ai.data.domain;

import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

public class TestItems implements KeyConcept {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	@Override
	public UID<?> getUID() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
