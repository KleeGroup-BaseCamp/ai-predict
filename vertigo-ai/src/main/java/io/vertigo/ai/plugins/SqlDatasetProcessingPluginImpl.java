package io.vertigo.ai.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.example.domain.DtDefinitions;
import io.vertigo.ai.impl.structure.dataset.DatasetImpl;
import io.vertigo.ai.structure.DatasetManager;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.processor.DatasetProcessingPlugin;
import io.vertigo.basics.task.TaskEngineSelect;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.Node;
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

	private static String SELECT_PREFIX = "INTERMEDIATE_PROCESSING_SELECT_";
	private static String JOIN_PREFIX = "INTERMEDIATE_PROCESSING_JOIN_";
	private static final String SMART_TYPE_PREFIX = SmartTypeDefinition.PREFIX;
	
	private final String dataSpace;
	private final String connectionName;
	private final String sequencePrefix;
	
	private final SqlDialect sqlDialect;
	private final TaskManager taskManager;
	private final CriteriaEncoder criteriaEncoder;
	
	private CriteriaCtx criteriaCtx = null;
	

	@Inject
	public SqlDatasetProcessingPluginImpl(
			@ParamValue("dataSpace") final Optional<String> optDataSpace,
			@ParamValue("connectionName") final Optional<String> optConnectionName,
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
		DtDefinition tableDefinition = dataset.getItemDefinition();
		String tableName = getTableName(tableDefinition);
		String outputName = SELECT_PREFIX + tableName;
		String requestedFields = (String) params.get("requestedFields");
		
		StringBuilder queryBuilder = new StringBuilder("create table if not exists ")
					.append(outputName)
					.append("; ")
					.append("insert into ")
					.append(outputName)
					.append(" select ")
					.append(requestedFields)
					.append(" from ")
					.append(tableName)
					.append(handleSortFilter(params));
		String query = queryBuilder.append(";").toString();
		
		String taskName = "TkSelect " + requestedFields + "from" + tableName;
		
		List<DtField> fields = new ArrayList<DtField>();
		for(String fieldName : requestedFields.split(",")) {
			fields.add(tableDefinition.getField(fieldName));
		}
		return executeTask(taskName, tableDefinition, outputName, fields, query);
	}

	@Override
	public <E extends Entity> io.vertigo.ai.structure.dataset.Dataset<E> join(
			io.vertigo.ai.structure.dataset.Dataset<E> dataset,
			Map<String, Object> params) {
		DtDefinition leftTableDefinition = dataset.getItemDefinition();
		Dataset<E> rightDataset = (Dataset<E>) params.get("rightDataset");
		DtDefinition rightTableDefinition = rightDataset.getItemDefinition();
		String leftTableName = getTableName(leftTableDefinition);
		String rightTableName = getTableName(rightTableDefinition);
		String outputName = "INTERMEDIATE_JOIN_"+leftTableName+"_"+rightTableName;
		
		String onLeftField = (String) params.get("onLeftField");
		String onRightField = (String) params.get("onRightField");
		StringBuilder queryBuilder = new StringBuilder("create table if not exists ")
				.append(outputName)
				.append("; ")
				.append("insert into ")
				.append(outputName)
				.append(" select (")
				.append(leftTableName)
				.append(" as l join ")
				.append(rightTableName)
				.append("as r on l.")
				.append(onLeftField)
				.append("=r.")
				.append(onRightField)
				.append(");");
		
		String taskName = "TkJoin " + leftTableName + "as l and" + rightTableName + "as r on l." + onLeftField + " = r." + onRightField;
		
		List<DtField> fields = leftTableDefinition.getFields();
		fields.addAll(
				rightTableDefinition.getFields()
					.stream()
					.filter(x -> !x.equals(rightTableDefinition.getField(onRightField)))
					.collect(Collectors.toList()));
		return executeTask(taskName, leftTableDefinition, outputName, fields, taskName);
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
				.append(sortParams.get("sortOrder"));
			if (params.indexOf(sortParams) != size) {
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
	
	private <E extends Entity> Dataset<E> executeTask(String taskName, DtDefinition tableDefinition, String outputName, List<DtField> fields, String query) {
		
		final DtDefinitionBuilder outputDefinitionBuilder = DtDefinition.builder("Dt"+StringUtil.constToUpperCamelCase(outputName))
						.withPackageName(tableDefinition.getPackageName())
						.withDataSpace(tableDefinition.getDataSpace());
		for (DtField field : fields) {
			outputDefinitionBuilder.addDataField(field.getName(),
					field.getLabel().toString(),
					field.getSmartTypeDefinition(),
					field.getCardinality(),
					field.isPersistent());
		}
		final DtDefinition outputDefinition = outputDefinitionBuilder.build();
		final TaskDefinitionBuilder taskDefinitionBuilder = TaskDefinition.builder(taskName)
				.withEngine(TaskEngineSelect.class)
				.withDataSpace(dataSpace)
				.withRequest(query);
		
		if (criteriaCtx != null) {
			for (final String attributeName : criteriaCtx.getAttributeNames()) {
				taskDefinitionBuilder.addInAttribute(attributeName, tableDefinition.getField(criteriaCtx.getDtFieldName(attributeName)).getSmartTypeDefinition(), Cardinality.OPTIONAL_OR_NULLABLE);
			}
		}
		
		final TaskDefinition taskDefinition = taskDefinitionBuilder
				.withOutAttribute("ds", Node.getNode().getDefinitionSpace().resolve(SMART_TYPE_PREFIX + outputDefinition.getName(), SmartTypeDefinition.class), Cardinality.MANY)
				.build();
		
		final TaskBuilder taskBuilder = Task.builder(taskDefinition);
		
		if (criteriaCtx != null) {
			for (final String attributeName : criteriaCtx.getAttributeNames()) {
				taskBuilder.addValue(attributeName, criteriaCtx.getAttributeValue(attributeName));
			}
		}
		
		taskManager.execute(taskBuilder
				.addContextProperty("connectionName", connectionName)
				.build());
		criteriaCtx = null;
		return new DatasetImpl<E>(outputDefinition);
	}
}
