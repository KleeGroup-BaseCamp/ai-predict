package io.vertigo.ai.example.iris.predict;

import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

public final class IrisPredict implements DtObject {

	private static final long serialVersionUID = 1L;

	private Long id;
	private java.math.BigDecimal sepalLength;
	private java.math.BigDecimal sepalWidth;
	private java.math.BigDecimal petalLength;
	private java.math.BigDecimal petalWidth;

	
	
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
	
	public java.math.BigDecimal getSepalLength() {
		return sepalLength;
	}

	public void setSepalLength(final java.math.BigDecimal sepalLength) {
		this.sepalLength = sepalLength;
	}
	
	public java.math.BigDecimal getSepalWidth() {
		return sepalWidth;
	}

	public void setSepalWidth(final java.math.BigDecimal sepalWidth) {
		this.sepalWidth = sepalWidth;
	}
	

	public java.math.BigDecimal getPetalLength() {
		return petalLength;
	}

	public void setPetalLength(final java.math.BigDecimal petalLength) {
		this.petalLength = petalLength;
	}
	
	public java.math.BigDecimal getPetalWidth() {
		return petalWidth;
	}

	public void setPetalWidth(final java.math.BigDecimal petalWidth) {
		this.petalWidth = petalWidth;
	}
	
	public String toString() {
		StringBuilder serialized = new StringBuilder();
		serialized.append("{");
		serialized.append("\"sepal_length\":"+getSepalLength().toString()+",");
		serialized.append("\"sepal_width\":"+getSepalWidth().toString()+",");
		serialized.append("\"petal_length\":"+getPetalLength().toString()+",");
		serialized.append("\"petal_width\":"+getPetalWidth().toString()+"}");
		return serialized.toString();
	}
}
