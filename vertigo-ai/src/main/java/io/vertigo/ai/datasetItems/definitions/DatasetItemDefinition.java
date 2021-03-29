package io.vertigo.ai.datasetItems.definitions;

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

/**
 * 
 */
@DefinitionPrefix(DatasetItemDefinition.PREFIX)
public final class DatasetItemDefinition extends AbstractDefinition{
    
	public static final String PREFIX = "DsI";
	
    private final DtDefinition datasetDtDefinition;
    private final Boolean streamed;
    final String datasetLoaderId;
    private final DtDefinition keyConceptDtDefinition;

    /**
     * 
     * @param name
     * @param datasetDtDefinition
     * @param streamed
     * @param datasetLoaderId
     */
    public DatasetItemDefinition(
        final String name,
        final DtDefinition datasetDtDefinition,
        final Boolean streamed,
        final String datasetLoaderId,
        final DtDefinition keyConceptDtDefinition
        ){
        super(name);
        
        this.datasetDtDefinition = datasetDtDefinition;
        this.streamed = streamed;
        this.datasetLoaderId = datasetLoaderId;
        this.keyConceptDtDefinition = keyConceptDtDefinition;
        
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
    
    public DtDefinition getKeyConceptDtDefinition() {
		return keyConceptDtDefinition;
	}
    
    }
