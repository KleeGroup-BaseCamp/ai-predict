package io.vertigo.ai.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.example.domain.DtDefinitions;
import io.vertigo.ai.impl.structure.dataset.DatasetImpl;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.processor.AgregationSmartTypes;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.ai.structure.processor.SortOrder;
import io.vertigo.basics.formatter.FormatterString;
import io.vertigo.basics.task.TaskEngineProc;
import io.vertigo.basics.task.TaskEngineProcBatch;
import io.vertigo.basics.task.TaskEngineSelect;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.BasicType;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.Node;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.util.StringUtil;
import io.vertigo.database.impl.sql.SqlManagerImpl;
import io.vertigo.database.sql.SqlManager;
import io.vertigo.database.sql.vendor.SqlDialect;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.CriteriaCtx;
import io.vertigo.datamodel.criteria.CriteriaEncoder;
import io.vertigo.datamodel.impl.smarttype.SmartTypeManagerImpl;
import io.vertigo.datamodel.smarttype.FormatterConfig;
import io.vertigo.datamodel.smarttype.definitions.SmartTypeDefinition;
import io.vertigo.datamodel.smarttype.definitions.SmartTypeDefinitionBuilder;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtDefinitionBuilder;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.definitions.DtProperty;
import io.vertigo.datamodel.structure.definitions.Property;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.definitions.TaskDefinitionBuilder;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.plugins.entitystore.sql.SqlCriteriaEncoder;

public final class SqlDatasetProcessingPluginImpl
		implements
			DatasetProcessingPlugin {

	private static final String CONNECTION_NAME = "connectionName";
	
	private final static String SELECT_PREFIX = "IPS_";
	private final static String JOIN_PREFIX = "IPJ_";
	private final static String GROUP_PREFIX = "IPG_";
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
		
		dataSpace = optDataSpace.orElse(DatasetManager.MAIN_DATA_SPACE_NAME);
		connectionName = optConnectionName.orElse(SqlManager.MAIN_CONNECTION_PROVIDER_NAME);
		sequencePrefix = optSequencePrefix.orElse("SEQ_");
		this.taskManager = taskManager;
		sqlDialect = sqlManager.getConnectionProvider(connectionName).getDataBase().getSqlDialect();
		criteriaEncoder = new SqlCriteriaEncoder(sqlDialect);
	}
	
	@Override
	public <E extends Entity> io.vertigo.ai.structure.dataset.Dataset<E> select(
			io.vertigo.ai.structure.dataset.Dataset<E> dataset,
			Map<String, Object> params) {
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		int order = (int) params.get("order");
		List<String> tableNameElement = Arrays.asList(tableName.split("_"));
		int tableNameElementSize = tableNameElement.size()-1;
		String outputName;
 		if (pattern.matcher(tableNameElement.get(tableNameElementSize)).matches()) {
			outputName = SELECT_PREFIX + String.join("_", tableNameElement.subList(0, tableNameElementSize-1)) + "_" + order;
		} else {
			 outputName = SELECT_PREFIX + tableName + "_" + order;
		}
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
	public <E extends Entity> io.vertigo.ai.structure.dataset.Dataset<E> join(
			io.vertigo.ai.structure.dataset.Dataset<E> dataset,
			Map<String, Object> params) {
		// Generate output table name
		DtDefinition leftTableDefinition = dataset.getItemDefinition();
		Dataset<E> rightDataset = (Dataset<E>) params.get("rightDataset");
		DtDefinition rightTableDefinition = rightDataset.getItemDefinition();
		String leftTableName = getTableName(leftTableDefinition);
		String rightTableName = getTableName(rightTableDefinition);
		int order = (int) params.get("order");
		
		String outputName = JOIN_PREFIX+leftTableName+"_"+rightTableName+"_"+order;
		
		String onLeftField = (String) params.get("leftField");
		String onRightField = (String) params.get("rightField");
		
		//Get requested field
		List<DtField> leftFields = leftTableDefinition.getFields()
				.stream()
				.filter(x -> !x.equals(!x.getName().equals("id")))
				.collect(Collectors.toList());
		List<DtField> rightFields = rightTableDefinition.getFields()
				.stream()
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
	public <E extends Entity> Dataset<E> group(Dataset<E> dataset,
			Map<String, Object> params) {
		
		// Generate table name
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		int order = (int) params.get("order");
		List<String> tableNameElement = Arrays.asList(tableName.split("_"));
		int tableNameElementSize = tableNameElement.size()-1;
		String outputName;
 		if (pattern.matcher(tableNameElement.get(tableNameElementSize)).matches()) {
			outputName = GROUP_PREFIX + String.join("_", tableNameElement.subList(0, tableNameElementSize)) + "_" + order;
		} else {
			 outputName = GROUP_PREFIX + tableName + "_" + order;
		}
 		
 		//Prepare requested fields
 		String groupFieldName = (String) params.get("field");
 		String constFieldName = StringUtil.camelToConstCase(groupFieldName);
 		List<Agregator> aggTypes = Arrays.asList((Agregator[]) params.get("aggType"));
 		DtField groupField = tableDefinition.getField(groupFieldName);
 		
 		StringBuilder queryBuilder = new StringBuilder("truncate table ")
				.append(outputName)
				.append("; insert into ")
				.append(outputName)
				.append(" select ")
				.append(constFieldName)
				.append(", ");
 		
 		for (Agregator aggType : aggTypes) {
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
		System.out.println(tuple);
		criteriaCtx = tuple.getVal2();
		return tuple.getVal1();
	}
	
	
	private SmartTypeDefinition getOutputSmartType(String outputDefinitionBaseName) {
		final SmartTypeDefinition outputSmartType = SmartTypeDefinition.builder(SMART_TYPE_PREFIX+outputDefinitionBaseName, BasicType.String).build();
		return outputSmartType;
	}
	
	private DtDefinition getOutputDtDefinition(String outputDefinitionBaseName, DtDefinition tableDefinition, List<DtField> fields) {
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX+outputDefinitionBaseName)
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
	
	private DtDefinition getOutputDtDefinitionForGroupBy(String outputDefinitionBaseName, DtDefinition tableDefinition, DtField groupField, List<Agregator> aggTypes) {
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder(DT_DEFINITION_PREFIX+outputDefinitionBaseName)
				.withPackageName(tableDefinition.getPackageName())
				.withDataSpace(tableDefinition.getDataSpace());
		outputDefinitionBuilder.addDataField(groupField.getName(),
				groupField.getLabel().toString(),
				groupField.getSmartTypeDefinition(),
				groupField.getCardinality(),
				groupField.isPersistent());
		
		for (Agregator aggType : aggTypes) {
			SmartTypeDefinition styDefinition = null;
			switch (aggType) {
				case COUNT:
					styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX+StringUtil.constToUpperCamelCase(aggType.getType()+"_"+groupField.getName()), BasicType.Integer).build();
				default:
					styDefinition = SmartTypeDefinition.builder(SMART_TYPE_PREFIX+StringUtil.constToUpperCamelCase(aggType.getType()+"_"+groupField.getName()), BasicType.BigDecimal).build();
			}
			outputDefinitionBuilder.addComputedField(StringUtil.constToLowerCamelCase(aggType.getType()+"_"+groupField.getName()),
				aggType.getType()+" "+groupField.getName(),
				styDefinition,
				Cardinality.ONE);
		}
		
		return outputDefinitionBuilder.build();
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

	private String buildCreateTableQueryGroupBy(String outputName, DtField groupField, List<Agregator> aggTypes) {
		String fieldName = StringUtil.camelToConstCase(groupField.getName());
		StringBuilder createQuery = new StringBuilder("create table if not exists ")
					.append(outputName)
					.append(" (");
		createQuery.append(fieldName)
			.append(" ")
			.append(groupField.getSmartTypeDefinition().getProperties().getValue(DtProperty.STORE_TYPE))
			.append(", ");
		
		for (Agregator aggType : aggTypes) {
			switch (aggType) {
				case COUNT:
					createQuery.append(aggType.getType()+"_"+fieldName)
						.append(" ")
						.append("numeric");
					break;
				default:
					createQuery.append(aggType.getType()+"_"+fieldName)
						.append(" ")
						.append("numeric(16,16)");
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
}
