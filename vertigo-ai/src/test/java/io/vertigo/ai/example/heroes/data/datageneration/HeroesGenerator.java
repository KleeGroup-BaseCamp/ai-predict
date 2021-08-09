package io.vertigo.ai.example.heroes.data.datageneration;

import javax.inject.Inject;

import io.vertigo.ai.example.heroes.dao.EraDAO;
import io.vertigo.ai.example.heroes.dao.FactionDAO;
import io.vertigo.ai.example.heroes.dao.HeroeDAO;
import io.vertigo.ai.example.heroes.domain.Era;
import io.vertigo.ai.example.heroes.domain.Faction;
import io.vertigo.ai.example.heroes.domain.Heroe;
import io.vertigo.ai.utils.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.database.migration.MigrationManager;
import io.vertigo.database.sql.SqlManager;


@Transactional
public class HeroesGenerator implements Component {
	
	private static final String HEROES_CSV_FILE_PATH = "io/vertigo/ai/example/heroes/datageneration/heroes.csv";
	private static final String ERAS_CSV_FILE_PATH = "io/vertigo/ai/example/heroes/datageneration/eras.csv";
	private static final String FACTIONS_CSV_FILE_PATH = "io/vertigo/ai/example/heroes/datageneration/factions.csv";
	
	private static final int HEROES_CSV_FILE_COLUMN_NUMBER = 4;
	private static final int FACTIONS_CSV_FILE_COLUMN_NUMBER = 3;
	private static final int ERAS_CSV_FILE_COLUMN_NUMBER = 2;
	
	@Inject
	private HeroeDAO heroeDAO;
	
	@Inject
	private EraDAO eraDAO;
	
	@Inject
	private FactionDAO factionDAO;
	
	@Inject
	private MigrationManager migrationManager;
	
	@Inject
	private ResourceManager resourceManager;

	private void consumeHeroe(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == HEROES_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Heroe Types", csvFilePath);
		//---
		
		Heroe heroe = new Heroe();
		heroe.setName(record[1]);
		heroe.setJob(record[2]);
		heroe.setFaction(1000+Integer.parseInt(record[3]));

		heroeDAO.create(heroe);
	}
	
	private void consumeEra(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == ERAS_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Era Types", csvFilePath);
		//---
		
		Era era = new Era();
		era.setName(record[1]);

		eraDAO.create(era);
	}
	
	private void consumeFaction(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == FACTIONS_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Faction Types", csvFilePath);
		//---
		
		Faction faction = new Faction();
		faction.setName(record[1]);
		faction.setEra(1000+Integer.parseInt(record[2]));

		factionDAO.create(faction);
	}
	
	public void createHeroesFromCSV() {
		migrationManager.update(SqlManager.MAIN_CONNECTION_PROVIDER_NAME);
		CSVReaderUtil.parseCSV(resourceManager, HEROES_CSV_FILE_PATH, this::consumeHeroe);
		CSVReaderUtil.parseCSV(resourceManager, FACTIONS_CSV_FILE_PATH, this::consumeFaction);
		CSVReaderUtil.parseCSV(resourceManager, ERAS_CSV_FILE_PATH, this::consumeEra);
	}
	
}
