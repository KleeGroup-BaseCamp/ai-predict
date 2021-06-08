package io.vertigo.ai.example.iris.data.datageneration;

import java.math.BigDecimal;

import javax.inject.Inject;

import io.vertigo.ai.example.iris.IrisPAO;
import io.vertigo.ai.example.iris.dao.IrisDAO;
import io.vertigo.ai.utils.CSVReaderUtil;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.resource.ResourceManager;

@Transactional
public class IrisGenerator implements Component{ 

	private static final String IRIS_CSV_FILE_PATH = "io/vertigo/ai/datageneration/iris.csv";
	private static final int IRIS_CSV_FILE_COLUMN_NUMBER = 5;
	
	@Inject
	private IrisPAO irisPAO;
	
	@Inject
	private ResourceManager resourceManager;

	private void consume(final String csvFilePath, final String[] record) {
		Assertion.check().isTrue(record.length == IRIS_CSV_FILE_COLUMN_NUMBER,
				"CSV File {0} Format not suitable for Equipment Types", csvFilePath);
		//---
		final Double sepalLength = Double.valueOf(record[0]);
		final Double sepalWidth = Double.valueOf(record[1]);
		final Double petalLength = Double.valueOf(record[2]);
		final Double petalWidth = Double.valueOf(record[3]);
		final String variety = record[4];
		
		irisPAO.createIris(sepalLength, sepalWidth, petalLength, petalWidth, variety);
	}
	
	public void createIrisFromCSV() {
		System.out.println(resourceManager);
		System.out.println(irisPAO);
		CSVReaderUtil.parseCSV(resourceManager, IRIS_CSV_FILE_PATH, this::consume);
	}
	
}
