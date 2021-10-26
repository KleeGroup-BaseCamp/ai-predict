package io.vertigo.ai.example.iris.data;

import java.math.BigDecimal;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeProperty;

public enum IrisSmartTypes {

	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "INTEGER")
	Integer,
	
	@SmartTypeDefinition(Double.class)
	@Formatter(clazz = FormatterDefault.class)
	Decimal,
	
	@SmartTypeDefinition(BigDecimal.class)
	@Formatter(clazz = FormatterDefault.class)
	ExactDecimal,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(100)")
	Label,
	
	@SmartTypeDefinition(Long.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Id,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(100)")
	Code,

}
