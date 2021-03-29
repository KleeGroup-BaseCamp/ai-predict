package io.vertigo.ai.datasetItems.models;

import io.vertigo.ai.datasetItems.definitions.DatasetItemDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;


public class DatasetItem<K extends KeyConcept, I extends DtObject>{
        
    
    private final DatasetItemDefinition datasetItemDefinition;

    private final UID<K> uid;

    private final I datasetDtObject;
    

    /**
     */
    private DatasetItem(final DatasetItemDefinition datasetItemDefinition, final UID<K> uid, final I datasetDtObject) {
        this.uid = uid;
        this.datasetItemDefinition = datasetItemDefinition;
        this.datasetDtObject = datasetDtObject;
    }

    /**
     */
    public DatasetItemDefinition getDefinition() {
        return datasetItemDefinition;
    }

    /**
     */
    public UID<K> getUID() {
        return uid;
    }

    /**
     */
    public I getDatasetDtObject() {
        return datasetDtObject;
    }

    /**
     */
    public boolean hasDataset() {
        return datasetDtObject != null;
    }
    
    /**
     */
    public static <S extends KeyConcept, I extends DtObject> DatasetItem<S, I> createIndex(final DatasetItemDefinition datasetItemDefinition, final UID<S> uid, final I datasetDto) {
        return new DatasetItem<>(datasetItemDefinition, uid, datasetDto);
    }

}
