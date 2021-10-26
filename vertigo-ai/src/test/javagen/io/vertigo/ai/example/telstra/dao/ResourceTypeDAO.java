package io.vertigo.ai.example.telstra.dao;

import javax.inject.Inject;

import io.vertigo.core.lang.Generated;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.task.definitions.TaskDefinition;
import io.vertigo.datamodel.task.model.Task;
import io.vertigo.datamodel.task.model.TaskBuilder;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.entitystore.EntityStoreManager;
import io.vertigo.datastore.impl.dao.DAO;
import io.vertigo.datastore.impl.dao.StoreServices;
import io.vertigo.datamodel.smarttype.SmartTypeManager;
import io.vertigo.datamodel.task.TaskManager;
import io.vertigo.ai.example.telstra.domain.ResourceType;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ResourceTypeDAO extends DAO<ResourceType, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param entityStoreManager Manager de persistance
	 * @param taskManager Manager de Task
	 * @param smartTypeManager SmartTypeManager
	 */
	@Inject
	public ResourceTypeDAO(final EntityStoreManager entityStoreManager, final TaskManager taskManager, final SmartTypeManager smartTypeManager) {
		super(ResourceType.class, entityStoreManager, taskManager, smartTypeManager);
	}

	/**
	 * Indique que le keyConcept associé à cette UID va être modifié.
	 * Techniquement cela interdit les opérations d'ecriture en concurrence
	 * et envoie un évenement de modification du keyConcept (à la fin de transaction eventuellement)
	 * @param UID UID du keyConcept modifié
	 * @return KeyConcept à modifier
	 */
	public ResourceType readOneForUpdate(final UID<ResourceType> uid) {
		return entityStoreManager.readOneForUpdate(uid);
	}

	/**
	 * Indique que le keyConcept associé à cet id va être modifié.
	 * Techniquement cela interdit les opérations d'ecriture en concurrence
	 * et envoie un évenement de modification du keyConcept (à la fin de transaction eventuellement)
	 * @param id Clé du keyConcept modifié
	 * @return KeyConcept à modifier
	 */
	public ResourceType readOneForUpdate(final java.lang.Long id) {
		return readOneForUpdate(createDtObjectUID(id));
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
	 * Execute la tache TkBulkCreateResourceType.
	 * @param resourceTypeList DtList de ResourceType
	*/
	@io.vertigo.datamodel.task.proxy.TaskAnnotation(
			name = "TkBulkCreateResourceType",
			request = "INSERT INTO RESOURCE_TYPE (ID, CODE, RESOURCE_TYPE) values (nextval('SEQ_RESOURCE_TYPE'), #resourceTypeList.code#, #resourceTypeList.resourceType#)",
			taskEngineClass = io.vertigo.basics.task.TaskEngineProcBatch.class)
	public void bulkCreateResourceType(@io.vertigo.datamodel.task.proxy.TaskInput(name = "resourceTypeList", smartType = "STyDtResourceType") final io.vertigo.datamodel.structure.model.DtList<io.vertigo.ai.example.telstra.domain.ResourceType> resourceTypeList) {
		final Task task = createTaskBuilder("TkBulkCreateResourceType")
				.addValue("resourceTypeList", resourceTypeList)
				.build();
		getTaskManager().execute(task);
	}

}
