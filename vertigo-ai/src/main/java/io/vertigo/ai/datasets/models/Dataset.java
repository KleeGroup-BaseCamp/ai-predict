package io.vertigo.ai.datasets.models;

import io.vertigo.ai.datasets.definitions.DatasetDefinition;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.datamodel.structure.model.UID;


public class Dataset<K extends KeyConcept, I extends DtObject> {
        
    
    private final DatasetDefinition datasetDefinition;

    private final UID<K> uid;

    private final I datasetDtObject;

    /**
     */
    private Dataset(final DatasetDefinition datasetDefinition, final UID<K> uid, final I datasetDtObject) {
        this.uid = uid;
        this.datasetDefinition = datasetDefinition;
        this.datasetDtObject = datasetDtObject;
    }

    /**
     */
    public DatasetDefinition getDefinition() {
        return datasetDefinition;
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
    public static <S extends KeyConcept, I extends DtObject> Dataset<S, I> createIndex(final DatasetDefinition datasetDefinition, final UID<S> uid, final I datasetDto) {
        return new Dataset<>(datasetDefinition, uid, datasetDto);
    }

}
