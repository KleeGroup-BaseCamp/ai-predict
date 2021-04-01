package io.vertigo.ai.data.domain.boston;

import io.vertigo.ai.data.domain.TestItems;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;

public class BostonItem extends TestItems {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Double crim;
	private Double zn;
	private Double indus;
	private Double chas;
	private Double nox;
	private Double rm;
	private Double age;
	private Double dis;
	private Double rad;
	private Double tax;
	private Double ptRatio;
	private Double b;
	private Double lstat;
	
	@Override
	public UID<BostonItem> getUID() {
		return UID.of(this);
	}

	@Field(smartType = "STyIdentifiant", type="ID", cardinality = Cardinality.ONE, label = "Identifiant")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "CRIM")
	public Double getCrim() {
		return crim;
	}

	public void setCrim(Double cRIM) {
		crim = cRIM;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "ZN")
	public Double getZn() {
		return zn;
	}

	public void setZn(Double zN) {
		zn = zN;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "INDUS")
	public Double getIndus() {
		return indus;
	}

	public void setIndus(Double iNDUS) {
		indus = iNDUS;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "CHAS")
	public Double getChas() {
		return chas;
	}

	public void setChas(Double cHAS) {
		chas = cHAS;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "NOX")
	public Double getNox() {
		return nox;
	}

	public void setNox(Double nOX) {
		nox = nOX;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "RM")
	public Double getRm() {
		return rm;
	}

	public void setRm(Double rM) {
		rm = rM;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "AGE")
	public Double getAge() {
		return age;
	}

	public void setAge(Double aGE) {
		age = aGE;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "DIS")
	public Double getDis() {
		return dis;
	}

	public void setDis(Double dIS) {
		dis = dIS;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "TAX")
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tAX) {
		tax = tAX;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "RAD")
	public Double getRad() {
		return rad;
	}

	public void setRad(Double rAD) {
		rad = rAD;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "PTRATIO")
	public Double getPtRatio() {
		return ptRatio;
	}

	public void setPtRatio(Double pTRATIO) {
		ptRatio = pTRATIO;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "B")
	public Double getB() {
		return b;
	}

	public void setB(Double b) {
		this.b = b;
	}

	@Field(smartType = "STyDouble", cardinality = Cardinality.ONE, label = "LSTAT")
	public Double getLstat() {
		return lstat;
	}

	public void setLstat(Double lSTAT) {
		lstat = lSTAT;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("{\"CRIM\":");
		res.append(crim);
		res.append(", ");
		res.append("\"ZN\":");
		res.append(zn);
		res.append(", ");
		res.append("\"INDUS\":");
		res.append(indus);
		res.append(", ");
		res.append("\"CHAS\":");
		res.append(chas);
		res.append(", ");
		res.append("\"NOX\":");
		res.append(nox);
		res.append(", ");
		res.append("\"RM\":");
		res.append(rm);
		res.append(", ");
		res.append("\"AGE\":");
		res.append(age);
		res.append(", ");
		res.append("\"DIS\":");
		res.append(dis);
		res.append(", ");
		res.append("\"RAD\":");
		res.append(rad);
		res.append(", ");
		res.append("\"TAX\":");
		res.append(tax);
		res.append(", ");
		res.append("\"PTRATIO\":");
		res.append(ptRatio);
		res.append(", ");
		res.append("\"B\":");
		res.append(b);
		res.append(", ");
		res.append("\"LSTAT\":");
		res.append(lstat);
		res.append("}");
		return res.toString();
	}

}
