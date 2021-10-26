package io.vertigo.ai.example.heroes.data;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeProperty;

public enum HeroesSmartTypes {

	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "NUMERIC")
	Int,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	@SmartTypeProperty(property = "storeType", value = "VARCHAR(100)")
	String
}
