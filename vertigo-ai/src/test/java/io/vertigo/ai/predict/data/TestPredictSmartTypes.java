package io.vertigo.ai.predict.data;

import io.vertigo.basics.formatter.FormatterDefault;


import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;

public enum TestPredictSmartTypes {

	@SmartTypeDefinition(Long.class)
	@Formatter(clazz = FormatterDefault.class)
	Identifiant,
	
	@SmartTypeDefinition(Double.class)
	@Formatter(clazz = FormatterDefault.class)
	Double,
	
	@SmartTypeDefinition(Double.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionNumeric,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionLabel,

}
