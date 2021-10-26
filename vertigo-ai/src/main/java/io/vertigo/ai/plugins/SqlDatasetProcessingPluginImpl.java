package io.vertigo.ai.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.impl.structure.dataset.DatasetImpl;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.AgregatorType;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.ai.structure.processor.ProcessorManager;
import io.vertigo.ai.structure.processor.SlidingWindow;
import io.vertigo.ai.structure.processor.SlidingWindowType;
import io.vertigo.ai.structure.processor.SortOrder;
import io.vertigo.ai.structure.processor.Window;
import io.vertigo.basics.task.TaskEngineProc;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicType;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.util.StringUtil;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.vendor.SqlDialect;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.CriteriaCtx;
import io.vertigo.datamodel.criteria.CriteriaEncoder;
import io.vertigo.datamodel.smarttype.definitions.SmartTypeDefinition;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtDefinitionBuilder;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.definitions.DtProperty;
import io.vertigo.datamodel.structure.definitions.Properties;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.definitions.TaskDefinitionBuilder;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.plugins.entitystore.sql.SqlCriteriaEncoder;

public final class SqlDatasetProcessingPluginImpl implements DatasetProcessingPlugin {

	private static final String CONNECTION_NAME = "connectionName";
	
	private final static String SELECT_PREFIX = "IPS_";
	private final static String JOIN_PREFIX = "IPJ_";
	private final static String GROUP_PREFIX = "IPG_";
	private final static String PIVOT_PREFIX = "IPP_";
	private final static String WINDOW_PREFIX = "IPW_";
	private final static String SMART_TYPE_PREFIX = SmartTypeDefinition.PREFIX;
	private final static String DT_DEFINITION_PREFIX = DtDefinition.PREFIX;
	
	private final String dataSpace;
	private final String connectionName;
	private final String sequencePrefix;
	
	private final SqlDialect sqlDialect;
	private final TaskManager taskManager;
	private final CriteriaEncoder criteriaEncoder;
	
	private CriteriaCtx criteriaCtx = null;
	
	private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	@Inject
	public SqlDatasetProcessingPluginImpl(
			@ParamValue("dataSpace") final Optional<String> optDataSpace,
			@ParamValue(CONNECTION_NAME) final Optional<String> optConnectionName,
			@ParamValue("sequencePrefix") final Optional<String> optSequencePrefix,
			final TaskManager taskManager,
			final SqlManager sqlManager) {
		Assertion.check()
				 .isNotNull(optDataSpace)
				 .isNotNull(optConnectionName)
				 .isNotNull(optSequencePrefix)
				 .isNotNull(taskManager)
				 .isNotNull(sqlManager);
		
		dataSpace = optDataSpace.orElse(ProcessorManager.MAIN_DATA_SPACE_NAME);
		connectionName = optConnectionName.orElse(SqlManager.MAIN_CONNECTION_PROVIDER_NAME);
		sequencePrefix = optSequencePrefix.orElse("SEQ_");
		this.taskManager = taskManager;
		sqlDialect = sqlManager.getConnectionProvider(connectionName).getDataBase().getSqlDialect();
		criteriaEncoder = new SqlCriteriaEncoder(sqlDialect);
	}
	
	@Override
	public <E extends Entity> Dataset<E> select(Dataset<E> dataset,	Map<String, Object> params) {
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		int order = (int) params.get("order");
		
 		String outputName = generateOutputName(SELECT_PREFIX, tableName, order);
 		
 		// Build request
		String requestedFields = (String) params.get("requestedFields");
		StringBuilder queryBuilder = new StringBuilder("truncate table ")
					.append(outputName)
					.append(";")
					.append("insert into ")
					.append(outputName)
					.append(" select ")
					.append(StringUtil.camelToConstCase(requestedFields))
					.append(" from ")
					.append(tableName)
					.append(handleSortFilter(params));
		String query = queryBuilder.append(";").toString();
		
		String taskName = "TkInsertSelectProcessing" + order;
		
		List<DtField> fields = new ArrayList<DtField>();
		for(String fieldName : requestedFields.split(",")) {
			fields.add(tableDefinition.getField(fieldName));
		}
		
		//Generate types
		String outputDefinitionBaseName = StringUtil.constToUpperCamelCase(outputName);
		SmartTypeDefinition outputSmartType = getOutputSmartType(outputDefinitionBaseName);
		DtDefinition outputDtDefinition = getOutputDtDefinition(outputDefinitionBaseName, tableDefinition, fields);
		String createTableQuery = buildCreateTableQuery(outputName, outputDtDefinition.getFields());
		
		return executeTask(taskName, tableDefinition, outputDtDefinition, outputSmartType, createTableQuery+query);
	}

	@Override
	public <E extends Entity> Dataset<E> join(Dataset<E> dataset, Map<String, Object> params) {
		// Generate output table name
		DtDefinition leftTableDefinition = dataset.getItemDefinition();
		Dataset<E> rightDataset = (Dataset<E>) params.get("rightDataset");
		DtDefinition rightTableDefinition = rightDataset.getItemDefinition();
		String leftTableName = getTableName(leftTableDefinition);
		String rightTableName = getTableName(rightTableDefinition);
		int order = (int) params.get("order");
		
		//String outputName = JOIN_PREFIX + leftTableName + "_" + rightTableName + "_" + order;
		String outputName = JOIN_PREFIX + leftTableName.substring(0, 3) + "_" + rightTableName.substring(0, 3) + "_" + order;
		
		String onLeftField = (String) params.get("leftField");
		String onRightField = (String) params.get("rightField");
		
		//Get requested field
		List<DtField> leftFields = leftTableDefinition.getFields()
				.stream()
				//.filter(x -> !x.equals(!x.getName().equals("id"))) // TODO: Code Smell
				.collect(Collectors.toList());
		
		// Getting fieldNames to detect collisions
		List<String> leftFieldNames = leftFields.stream()
										       .map(DtField::getName)
										       .collect(Collectors.toList());
		
		List<DtField> rightFields = rightTableDefinition.getFields()
				.stream()
				.filter(x -> !leftFieldNames.contains(x.getName())) // TODO: Add support for fieldname prefix in case of name collision  
				.filter(x -> !x.equals(rightTableDefinition.getField(onRightField)) && !x.getName().equals("id"))
				.collect(Collectors.toList());
		
		List<DtField> fields = new ArrayList<DtField>();
		fields.addAll(leftFields);
		fields.addAll(rightFields);
		List<String> fieldNames = new ArrayList<String>();
		leftFields.forEach(field -> fieldNames.add("l."+StringUtil.camelToConstCase(field.getName())));
		rightFields.forEach(field -> fieldNames.add("r."+StringUtil.camelToConstCase(field.getName())));
		
		// Build request
		StringBuilder queryBuilder = new StringBuilder("truncate table ")
				.append(outputName)
				.append("; insert into ")
				.append(outputName)
				.append(" select ")
				.append(String.join(",", fieldNames))
				.append(" from (")
				.append(leftTableName)
				.append(" as l join ")
				.append(rightTableName)
				.append(" as r on l.")
				.append(StringUtil.camelToConstCase(onLeftField))
				.append("=r.")
				.append(StringUtil.camelToConstCase(onRightField))
				.append(");");
		
		String taskName = "TkInsertJoin" + order;
		
		//Generate types
		String outputDefinitionBaseName = StringUtil.constToUpperCamelCase(outputName);
		SmartTypeDefinition outputSmartType = getOutputSmartType(outputDefinitionBaseName);
		DtDefinition outputDtDefinition = getOutputDtDefinition(outputDefinitionBaseName, leftTableDefinition, fields);
		String createTableQuery = buildCreateTableQuery(outputName, outputDtDefinition.getFields());
		
		return executeTask(taskName, leftTableDefinition, outputDtDefinition, outputSmartType, createTableQuery + queryBuilder.toString());
	}
	
	@Override
	public <E extends Entity> Dataset<E> group(Dataset<E> dataset, Map<String, Object> params) {
		
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		int order = (int) params.get("order");
		
		String outputName = generateOutputName(GROUP_PREFIX, tableName, order);
		
 		//Prepare requested fields
 		String groupFieldName = (String) params.get("field");
 		String constFieldName = StringUtil.camelToConstCase(groupFieldName);
 		List<AgregatorType> aggTypes = Arrays.asList((AgregatorType[]) params.get("aggType"));
 		DtField groupField = tableDefinition.getField(groupFieldName);
 		
 		StringBuilder queryBuilder = new StringBuilder("truncate table ")
				.append(outputName)
				.append("; insert into ")
				.append(outputName)
				.append(" select ")
				.append(constFieldName)
				.append(", ");
 		
 		for (AgregatorType aggType : aggTypes) {
 			queryBuilder.append(aggType.getType())
 				.append("(*) as ")
 				.append(aggType.getType()+"_"+constFieldName);
 			if (aggTypes.indexOf(aggType) != aggTypes.size()-1 ) {
 				queryBuilder.append(",");
 			}
 			queryBuilder.append(" ");
 		}
 		
 		queryBuilder.append("from ")
 			.append(tableName)
 			.append(" group by ")
 			.append(constFieldName);
 		
 		String taskName = "TkInsertGroupBy" + order;
 		
 		//Generate types
		String outputDefinitionBaseName = StringUtil.constToUpperCamelCase(outputName);
		SmartTypeDefinition outputSmartType = getOutputSmartType(outputDefinitionBaseName);
		DtDefinition outputDtDefinition = getOutputDtDefinitionForGroupBy(outputDefinitionBaseName, tableDefinition, groupField, aggTypes);
		String createTableQuery = buildCreateTableQueryGroupBy(outputName, groupField, aggTypes);
 		
		return executeTask(taskName, tableDefinition, outputDtDefinition, outputSmartType, createTableQuery + queryBuilder.toString());
	}
	
	
	@Override
	public <E extends Entity> Dataset<E> pivot(Dataset<E> dataset, Map<String, Object> params) {
		
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		
		// TODO: Order
		int order = (int) params.get("order");
		
		String outputName = generateOutputName(PIVOT_PREFIX, tableName, order);
 		
 		//Prepare requested fields
		List<String> rowIdfields = (List<String>) params.get("rowIdfields");
		String pivotColumn = (String) params.get("pivotColumn");
		List<String> pivotStaticValues = (List<String>) params.get("pivotStaticValues");
		List<Agregator> aggs = (List<Agregator>) params.get("aggs");
		
		DtField pivotField = tableDefinition.getField(pivotColumn);

		List<DtField> groupFields = new ArrayList<>();
		List<String> rowIdsConstFieldNames = new ArrayList<>();

		for (String rowIdfield : rowIdfields) {
			rowIdsConstFieldNames.add(StringUtil.camelToConstCase(rowIdfield));
			groupFields.add(tableDefinition.getField(rowIdfield));
		}
 		
 		StringBuilder queryBuilder = new StringBuilder("truncate table ")
				.append(outputName)
				.append("; insert into ")
				.append(outputName)
				.append(" select ")
				.append(String.join(",", rowIdsConstFieldNames))
				.append(", ");

 		StringJoiner sjExternalSelect = new StringJoiner(",");
 		
 		for (Agregator agg : aggs) {
 			AgregatorType aggType = agg.getAgregatorType();
 			
 			for (String staticValue : pivotStaticValues) {
 				StringBuilder sbAgg = new StringBuilder();
 				
 				String sqlAggFunction;
 				if (AgregatorType.COUNT == aggType && agg.getAgregatorField().isEmpty()) {
 					sqlAggFunction = AgregatorType.SUM.getType();
 				} else {
 					sqlAggFunction = aggType.getType();
 				}
 				
				String pivotedFeature = generateInnerPivotedColumn(agg.getAgregatorField(), staticValue);
				sbAgg.append(sqlAggFunction)
					 .append("(\"")
					 .append(pivotedFeature)
					 .append("\") as ");
				String aliasFeature = generateExternPivotedColumn(agg, staticValue);
				sbAgg.append("\"").append(aliasFeature).append("\"");
 				
 				sjExternalSelect.add(sbAgg);
			}
 		}
 		
 		queryBuilder.append(sjExternalSelect).append(" ");

 		queryBuilder.append("from (")
 			.append(" select ")
 			.append(String.join(",", rowIdfields))
 			.append(" , ");
 		
 		StringJoiner sjInnerSelectAgg = new StringJoiner(",");
 		
 		List<String> aggFields = aggs.stream()
 									 .filter(agg -> agg.getAgregatorField().isPresent())
 									 .map(agg -> agg.getAgregatorField().get())
 									 .distinct()
 									 .collect(Collectors.toList());

 		for (String pivotValue : pivotStaticValues) {
 			
 			for (String aggField : aggFields) {
 				String innerSelect = generateCaseInnerSelect(pivotField, pivotValue, Optional.of(aggField));
 				sjInnerSelectAgg.add(innerSelect);
 			}
 			
 			boolean hasGlobalCount = aggs.stream().filter(agg -> agg.getAgregatorField().isEmpty()).anyMatch(agg -> AgregatorType.COUNT == agg.getAgregatorType());
	 		if (hasGlobalCount) {
	 			String innerSelect = generateCaseInnerSelect(pivotField, pivotValue, Optional.empty());
	 			sjInnerSelectAgg.add(innerSelect);
	 		}
 		}

 		queryBuilder.append(sjInnerSelectAgg)
 					.append(" from ")
 					.append(tableName)
		 			.append(") as train_pivot group by ")
		 			.append(String.join(",", rowIdsConstFieldNames));

 		String taskName = "TkInsertPivot" + order;

 		//Generate types
		String outputDefinitionBaseName = StringUtil.constToUpperCamelCase(outputName);
		SmartTypeDefinition outputSmartType = getOutputSmartType(outputDefinitionBaseName);
		DtDefinition outputDtDefinition = getOutputDtDefinitionForPivot(outputDefinitionBaseName, tableDefinition, pivotStaticValues, groupFields, aggs);
		String createTableQuery = buildCreateTableQueryPivot(outputName, groupFields, pivotStaticValues, aggs);
 		
		return executeTask(taskName, tableDefinition, outputDtDefinition, outputSmartType, createTableQuery + queryBuilder.toString());
	}

	private String generateOutputName(String operationPrefix, String tableName, int order) {
		List<String> tableNameElement = Arrays.asList(tableName.split("_"));
		int tableNameElementSize = tableNameElement.size()-1;
		String outputName;
		
 		if (pattern.matcher(tableNameElement.get(tableNameElementSize)).matches()) {
			outputName = operationPrefix + String.join("_", tableNameElement.subList(0, tableNameElementSize)) + "_" + order;
		} else {
			 outputName = operationPrefix + tableName + "_" + order;
		}
		return outputName;
	}

	private String generateCaseInnerSelect(DtField pivotField, String pivotValue, Optional<String> agregatorField) {
		StringBuilder sbInnerSelect = new StringBuilder();
		
		sbInnerSelect.append("case when (")
					.append(StringUtil.camelToConstCase(pivotField.getName()))
					.append(" = '")
					.append(pivotValue)
					.append("') THEN ");
		
		if (agregatorField.isEmpty()) {
			sbInnerSelect.append(" 1 ELSE 0");
		} else {
			sbInnerSelect.append("\"").append(agregatorField.get()).append("\"");
		}
		
		String pivotedFeature = generateInnerPivotedColumn(agregatorField, pivotValue);
		
		sbInnerSelect.append(" END AS \"")
					.append(pivotedFeature)
					.append("\"");
		return sbInnerSelect.toString();
	}

	
	private String generateInnerPivotedColumn(Optional<String> agregatorField, String staticValue) {
		String aggField = agregatorField.orElse("count");
		return staticValue + "_" + aggField; 
	}

	private String generateExternPivotedColumn(Agregator agg, String staticValue) {
		StringBuilder aggField = new StringBuilder();
		String aggType = agg.getAgregatorType().getType();
		aggField.append(aggType)
				.append("_")
				.append(staticValue.replace(' ', '_'));

		if (agg.getAgregatorField().isPresent()) {
			aggField.append("_").append(agg.getAgregatorField().get());
		}

		return aggField.toString();
	}

	@Override
	public <E extends Entity, F extends DtObject> Dataset<E> build(Dataset<E> inputDataset, Dataset<F> outputDataset){
		DtDefinition inputDefinition = inputDataset.getItemDefinition();
		String inputTableName = getTableName(inputDefinition);
		DtDefinition outputDefinition = outputDataset.getItemDefinition();
		String outputTableName = getTableName(outputDefinition);
		
		//check if there is an id
		List<DtField> fields = inputDefinition.getFields();
		String id = "row_number() over ( order by " + StringUtil.camelToConstCase(fields.get(0).getName()) + ") as id, ";
		for (DtField field : inputDefinition.getFields()) {
			if (field.getName().equals("id")) {
				id = "";
			}
		}
		String query = new StringBuilder("truncate table ")
				.append(outputTableName)
				.append("; insert into ")
				.append(outputTableName)
				.append(" select ")
				.append(id)
				.append("* from ")
				.append(inputTableName)
				.toString();
		
		String taskName = "TkInsertBuildProcessing";
		SmartTypeDefinition outputSmartType = getOutputSmartType(StringUtil.constToUpperCamelCase(outputTableName));
		executeTask(taskName, inputDefinition, outputDefinition, outputSmartType, query);
		return new DatasetImpl<E>(outputDefinition);
	}
	
	private <E extends Entity> String filter(Map<String, Object> params) {
		StringBuilder filterQueryBuilder = new StringBuilder(" where ");
		filterQueryBuilder.append(convertCriteriaToSql((Criteria<E>) params.get("criteria")));
		
		return filterQueryBuilder.toString();
	}
	
	private String sort(List<Map<String, Object>> params) {
		StringBuilder sortQueryBuilder = new StringBuilder(" order by ");
		int size = params.size();
		for (Map<String, Object> sortParams : params) {
			sortQueryBuilder.append(sortParams.get("sortField"))
				.append(" ")
				.append(((SortOrder) sortParams.get("sortOrder")).getValue());
			if (params.indexOf(sortParams) != size - 1) {
				sortQueryBuilder.append(", ");
			}
		}

		return sortQueryBuilder.toString();
	}
	
	private String handleSortFilter(Map<String, Object> params) {
		StringBuilder sortFilterQueryBuilder = new StringBuilder("");
		if (params.containsKey("filter")) {
			String filterQuery = filter((Map<String, Object>) params.get("filter"));
			sortFilterQueryBuilder.append(filterQuery);
		}
		if (params.containsKey("sort")) {
			String sortQuery = sort((List<Map<String, Object>>)params.get("sort"));
			sortFilterQueryBuilder.append(sortQuery);
		}
		return sortFilterQueryBuilder.toString();
	}
	
	private String getTableName(DtDefinition tableDefinition) {
		String definitionName = tableDefinition.getFragment().orElse(tableDefinition).getLocalName();
		String tableName = StringUtil.camelToConstCase(definitionName);
		return tableName;
	}
	
	private <E extends Entity> String convertCriteriaToSql(Criteria<E> criteria) {
		final Tuple<String, CriteriaCtx> tuple = criteria.toStringAnCtx(criteriaEncoder);
		criteriaCtx = tuple.getVal2();
		return tuple.getVal1();
	}
	
	
	private SmartTypeDefinition getOutputSmartType(String outputDefinitionBaseName) {
		final SmartTypeDefinition outputSmartType = SmartTypeDefinition.builder(SMART_TYPE_PREFIX + outputDefinitionBaseName, BasicType.String).build();
		return outputSmartType;
	}
	
	private DtDefinition getOutputDtDefinition(String outputDefinitionBaseName, DtDefinition tableDefinition, List<DtField> fields) {
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX + outputDefinitionBaseName)
				.withPackageName(tableDefinition.getPackageName())
				.withDataSpace(tableDefinition.getDataSpace());
		
		for (DtField field : fields) {
			outputDefinitionBuilder.addDataField(field.getName(),
					field.getLabel().toString(),
					field.getSmartTypeDefinition(),
					field.getCardinality(),
					field.isPersistent());
		}
		
		return outputDefinitionBuilder.build();
	}
	
	private DtDefinition getOutputDtDefinitionForGroupBy(String outputDefinitionBaseName, DtDefinition tableDefinition, DtField groupField, List<AgregatorType> aggTypes) {
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX + outputDefinitionBaseName)
				.withPackageName(tableDefinition.getPackageName())
				.withDataSpace(tableDefinition.getDataSpace());
		outputDefinitionBuilder.addDataField(groupField.getName(),
				groupField.getLabel().toString(),
				groupField.getSmartTypeDefinition(),
				groupField.getCardinality(),
				groupField.isPersistent());
		
		for (AgregatorType aggType : aggTypes) {
			SmartTypeDefinition styDefinition = null;
			switch (aggType) {
				case COUNT:
					styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX + StringUtil.constToUpperCamelCase(aggType.getType() + "_" + groupField.getName()), BasicType.Integer)
													   .withProperties(Properties.builder().addValue(DtProperty.STORE_TYPE, "bigint").build())
													   .build();
					break;
				default:
					styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX + StringUtil.constToUpperCamelCase(aggType.getType() + "_" + groupField.getName()), BasicType.BigDecimal)
							   						   .withProperties(Properties.builder().addValue(DtProperty.STORE_TYPE, "numeric").build())
							   						   .build();
					break;
			}
			outputDefinitionBuilder.addComputedField(StringUtil.constToLowerCamelCase(aggType.getType() + "_" + groupField.getName()),
				aggType.getType() + " " + groupField.getName(),
				styDefinition,
				Cardinality.ONE);
		}
		
		return outputDefinitionBuilder.build();
	}
	
	private DtDefinition getOutputDtDefinitionForPivot(String outputDefinitionBaseName, DtDefinition tableDefinition, List<String> pivotStaticValues, List<DtField> groupFields, List<Agregator> aggs) {
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX+outputDefinitionBaseName)
				.withPackageName(tableDefinition.getPackageName())
				.withDataSpace(tableDefinition.getDataSpace());
		
		for (DtField groupField : groupFields) {
			outputDefinitionBuilder.addDataField(groupField.getName(),
					groupField.getLabel().toString(),
					groupField.getSmartTypeDefinition(),
					groupField.getCardinality(),
					groupField.isPersistent());
		}
		
		for (Agregator agg : aggs) {
			
			SmartTypeDefinition styDefinition = generatedAggregatorSmartType(agg);
			
			for (String pivotValue : pivotStaticValues) {
				
				String aliasFeature = generateExternPivotedColumn(agg, pivotValue);
				
				outputDefinitionBuilder.addComputedField(StringUtil.constToLowerCamelCase(aliasFeature),
						agg.getAgregatorType().getType() + " " + aliasFeature,
						styDefinition,
						Cardinality.ONE);
			}
			
		}
		
		return outputDefinitionBuilder.build();
	}

	private SmartTypeDefinition generatedAggregatorSmartType(Agregator agg) {
		SmartTypeDefinition styDefinition = null;
		switch (agg.getAgregatorType()) {
			case COUNT:
				styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX + StringUtil.constToUpperCamelCase(agg.getAgregatorType().getType()), BasicType.Integer)
												   .withProperties(Properties.builder().addValue(DtProperty.STORE_TYPE, "bigint").build())
												   .build();
				break;
			default:
				styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX + StringUtil.constToUpperCamelCase(agg.getAgregatorType().getType()), BasicType.BigDecimal)
												   .withProperties(Properties.builder().addValue(DtProperty.STORE_TYPE, "numeric").build())
												   .build();
				break;
		}
		return styDefinition;
	}
	
	private <E extends Entity> Dataset<E> executeTask(String taskName, DtDefinition tableDefinition, DtDefinition outputDefinition, SmartTypeDefinition outputSmartType, String query) {
		
		final TaskDefinitionBuilder taskDefinitionBuilder = TaskDefinition.builder(taskName)
				.withEngine(TaskEngineProc.class)
				.withDataSpace(dataSpace)
				.withRequest(query);
		
		if (criteriaCtx != null) {
			for (final String attributeName : criteriaCtx.getAttributeNames()) {
				taskDefinitionBuilder.addInAttribute(attributeName, tableDefinition.getField(criteriaCtx.getDtFieldName(attributeName)).getSmartTypeDefinition(), Cardinality.OPTIONAL_OR_NULLABLE);
			}
		}
		
		final TaskDefinition taskDefinition = taskDefinitionBuilder
				.withOutAttribute("ds", outputSmartType, Cardinality.OPTIONAL_OR_NULLABLE)
				.build();
		
		final TaskBuilder taskBuilder = Task.builder(taskDefinition);
		
		if (criteriaCtx != null) {
			for (final String attributeName : criteriaCtx.getAttributeNames()) {
				taskBuilder.addValue(attributeName, criteriaCtx.getAttributeValue(attributeName));
			}
		}
		
		taskBuilder.addContextProperty(CONNECTION_NAME, connectionName);
		Task task = taskBuilder.build();
		taskManager.execute(task);
		criteriaCtx = null;
		return new DatasetImpl<E>(outputDefinition);
	}
	
	private String buildCreateTableQuery(String outputName, List<DtField> fields) {
		StringBuilder createQuery = new StringBuilder("create table if not exists ")
					.append(outputName)
					.append(" (");
		for (DtField field : fields) {
			createQuery.append(StringUtil.camelToConstCase(field.getName()))
					.append(" ")
					.append(field.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE));
			if (fields.indexOf(field) != fields.size()-1)
				createQuery.append(",");
		}
		createQuery.append("); ");
		return createQuery.toString();
	}

	private String buildCreateTableQueryGroupBy(String outputName, DtField groupField, List<AgregatorType> aggTypes) {
		//TODO : Passer GroupBy d'un champ unique à une liste de champs (cf Pivot)
		
		String fieldName = StringUtil.camelToConstCase(groupField.getName());
		StringBuilder createQuery = new StringBuilder("create table if not exists ")
					.append(outputName)
					.append(" (");
		createQuery.append(fieldName)
			.append(" ")
			.append(groupField.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE))
			.append(", ");
		
		for (AgregatorType aggType : aggTypes) {
			createQuery.append(aggType.getType())
					   .append("_")
					   .append(fieldName)
					   .append(" ");
			switch (aggType) {
				case COUNT:
					createQuery.append("bigint");
					break;
				default:
					createQuery.append("numeric");
					break;
			}
			if (aggTypes.indexOf(aggType)!=aggTypes.size()-1) {
				createQuery.append(",");
			}
			createQuery.append(" ");
		}
		
		createQuery.append("); ");
		return createQuery.toString();
	}
	
	
	private String buildCreateTableQueryPivot(String outputName, List<DtField> groupFields, List<String> pivotStaticValues, List<Agregator> aggs) {
		
		StringBuilder createQuery = new StringBuilder("create table if not exists ")
					.append(outputName)
					.append(" (");
		
		for (DtField groupField : groupFields) {
			String fieldName = StringUtil.camelToConstCase(groupField.getName());

			createQuery.append(fieldName)
					   .append(" ")
					   .append(groupField.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE))
					   .append(", ");
		}
		
		for (Agregator agg : aggs) {
			for (String pivotValue : pivotStaticValues) {
				String aliasFeature = generateExternPivotedColumn(agg, pivotValue);
				createQuery.append(aliasFeature)
						   .append(" ");

				switch (agg.getAgregatorType()) {
					case COUNT:
						createQuery.append("bigint");
						break;
					default:
						createQuery.append("numeric");
						break;
				}
				
				if (pivotStaticValues.indexOf(pivotValue) != pivotStaticValues.size()-1) {
					createQuery.append(",");
				}
			}
			
			if (aggs.indexOf(agg) != aggs.size()-1) {
				createQuery.append(",");
			}
			createQuery.append(" ");
		}
		createQuery.append("); ");
		return createQuery.toString();
	}

	@Override
	public <E extends Entity> Dataset<E> window(Dataset<E> dataset, Map<String, Object> params) {
		List<String> strFields = (List<String>) params.get("fields");
		List<Agregator> aggs = (List<Agregator>) params.get("aggs");
		List<Window> windows = (List<Window>) params.get("windows");
		///
		Assertion.check()
			.isNotNull(strFields)
			.isNotNull(aggs)
			.isNotNull(windows)
			.isFalse(aggs.stream().anyMatch(agg -> agg.getAgregatorField().isEmpty()), "Aggregation must have a field associated (No global count allowed)");
		///
		
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		
		// TODO: Order
		int order = (int) params.get("order");
		
		String outputName = generateOutputName(WINDOW_PREFIX, tableName, order);

		List<DtField> fields = new ArrayList<>();
		List<String> fieldsConstFieldNames = new ArrayList<>();

		for (String strField : strFields) {
			fieldsConstFieldNames.add(StringUtil.camelToConstCase(strField));
			fields.add(tableDefinition.getField(strField));
		}
		
		List<String> noStoreTypeField = fields.stream()
					.filter(field -> field.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE) == null)
					.map(DtField::getName)
					.collect(Collectors.toList());

		Assertion.check().isTrue(noStoreTypeField.isEmpty(), "{0} fields must have a StoreType", String.join(",", noStoreTypeField));
 		
 		StringBuilder queryBuilder = new StringBuilder("truncate table ")
				.append(outputName)
				.append("; insert into ")
				.append(outputName)
				.append(" select ")
				.append(String.join(",", fieldsConstFieldNames))
				.append(", ");

 		StringJoiner sjSelect = new StringJoiner(",");

 		for (Window win : windows) {
 			for (Agregator agg : aggs) {
 				
 				AgregatorType aggType = agg.getAgregatorType();
 				
 				StringBuilder sbAgg = new StringBuilder();
 				String sqlAggFunction = aggType.getType();
 				
 				String partitionByJoined = win.getPartitionField()
 						.stream()
 						.collect(Collectors.joining(",", "\"", "\""));
 				
 				String orderByJoined = win.getOrderFields()
 						.stream()
 						.map(of -> "\"" + of.getFieldName() + "\" " + of.getSortOrder().getValue() + " NULLS FIRST")
 						.collect(Collectors.joining(","));
 				
 				sbAgg.append(sqlAggFunction)
	 				 .append("(\"")
	 				 .append(agg.getAgregatorField().get())
	 				 .append("\")")
	 				 .append(" OVER (PARTITION BY ")
	 				 .append(partitionByJoined)
	 				 .append(" ORDER BY ")
	 				 .append(orderByJoined);
 				
 				if (win.getSlidingWindow().isPresent()) {
 					SlidingWindow sw = win.getSlidingWindow().get();
 					
 					if (sw.getSlidingWindowType() == SlidingWindowType.ROWS) {
 						sbAgg.append(" ROWS");
 					} else if (sw.getSlidingWindowType() == SlidingWindowType.VALUES) {
 						sbAgg.append(" RANGE");
 					}
 					
 					sbAgg.append(" BETWEEN ")
 						 .append(generateMinBoundValue(sw.getMinValue()))
 						 .append(" AND ")
 						 .append(generateMaxBoundValue(sw.getMaxValue()));
 				}
 				
 				String aliasFeature = generateAliasWindowColumn(agg, win);
 				
 				sbAgg.append(") as \"")
 					 .append(aliasFeature)
 					 .append("\"");
 				
 				sjSelect.add(sbAgg);
 			}
 		}
 		
 		queryBuilder.append(sjSelect)
 					.append(" FROM ")
 					.append(tableName);

 		String taskName = "TkInsertWindow" + order;
 		
 		//Generate types
		String outputDefinitionBaseName = StringUtil.constToUpperCamelCase(outputName);
		SmartTypeDefinition outputSmartType = getOutputSmartType(outputDefinitionBaseName);
		DtDefinition outputDtDefinition = getOutputDtDefinitionForWindow(outputDefinitionBaseName, tableDefinition, fields, aggs, windows);
		String createTableQuery = buildCreateTableQueryWindow(outputName, fields, aggs, windows);
 		
		return executeTask(taskName, tableDefinition, outputDtDefinition, outputSmartType, createTableQuery + queryBuilder.toString());
	}

	private DtDefinition getOutputDtDefinitionForWindow(String outputDefinitionBaseName, DtDefinition tableDefinition, List<DtField> fields, List<Agregator> aggs, List<Window> windows) {
		
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX + outputDefinitionBaseName)
				.withPackageName(tableDefinition.getPackageName())
				.withDataSpace(tableDefinition.getDataSpace());
		
		for (DtField field : fields) {
			outputDefinitionBuilder.addDataField(field.getName(),
					field.getLabel().toString(),
					field.getSmartTypeDefinition(),
					field.getCardinality(),
					field.isPersistent());
		}

		for (Agregator agg : aggs) {
			SmartTypeDefinition styDefinition = generatedAggregatorSmartType(agg);

			for (Window win : windows) {
				String aliasFeature = generateAliasWindowColumn(agg, win);
				
				outputDefinitionBuilder.addComputedField(StringUtil.constToLowerCamelCase(aliasFeature),
						agg.getAgregatorType().getType() + " " + aliasFeature,
						styDefinition,
						Cardinality.ONE);
			}
		}

		return outputDefinitionBuilder.build();
	}

	private String generateAliasWindowColumn(Agregator agg, Window win) {
		StringBuilder sb = new StringBuilder();
		sb.append(win.getId())
		  .append("_")
		  .append(agg.getAgregatorField().get())
		  .append("_")
		  .append(agg.getAgregatorType().getType());
		return sb.toString();
	}

	private String buildCreateTableQueryWindow(String outputName, List<DtField> fields, List<Agregator> aggs, List<Window> windows) {
		StringBuilder createQuery = new StringBuilder("create table if not exists ")
				.append(outputName)
				.append(" (");
	
		for (DtField field : fields) {
			String fieldName = StringUtil.camelToConstCase(field.getName());

			createQuery.append(fieldName)
					   .append(" ")
					   .append(field.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE))
					   .append(", ");
		}

		for (Window win : windows) {
			for (Agregator agg : aggs) {

				String aliasFeature = generateAliasWindowColumn(agg, win);
				createQuery.append("\"").append(aliasFeature).append("\" ");

				switch (agg.getAgregatorType()) {
					case COUNT:
						createQuery.append("bigint");
						break;
					default:
						createQuery.append("numeric");
						break;
				}

				if (aggs.indexOf(agg) != aggs.size()-1) {
					createQuery.append(",");
				}
				createQuery.append(" ");
			}
		}
		
		createQuery.append("); ");
		return createQuery.toString();
	}
	
	private String generateMinBoundValue(OptionalInt value) {
		return generateBoundValue(value, "PRECEDING");
	}

	private String generateMaxBoundValue(OptionalInt value) {
		return generateBoundValue(value, "FOLLOWING");
	}
	
	private String generateBoundValue(OptionalInt value, String boundDir) {
		String ret;
		
		if (value.isPresent()) {
			int val = value.getAsInt();
			if (val == 0) {
				ret = "CURRENT ROW";
			} else {
				ret = String.valueOf(val) + " " + boundDir;
			}
		} else {
			ret = "UNBOUNDED " + boundDir;
		}
 
		return ret;
	}
}
