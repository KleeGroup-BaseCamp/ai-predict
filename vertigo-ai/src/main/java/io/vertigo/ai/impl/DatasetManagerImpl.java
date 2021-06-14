package io.vertigo.ai.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.structure.record.DatasetManager;
import io.vertigo.ai.structure.record.definitions.DatasetDefinition;
import io.vertigo.ai.structure.record.models.Dataset;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.datafactory.collections.ListFilter;
import io.vertigo.datafactory.impl.search.WritableFuture;
import io.vertigo.datafactory.search.definitions.SearchIndexDefinition;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtStereotype;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.StoreEvent;

public final class DatasetManagerImpl implements DatasetManager, Activeable{

	private static final String CATEGORY = "dataset";
	private final AnalyticsManager analyticsManager;
	//private final DatasetServicesPlugin datasetServicesPlugin;

	private final ScheduledExecutorService executorService; //TODO : replace by WorkManager to make distributed work easier
	private final Map<String, Set<UID<? extends KeyConcept>>> dirtyElementsPerIndexName = new HashMap<>();
	
	/**
	 * Constructor.
	 * @param datasetServicesPlugin the datasetServicesPlugin
	 * @param localeManager the localeManager
	 * @param analyticsManager the analyticsManager
	 */
	@Inject
	public DatasetManagerImpl(
			/*final DatasetServicesPlugin datasetServicesPlugin,*/
			final LocaleManager localeManager,
			final AnalyticsManager analyticsManager) {
		Assertion.check()
				//.isNotNull(datasetServicesPlugin)
				.isNotNull(analyticsManager);
		//-----
		//this.datasetServicesPlugin = datasetServicesPlugin;
		this.analyticsManager = analyticsManager;

		executorService = Executors.newSingleThreadScheduledExecutor();
	}
	
	/** {@inheritDoc} */
	@Override
	public void start() {

		for (final DatasetDefinition recordDefinition : Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class)) {
			final Set<UID<? extends KeyConcept>> dirtyElements = new LinkedHashSet<>();
			dirtyElementsPerIndexName.put(recordDefinition.getName(), dirtyElements);
			executorService.scheduleWithFixedDelay(new RefreshTask(recordDefinition, dirtyElements), 1, 1, TimeUnit.SECONDS); //on dépile les dirtyElements toutes les 1 secondes
		}
	}

	@Override
	public void stop() {
		for (final DatasetDefinition recordDefinition : Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class)) {
			final Set<UID<? extends KeyConcept>> dirtyElements = new LinkedHashSet<>();
			dirtyElementsPerIndexName.put(recordDefinition.getName(), dirtyElements);
			executorService.scheduleWithFixedDelay(new RefreshTask(recordDefinition, dirtyElements), 1, 1, TimeUnit.SECONDS); //on dépile les dirtyElements toutes les 1 secondes
		}
		executorService.shutdown();		
	}

	@Override
	public DatasetDefinition findFirstDatasetDefinitionByKeyConcept(
			Class<? extends KeyConcept> keyConceptClass) {
		final Optional<DatasetDefinition> datasetDefinition = findFirstIndexDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(datasetDefinition.isPresent(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return datasetDefinition.get();
	}
	
	private static Optional<DatasetDefinition> findFirstIndexDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class).stream()
				.filter(datasetDefinition -> datasetDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.findFirst();
	}

	@Override
	public void markAsDirty(List<UID<? extends KeyConcept>> keyConceptUIDs) {
		Assertion.check()
		.isNotNull(keyConceptUIDs)
		.isFalse(keyConceptUIDs.isEmpty(), "dirty keyConceptUris cant be empty");
		//-----
		final DtDefinition keyConceptDefinition = keyConceptUIDs.get(0).getDefinition();
		final List<DatasetDefinition> recordDefinitions = findIndexDefinitionByKeyConcept(keyConceptDefinition);
		Assertion.check().isFalse(recordDefinitions.isEmpty(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptDefinition.getName());
		//-----
		for (final DatasetDefinition recordDefinition : recordDefinitions) {
			final Set<UID<? extends KeyConcept>> dirtyElements = dirtyElementsPerIndexName.get(recordDefinition.getName());
			RefreshTask task = new RefreshTask(recordDefinition, dirtyElements);
			task.run();
			synchronized (dirtyElements) {
				dirtyElements.addAll(keyConceptUIDs);
			}
		}
	}

	@Override
	public Future<Long> refreshAll(DatasetDefinition datasetDefinition) {
		final WritableFuture<Long> refreshFuture = new WritableFuture<>();
		executorService.schedule(new RefreshAllTrainDataTask(datasetDefinition, refreshFuture/*, this*/), 5, TimeUnit.SECONDS); //une reindexation total dans max 5s
		return refreshFuture;
	}

	@Override
	public <K extends KeyConcept, I extends DtObject> void putAll(
			DatasetDefinition datasetDefinition,
			Set<UID<? extends KeyConcept>> uids) {
	}

	@Override
	public <K extends KeyConcept, I extends DtObject> void put(
			DatasetDefinition datasetDefinition, Dataset<K, I> dataset) {
		analyticsManager.trace(
				CATEGORY,
				"/put/" + datasetDefinition.getName(),
				tracer -> {
					//datasetServicesPlugin.put(datasetDefinition, dataset);
					tracer.setMeasure("nbModifiedRow", 1);
				});
		
	}

	@Override
	public long count(DatasetDefinition datasetDefinition) {
		return analyticsManager.traceWithReturn(
				CATEGORY,
				"/count/" + datasetDefinition.getName(),
				tracer -> {
					//datasetServicesPlugin.count(datasetDefinition);
					return 0l;
				});
	}

	@Override
	public <K extends KeyConcept> void remove(
			DatasetDefinition datasetDefinition, UID<K> uid) {
		analyticsManager.trace(
				CATEGORY,
				"/remove/" + datasetDefinition.getName(),
				tracer -> {
					//datasetServicesPlugin.remove(datasetDefinition, uid);
					tracer.setMeasure("nbModifiedRow", 1);
				});
		
	}
	
	private static boolean hasIndexDefinitionByKeyConcept(final DtDefinition keyConceptDefinition) {
		final List<DatasetDefinition> indexDefinitions = findDatasetDefinitionByKeyConcept(keyConceptDefinition);
		return !indexDefinitions.isEmpty();
	}

	
	/**
	 * Receive Store event.
	 * @param storeEvent Store event
	 */
	@EventBusSubscribed
	public void onEvent(final StoreEvent storeEvent) {
		final UID uid = storeEvent.getUID();
		//On ne traite l'event que si il porte sur un KeyConcept
		if (uid.getDefinition().getStereotype() == DtStereotype.KeyConcept
				&& hasIndexDefinitionByKeyConcept(uid.getDefinition())) {
			final List<UID<? extends KeyConcept>> list = Collections.singletonList(uid);
			markAsDirty(list);
		}
		
	}

	@Override
	public DatasetDefinition findDatasetDefinitionByKeyConcept(
			Class<? extends KeyConcept> keyConceptClass) {
		final Optional<DatasetDefinition> datasetDefinition = findFirstIndexDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(datasetDefinition.isPresent(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return datasetDefinition.get();
	}
	
	private static List<DatasetDefinition> findDatasetDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class).stream()
				.filter(indexDefinition -> indexDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.collect(Collectors.toList());
	}

	@Override
	public void removeAll(DatasetDefinition indexDefinition) {
		analyticsManager.trace(
				CATEGORY,
				"/removeAll/" + indexDefinition.getName(),
				tracer -> {
					//datasetServicesPlugin.remove(indexDefinition, listFilter);
				});
		
	}
	
	private static List<DatasetDefinition> findIndexDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(DatasetDefinition.class).stream()
				.filter(recordDefinition -> recordDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.collect(Collectors.toList());
	}
	
}
