package io.vertigo.ai.datasets.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;

public class Dataset<D extends DatasetItem> implements DtObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DatasetDefinition datasetDefinition;
	private List<DatasetItem> dataset;
	
	public Dataset(DatasetDefinition datasetDefinition, List<DatasetItem> dataset) {
		this.dataset = dataset;
		this.datasetDefinition = datasetDefinition;
	}
	
	public void addDatasetItem(DatasetItem datasetItem) {
		dataset.add(datasetItem);
	}
	
	public List<DatasetItem> getDatasetItems(){
		return dataset;
	}
	
	public List<UID> getDatasetUIDs(){
		List<UID> uids = new ArrayList<UID>();
		for (DatasetItem item : dataset) {
			uids.add(item.getUID());
		}
		return uids;
	}
	
	public List<? extends DtObject> getDatasetSerialized(){
		List<? extends DtObject> data = new ArrayList<>();
		data = dataset.stream().map(DatasetItem::getDatasetDtObject).collect(Collectors.toList());
		return data;
	}
}
