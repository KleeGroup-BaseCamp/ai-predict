package io.vertigo.ai.withstore;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.data.domain.boston.BostonItem;
import io.vertigo.ai.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.datasetItems.definitions.DatasetItemChunk;
import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.DatasetManager;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.ai.datasets.models.Dataset;
import io.vertigo.ai.impl.AbstractSqlDatasetLoader;
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
	public Dataset<DatasetItem<BostonRegressionItem, BostonRegressionItem>> loadData(
			DatasetItemChunk<BostonRegressionItem> itemChunk, String datasetName) {
		final DatasetDefinition datasetDefinition = datasetManager.findDatasetDefinition(datasetName);
		final DatasetItemDefinition datasetItemDefinition = datasetManager.findFirstDatasetItemDefinitionByKeyConcept(BostonItem.class);
		try (final VTransactionWritable tx = getTransactionManager().createCurrentTransaction()) {
			final Dataset<DatasetItem<BostonRegressionItem, BostonRegressionItem>> result = new Dataset<DatasetItem<BostonRegressionItem, BostonRegressionItem>>(datasetDefinition, new ArrayList<>());
			for (final BostonRegressionItem item : loadItems(itemChunk)) {
				final UID<BostonRegressionItem> uid = item.getUID();
				result.addDatasetItem(DatasetItem.createItem(datasetItemDefinition, uid, item));
			}
			return result;
		}
	}

	private DtList<BostonRegressionItem> loadItems(final DatasetItemChunk<BostonRegressionItem> itemChunk) {
		final TaskDefinition taskDefinition = getTaskDefinition(itemChunk);

		final Task task = Task.builder(taskDefinition)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	private TaskDefinition getTaskDefinition(final DatasetItemChunk<BostonRegressionItem> itemChunk) {
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
