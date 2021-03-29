package io.vertigo.ai.datasetItems.models;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;

/**
 * Objet d'échange avec le dataset.
 */
public class DatasetItem<K extends KeyConcept, I extends DtObject>{
        
    
    private final DatasetItemDefinition datasetItemDefinition;

    private final UID<K> uid;

    private final I itemDtObject;
    

	/**
	 * Constructor.
	 * @param datasetItemDefinition definition de O, I
	 * @param uid UID de l'objet indexé
	 * @param datasetDtObject objet attaché à l'item
	 */
    private DatasetItem(final DatasetItemDefinition datasetItemDefinition, final UID<K> uid, final I itemDtObject) {
        this.uid = uid;
        this.datasetItemDefinition = datasetItemDefinition;
        this.itemDtObject = itemDtObject;
    }

	/**
	 * @return Définition de l'item.
	 */
    public DatasetItemDefinition getDefinition() {
        return datasetItemDefinition;
    }

	/**
	 * @return UID de l'item.
	 */
    public UID<K> getUID() {
        return uid;
    }

	/**
	 * @return Objet contenant les champs de l'item
	 */
    public I getItemDtObject() {
        return itemDtObject;
    }

    /**
     */
    public boolean hasItem() {
        return itemDtObject != null;
    }
    
	/**
	 * Constructeur de l'Objet permettant de créer un nouvel item.
	 * @param <I> Type de l'objet représentant l'item
	 * @param uid UID de l'item
	 * @param datasetItemDefinition Définition de l'item.
	 * @param itemDtObject  DTO représentant l'item
	 * @return  Objet permettant de créer l'item
	 */
    public static <S extends KeyConcept, I extends DtObject> DatasetItem<S, I> createItem(final DatasetItemDefinition datasetItemDefinition, final UID<S> uid, final I itemDtObject) {
        return new DatasetItem<S, I>(datasetItemDefinition, uid, itemDtObject);
    }

}
