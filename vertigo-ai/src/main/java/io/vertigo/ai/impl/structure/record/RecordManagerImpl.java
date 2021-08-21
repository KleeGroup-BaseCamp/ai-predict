/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2021, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertigo.ai.impl.structure.record;

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

import io.vertigo.ai.structure.record.RecordManager;
import io.vertigo.ai.structure.record.definitions.RecordDefinition;
import io.vertigo.ai.structure.record.models.Record;
import io.vertigo.commons.eventbus.EventBusSubscribed;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.datafactory.impl.search.WritableFuture;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtStereotype;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.StoreEvent;

/**
 * Implémentation standard du gestionnaire des records.
 */
public final class RecordManagerImpl implements RecordManager, Activeable{

	private static final String CATEGORY = "record";
	private final AnalyticsManager analyticsManager;

	private final ScheduledExecutorService executorService; //TODO : replace by WorkManager to make distributed work easier
	private final Map<String, Set<UID<? extends KeyConcept>>> dirtyElementsPerIndexName = new HashMap<>();
	
	/**
	 * Constructor.
	 * @param localeManager the localeManager
	 * @param analyticsManager the analyticsManager
	 */
	@Inject
	public RecordManagerImpl(
			final LocaleManager localeManager,
			final AnalyticsManager analyticsManager) {
		Assertion.check()
				//.isNotNull(recordServicesPlugin)
				.isNotNull(analyticsManager);
		//-----
		//this.recordServicesPlugin = recordServicesPlugin;
		this.analyticsManager = analyticsManager;

		executorService = Executors.newSingleThreadScheduledExecutor();
	}
	
	/** {@inheritDoc} */
	@Override
	public void start() {

		for (final RecordDefinition recordDefinition : Node.getNode().getDefinitionSpace().getAll(RecordDefinition.class)) {
			final Set<UID<? extends KeyConcept>> dirtyElements = new LinkedHashSet<>();
			dirtyElementsPerIndexName.put(recordDefinition.getName(), dirtyElements);
			executorService.scheduleWithFixedDelay(new RefreshDataTask(recordDefinition, dirtyElements), 1, 1, TimeUnit.SECONDS); //on dépile les dirtyElements toutes les 1 secondes
		}
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		for (final RecordDefinition recordDefinition : Node.getNode().getDefinitionSpace().getAll(RecordDefinition.class)) {
			final Set<UID<? extends KeyConcept>> dirtyElements = new LinkedHashSet<>();
			dirtyElementsPerIndexName.put(recordDefinition.getName(), dirtyElements);
			executorService.scheduleWithFixedDelay(new RefreshDataTask(recordDefinition, dirtyElements), 1, 1, TimeUnit.SECONDS); //on dépile les dirtyElements toutes les 1 secondes
		}
		executorService.shutdown();		
	}

	/** {@inheritDoc} */
	@Override
	public RecordDefinition findFirstRecordDefinitionByKeyConcept(
			Class<? extends KeyConcept> keyConceptClass) {
		final Optional<RecordDefinition> recordDefinition = findFirstIndexDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(recordDefinition.isPresent(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return recordDefinition.get();
	}
	
	private static Optional<RecordDefinition> findFirstIndexDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(RecordDefinition.class).stream()
				.filter(recordDefinition -> recordDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.findFirst();
	}

	/** {@inheritDoc} */
	@Override
	public void markAsDirty(List<UID<? extends KeyConcept>> keyConceptUIDs) {
		Assertion.check()
		.isNotNull(keyConceptUIDs)
		.isFalse(keyConceptUIDs.isEmpty(), "dirty keyConceptUris cant be empty");
		//-----
		final DtDefinition keyConceptDefinition = keyConceptUIDs.get(0).getDefinition();
		final List<RecordDefinition> recordDefinitions = findIndexDefinitionByKeyConcept(keyConceptDefinition);
		Assertion.check().isFalse(recordDefinitions.isEmpty(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptDefinition.getName());
		//-----
		for (final RecordDefinition recordDefinition : recordDefinitions) {
			final Set<UID<? extends KeyConcept>> dirtyElements = dirtyElementsPerIndexName.get(recordDefinition.getName());
			RefreshDataTask task = new RefreshDataTask(recordDefinition, dirtyElements);
			task.run();
			synchronized (dirtyElements) {
				dirtyElements.addAll(keyConceptUIDs);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Future<Long> refreshAll(RecordDefinition recordDefinition) {
		final WritableFuture<Long> refreshFuture = new WritableFuture<>();
		executorService.schedule(new RefreshAllTrainDataTask(recordDefinition, refreshFuture/*, this*/), 5, TimeUnit.SECONDS); //une rerecordation total dans max 5s
		return refreshFuture;
	}

	/** {@inheritDoc} */
	@Override
	public <K extends KeyConcept, I extends DtObject> void putAll(
			RecordDefinition recordDefinition,
			Set<UID<? extends KeyConcept>> uids) {
	}

	/** {@inheritDoc} */
	@Override
	public <K extends KeyConcept, I extends DtObject> void put(
			RecordDefinition recordDefinition, Record<K, I> record) {
		analyticsManager.trace(
				CATEGORY,
				"/put/" + recordDefinition.getName(),
				tracer -> {
					//recordServicesPlugin.put(recordDefinition, record);
					tracer.setMeasure("nbModifiedRow", 1);
				});
		
	}
	
	/** {@inheritDoc} */
	@Override
	public long count(RecordDefinition recordDefinition) {
		return analyticsManager.traceWithReturn(
				CATEGORY,
				"/count/" + recordDefinition.getName(),
				tracer -> {
					//recordServicesPlugin.count(recordDefinition);
					return 0l;
				});
	}

	/** {@inheritDoc} */
	@Override
	public <K extends KeyConcept> void remove(
			RecordDefinition recordDefinition, UID<K> uid) {
		analyticsManager.trace(
				CATEGORY,
				"/remove/" + recordDefinition.getName(),
				tracer -> {
					//recordServicesPlugin.remove(recordDefinition, uid);
					tracer.setMeasure("nbModifiedRow", 1);
				});
		
	}
	
	private static boolean hasIndexDefinitionByKeyConcept(final DtDefinition keyConceptDefinition) {
		final List<RecordDefinition> recordDefinitions = findRecordDefinitionByKeyConcept(keyConceptDefinition);
		return !recordDefinitions.isEmpty();
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

	/** {@inheritDoc} */
	@Override
	public RecordDefinition findRecordDefinitionByKeyConcept(
			Class<? extends KeyConcept> keyConceptClass) {
		final Optional<RecordDefinition> recordDefinition = findFirstIndexDefinitionByKeyConcept(DtObjectUtil.findDtDefinition(keyConceptClass));
		Assertion.check().isTrue(recordDefinition.isPresent(), "No SearchIndexDefinition was defined for this keyConcept : {0}", keyConceptClass.getSimpleName());
		return recordDefinition.get();
	}
	
	private static List<RecordDefinition> findRecordDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(RecordDefinition.class).stream()
				.filter(recordDefinition -> recordDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.collect(Collectors.toList());
	}

	/** {@inheritDoc} */
	@Override
	public void removeAll(RecordDefinition recordDefinition) {
		analyticsManager.trace(
				CATEGORY,
				"/removeAll/" + recordDefinition.getName(),
				tracer -> {
					//recordServicesPlugin.remove(recordDefinition, listFilter);
				});
		
	}
	
	private static List<RecordDefinition> findIndexDefinitionByKeyConcept(final DtDefinition keyConceptDtDefinition) {
		return Node.getNode().getDefinitionSpace().getAll(RecordDefinition.class).stream()
				.filter(recordDefinition -> recordDefinition.getKeyConceptDtDefinition().equals(keyConceptDtDefinition))
				.collect(Collectors.toList());
	}
	
}
