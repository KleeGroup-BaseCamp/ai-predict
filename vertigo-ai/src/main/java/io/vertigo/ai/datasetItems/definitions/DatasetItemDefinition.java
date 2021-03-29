package io.vertigo.ai.datasetItems.definitions;

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

/**
 * Définition de l'item d'un dataset.
 *
 * Un DatasetItem est constitué de deux types d'objets.
 * - Un objet (les champs indexés)
 * - Un keyConcept représentant le concept métier réprésenté par cet item.
 * La définition d'item précise également un DatasetItemLoader permettant la mise à jour autonome de l'item.
 *
 */
@DefinitionPrefix(DatasetItemDefinition.PREFIX)
public final class DatasetItemDefinition extends AbstractDefinition{
    
	public static final String PREFIX = "DsI";
	
    private final DtDefinition itemDtDefinition;
    private final Boolean streamed;
    final String datasetLoaderId;
    private final DtDefinition keyConceptDtDefinition;

    /**
     * Constructor
     * @param name
     * @param itemDtDefinition
     * @param streamed
     * @param datasetLoaderId
     */
    public DatasetItemDefinition(
        final String name,
        final DtDefinition itemDtDefinition,
        final Boolean streamed,
        final String datasetLoaderId,
        final DtDefinition keyConceptDtDefinition
        ){
        super(name);
        
        this.itemDtDefinition = itemDtDefinition;
        this.streamed = streamed;
        this.datasetLoaderId = datasetLoaderId;
        this.keyConceptDtDefinition = keyConceptDtDefinition;
        
    }

    /**
     * @return Définition de l'objet représentant le contenu de l'item.
     */
    public DtDefinition getDatasetItemDtDefinition() {
		return itemDtDefinition;
	}

    /**
	 * @return Nom du composant de chargement de l'item.
     */
    public String getDatasetLoaderId() {
        return datasetLoaderId;
    }

    /**
     * @return True si l'item est streamé, false sinon
     */
    public Boolean isStreamed() {
    	return streamed;
    }
    
	/**
	 * @return Définition du keyConcept de l'item.
	 */
    public DtDefinition getKeyConceptDtDefinition() {
		return keyConceptDtDefinition;
	}
    
    }
