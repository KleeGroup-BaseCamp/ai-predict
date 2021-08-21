package io.vertigo.ai.predict.data.domain.boston;

import io.vertigo.ai.predict.data.domain.TestItems;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;

public class BostonRegressionItem extends TestItems {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Double rm;
	
	@Override
	public UID<BostonRegressionItem> getUID() {
		return UID.of(this);
	}

	@Field(smartType = "STyIdentifiant", type="ID", cardinality = Cardinality.ONE, label = "Identifiant")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "RM")
	public Double getRm() {
		return rm;
	}

	public void setRm(Double rM) {
		rm = rM;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("{\"RM\":");
		res.append(rm);
		res.append("}");
		return res.toString();
	}

}
