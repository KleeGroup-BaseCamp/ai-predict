package io.vertigo.ai.datasets.definitions;

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

/**
 * 
 */
public final class DatasetDefinition extends AbstractDefinition{
    
    private final DtDefinition datasetDtDefinition;
    private final Boolean streamed;
    final String datasetLoaderId;

    /**
     * 
     * @param name
     * @param datasetDtDefinition
     * @param streamed
     * @param datasetLoaderId
     */
    public DatasetDefinition(
        final String name,
        final DtDefinition datasetDtDefinition,
        final Boolean streamed,
        final String datasetLoaderId
        ){
        super(name);
        
        this.datasetDtDefinition = datasetDtDefinition;
        this.streamed = streamed;
        this.datasetLoaderId = datasetLoaderId;
        
    }

    /**
     * 
     * @return
     */
    public DtDefinition getDatasetDtDefinition() {
		return datasetDtDefinition;
	}

    /**
     * 
     * @return
     */
    public String getDatasetLoaderId() {
        return datasetLoaderId;
    }

    public Boolean isStreamed() {
    	return streamed;
    }
    }
