package io.vertigo.ai.predict.withstore;

import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.impl.structure.datasetInMemory.loader.AbstractSqlDatasetLoader;
import io.vertigo.ai.predict.data.domain.boston.BostonItem;
import io.vertigo.ai.predict.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.dataset.models.Dataset;
import io.vertigo.ai.structure.row.definitions.RowChunk;
import io.vertigo.ai.structure.row.definitions.RowDefinition;
import io.vertigo.ai.structure.row.models.Row;
import io.vertigo.basics.task.TaskEngineSelect;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.Cardinality;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.smarttype.definitions.SmartTypeDefinition;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;

public final class ItemDatasetStoreLoader extends AbstractSqlDatasetLoader<Long, BostonRegressionItem, BostonRegressionItem> {

	private final DatasetManager datasetManager;
	private final DefinitionSpace definitionSpace;
	
	@Inject
	public ItemDatasetStoreLoader(final TaskManager taskManager, final DatasetManager datasetManager, VTransactionManager transactionManager) {
		super(taskManager, transactionManager);
		this.datasetManager = datasetManager;
		definitionSpace = Node.getNode().getDefinitionSpace();
	}

	@Override
	public Dataset loadData(RowChunk<BostonRegressionItem> itemChunk, String datasetName) {
		final DatasetDefinition datasetDefinition = datasetManager.findDatasetDefinition(datasetName);
		final RowDefinition datasetItemDefinition = datasetManager.findFirstRowDefinitionByKeyConcept(BostonItem.class);
		final Class<?> clazz = itemChunk.getRowClass();
		try (final VTransactionWritable tx = getTransactionManager().createCurrentTransaction()) {
			final Dataset result = new Dataset(clazz, datasetDefinition);
			for (final BostonRegressionItem item : loadItems(itemChunk)) {
				final UID<BostonRegressionItem> uid = item.getUID();
				result.append(new Row(item, uid, datasetItemDefinition));
			}
			return result;
		}
	}

	private DtList<BostonRegressionItem> loadItems(final RowChunk<BostonRegressionItem> itemChunk) {
		final TaskDefinition taskDefinition = getTaskDefinition(itemChunk);

		final Task task = Task.builder(taskDefinition)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	private TaskDefinition getTaskDefinition(final RowChunk<BostonRegressionItem> itemChunk) {
		final SmartTypeDefinition smartTypeItem = definitionSpace.resolve("STyDtBostonRegressionItem", SmartTypeDefinition.class);
		final String sql = itemChunk.getAllUIDs()
				.stream()
				.map(uri -> uri.getId().toString())
				.collect(Collectors.joining(", ", "select * from BOSTON_REGRESSION_ITEM where ID in (", ")"));

		return TaskDefinition.builder("TkLoadAllItems")
				.withEngine(TaskEngineSelect.class)
				.withRequest(sql)
				.withPackageName(TaskEngineSelect.class.getPackage().getName())
				.withOutAttribute("dtc", smartTypeItem, Cardinality.MANY)
				.build();
	}

}
