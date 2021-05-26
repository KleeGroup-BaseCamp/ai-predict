package io.vertigo.ai.example.data;

import io.vertigo.basics.formatter.FormatterDefault;


import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;

public enum SmartTypes {

	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	Integer,
	
	@SmartTypeDefinition(Double.class)
	@Formatter(clazz = FormatterDefault.class)
	Decimal,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	Label,

}