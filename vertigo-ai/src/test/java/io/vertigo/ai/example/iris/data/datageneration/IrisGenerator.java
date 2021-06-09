package io.vertigo.ai.example.iris.data.datageneration;

import java.math.BigDecimal;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.example.iris.domain.Iris;
import io.vertigo.ai.utils.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;
import io.vertigo.database.migration.MigrationManager;
import io.vertigo.database.sql.SqlManager;

@Transactional
public class IrisGenerator implements Component { 

	private static final String IRIS_CSV_FILE_PATH = "io/vertigo/ai/datageneration/iris.csv";
	private static final int IRIS_CSV_FILE_COLUMN_NUMBER = 5;
	
	@Inject
	private IrisDAO irisDAO;
	
	@Inject
	private MigrationManager migrationManager;
	
	@Inject
	private ResourceManager resourceManager;

	private void consume(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == IRIS_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Equipment Types", csvFilePath);
		//---
		
		Iris iris = new Iris();
		iris.setPetalLength(new BigDecimal(record[2]));
		iris.setSepalLength(new BigDecimal(record[0]));
		iris.setPetalWidth(new BigDecimal(record[3]));
		iris.setSepalWidth(new BigDecimal(record[1]));
		iris.setVariety(record[4]);

		irisDAO.create(iris);
	}
	
	public void createIrisFromCSV() {
		migrationManager.update(SqlManager.MAIN_CONNECTION_PROVIDER_NAME);
		CSVReaderUtil.parseCSV(resourceManager, IRIS_CSV_FILE_PATH, this::consume);
	}
	
}
