package io.vertigo.ai.datasets.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.ai.datasetItems.models.DatasetItem;
import io.vertigo.ai.datasets.definitions.DatasetDefinition;

/**
 * Objet permettant l'échange entre des entrées dans une bdd (DatasetItems) et une API de prédiction.
 * Cet objet permet de
 *  - construire la liste des objets à prédire
 *  - sérialiser cette même liste
 */
public class Dataset<D extends DatasetItem<?, ?>> implements DtObject {


	private static final long serialVersionUID = 1L;
	private List<DatasetItem<?, ?>> datasetItems;
	
	/**
	 * Constructor
	 * @param datasetDefinition Définition du dataset
	 * @param datasetItems Liste des DatasetItems à 
	 */
	public Dataset(DatasetDefinition datasetDefinition, List<DatasetItem<?, ?>> datasetItems) {
		this.datasetItems = datasetItems;
	}
	
	public void addDatasetItem(DatasetItem<?, ?> datasetItem) {
		datasetItems.add(datasetItem);
	}
	
	public List<DatasetItem<?, ?>> getDatasetItems(){
		return datasetItems;
	}
	
	public List<UID<?>> getDatasetUIDs(){
		List<UID<?>> uids = new ArrayList<UID<?>>();
		for (DatasetItem<?, ?> item : datasetItems) {
			uids.add(item.getUID());
		}
		return uids;
	}
	
	public List<? extends DtObject> getDatasetSerialized(){
		List<? extends DtObject> data = new ArrayList<>();
		data = datasetItems.stream().map(DatasetItem::getItemDtObject).collect(Collectors.toList());
		return data;
	}
}
