package io.vertigo.ai.example.heroes;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.example.domain.DtDefinitions.HeroeFields;
import io.vertigo.ai.example.heroes.dao.FactionCountDAO;
import io.vertigo.ai.example.heroes.dao.FactionDAO;
import io.vertigo.ai.example.heroes.dao.HeroeDAO;
import io.vertigo.ai.example.heroes.data.datageneration.HeroesGenerator;
import io.vertigo.ai.example.heroes.domain.Era;
import io.vertigo.ai.example.heroes.domain.Faction;
import io.vertigo.ai.example.heroes.domain.FactionCount;
import io.vertigo.ai.example.heroes.domain.Heroe;
import io.vertigo.ai.impl.structure.dataset.DatasetImpl;
import io.vertigo.ai.mlmodel.ModelManager;
import io.vertigo.ai.predict.data.domain.boston.BostonRegressionItem;
import io.vertigo.ai.structure.dataset.Dataset;
import io.vertigo.ai.structure.dataset.DatasetManager;
import io.vertigo.ai.structure.dataset.definitions.DatasetDefinition;
import io.vertigo.ai.structure.processor.Agregator;
import io.vertigo.ai.structure.processor.Processor;
import io.vertigo.ai.structure.processor.ProcessorBuilder;
import io.vertigo.ai.structure.processor.SortOrder;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtFieldName;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.entitystore.EntityStoreManager;

public abstract class AbstractHeroesTestManager {

	private DatasetDefinition datasetDefinition;
	
	@Inject
	private HeroesGenerator heroesGenerator;
	
	@Inject
	private DatasetManager datasetManager;

	@Inject
	private EntityStoreManager entityStoreManager;
	
	@Inject
	private VTransactionManager transactionManager;
	
	@Inject
	private ModelManager modelManager;
	
	@Inject
	private HeroesPAO heroesPAO;
	
	@Inject
	private FactionCountDAO factionCountDAO;
		
	private AutoCloseableNode node;
	
	private DtDefinition heroeDtDefinition; 
	private DtDefinition factionDtDefinition;
	private DtDefinition factionCountDtDefinition;
	
	
	
	protected final void init(final String datasetName) {
		final DefinitionSpace definitionSpace = node.getDefinitionSpace();

		datasetDefinition = definitionSpace.resolve(datasetName, DatasetDefinition.class);
		heroeDtDefinition = DtObjectUtil.findDtDefinition(Heroe.class);
		factionDtDefinition = DtObjectUtil.findDtDefinition(Faction.class);
		factionCountDtDefinition = DtObjectUtil.findDtDefinition(FactionCount.class);
	}
	
	protected abstract void doSetUp();

	protected abstract NodeConfig buildNodeConfig();
	
	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
		//--
		doSetUp();
		heroesGenerator.createHeroesFromCSV();
	}
	
	@AfterEach
	public final void tearDown() {
		if (node != null) {
			try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
				heroesPAO.truncateFaction();
				heroesPAO.truncateEra();
				heroesPAO.truncateHeroes();
				transaction.commit();
			};
			
			node.close();
		}
	}
	
	@Test
	public void testProcessing() {
		Dataset<Heroe> heroes = new DatasetImpl<Heroe>(heroeDtDefinition);
		Dataset<Faction> factions = new DatasetImpl<Faction>(factionDtDefinition);
		Dataset<FactionCount> factionCount = new DatasetImpl<FactionCount>(factionCountDtDefinition);
		ProcessorBuilder processorBuilder = datasetManager.createBuilder();
		final Criteria<Heroe> criteria = Criterions.isEqualTo(HeroeFields.faction, 1);
		List<Processor> processors = processorBuilder.filter(criteria).select("heroeName,faction").join(factions, "factionId", "faction").groupBy("factionName", Agregator.COUNT).build();
		FactionCount factionCountItem;
		try (VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			datasetManager.executeProcessing(heroes, factionCount, processors);
			factionCountItem = factionCountDAO.get(1);
			transaction.commit();
		};
		
		
		Assertions.assertEquals(4, factionCountItem.getCountFactionName());
		Assertions.assertEquals("Old Republic", factionCountItem.getFactionName());
		
	}

}
		