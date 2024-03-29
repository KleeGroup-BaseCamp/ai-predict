package io.vertigo.ai.example.telstra.train;

import javax.inject.Inject;

import io.vertigo.core.node.Node;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datastore.impl.dao.StoreServices;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
 @Generated
public final class TrainPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public TrainPAO(final TaskManager taskManager) {
		Assertion.check().isNotNull(taskManager);
		//-----
		this.taskManager = taskManager;
	}

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Node.getNode().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkRemoveEventTypeTrain.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkRemoveEventTypeTrain",
			request = "TRUNCATE TABLE EVENT_TYPE_TRAIN;",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeEventTypeTrain() {
		final Task task = createTaskBuilder("TkRemoveEventTypeTrain")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkRemoveLocationTrain.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkRemoveLocationTrain",
			request = "TRUNCATE TABLE LOCATION_TRAIN;",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeLocationTrain() {
		final Task task = createTaskBuilder("TkRemoveLocationTrain")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkRemoveLogFeatureTrain.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkRemoveLogFeatureTrain",
			request = "TRUNCATE TABLE LOG_FEATURE_TRAIN;",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeLogFeatureTrain() {
		final Task task = createTaskBuilder("TkRemoveLogFeatureTrain")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkRemoveResourceTypeTrain.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkRemoveResourceTypeTrain",
			request = "TRUNCATE TABLE RESOURCE_TYPE_TRAIN;",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeResourceTypeTrain() {
		final Task task = createTaskBuilder("TkRemoveResourceTypeTrain")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkRemoveSeverityTypeTrain.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkRemoveSeverityTypeTrain",
			request = "TRUNCATE TABLE SEVERITY_TYPE_TRAIN;",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void removeSeverityTypeTrain() {
		final Task task = createTaskBuilder("TkRemoveSeverityTypeTrain")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
