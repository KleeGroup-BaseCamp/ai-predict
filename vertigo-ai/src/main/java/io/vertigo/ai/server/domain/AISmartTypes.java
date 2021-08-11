package io.vertigo.ai.server.domain;

import java.math.BigDecimal;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;

public enum AISmartTypes {

	//TODO: Uniformiser le nommage des SmartTypes et des définitions d'objets
	
	@SmartTypeDefinition(Long.class)
	@Formatter(clazz = FormatterDefault.class)
	Identifiant,
	
	@SmartTypeDefinition(BigDecimal.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionNumeric,
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionLabel,

	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionInteger,

	@SmartTypeDefinition(Boolean.class)
	@Formatter(clazz = FormatterDefault.class)
	PredictionBoolean,

	//Doublon avec PredictionLabl
	/*@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	Label,*/

	//Doublon avec PredictionNumeric
	//TODO: Float -> BigDecimal
	@SmartTypeDefinition(BigDecimal.class)
	@Formatter(clazz = FormatterDefault.class)
	Float,

	//Doublon avec PredictionInteger
	/*@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	Integer,*/
	
	@SmartTypeDefinition(String.class)
	@Formatter(clazz = FormatterDefault.class)
	StringAIResponse,
	
	@SmartTypeDefinition(Boolean.class)
	@Formatter(clazz = FormatterDefault.class)
	BooleanAIResponse,
	
	@SmartTypeDefinition(BigDecimal.class)
	@Formatter(clazz = FormatterDefault.class)
	DecimalAIResponse,
	
	//Doublon avec PredictionInteger
	@SmartTypeDefinition(Integer.class)
	@Formatter(clazz = FormatterDefault.class)
	IntegerAIResponse,
}
