package io.vertigo.ai.example.telstra;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.structure.dataset.definitions.DatasetDefinitionSupplier;
import io.vertigo.ai.structure.record.definitions.RecordDefinitionSupplier;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionProvider;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.DefinitionSupplier;

public class TelstraDefinitionProvider implements Component, DefinitionProvider {

	@Override
	public List<DefinitionSupplier> get(DefinitionSpace definitionSpace) {
		List<DefinitionSupplier> definitions = new ArrayList<DefinitionSupplier>();

		definitions.add(new RecordDefinitionSupplier("DsLocation")
							.withKeyConcept("DtLocation")
							.withDatasetDtDefinition("DtLocationTrain")
							.withLoaderId("telstraLocationRecordLoader"));

		definitions.add(new RecordDefinitionSupplier("DsEventType")
				.withKeyConcept("DtEventType")
				.withDatasetDtDefinition("DtEventTypeTrain")
				.withLoaderId("telstraEventTypeRecordLoader"));
		
		definitions.add(new RecordDefinitionSupplier("DsResourceType")
				.withKeyConcept("DtResourceType")
				.withDatasetDtDefinition("DtResourceTypeTrain")
				.withLoaderId("telstraResourceTypeRecordLoader"));
		
		definitions.add(new RecordDefinitionSupplier("DsSeverityType")
				.withKeyConcept("DtSeverityType")
				.withDatasetDtDefinition("DtSeverityTypeTrain")
				.withLoaderId("telstraSeverityTypeRecordLoader"));
		
		definitions.add(new RecordDefinitionSupplier("DsLogFeature")
				.withKeyConcept("DtLogFeature")
				.withDatasetDtDefinition("DtLogFeatureTrain")
				.withLoaderId("telstraLogFeatureRecordLoader"));
		
		return definitions;
	}

}
