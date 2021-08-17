package io.vertigo.ai.example.heroes;

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
public final class HeroesPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public HeroesPAO(final TaskManager taskManager) {
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
	 * Execute la tache TkTruncateEra.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkTruncateEra",
			request = "TRUNCATE TABLE ERA",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void truncateEra() {
		final Task task = createTaskBuilder("TkTruncateEra")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkTruncateFaction.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkTruncateFaction",
			request = "TRUNCATE TABLE FACTION",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void truncateFaction() {
		final Task task = createTaskBuilder("TkTruncateFaction")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkTruncateHeroes.
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			dataSpace = "train",
			name = "TkTruncateHeroes",
			request = "TRUNCATE TABLE HEROE",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProc.class)
	public void truncateHeroes() {
		final Task task = createTaskBuilder("TkTruncateHeroes")
				.addContextProperty("connectionName", io.vertigo.datastore.impl.dao.StoreUtil.getConnectionName("train"))
				.build();
		getTaskManager().execute(task);
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
