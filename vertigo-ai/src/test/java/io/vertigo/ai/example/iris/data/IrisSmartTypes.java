package io.vertigo.ai.example.iris.data;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;

public enum IrisSmartTypes {

	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	Integer,
	
	@SmartTypeDefinition(Double.class)
	@Formatter(clazz = FormatterDefault.class)
	Decimal,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	Label,
	
	@SmartTypeDefinition(Long.class)
	@Formatter(clazz = FormatterDefault.class)
	Id,

}
