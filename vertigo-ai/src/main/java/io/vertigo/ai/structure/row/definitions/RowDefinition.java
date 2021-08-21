package io.vertigo.ai.structure.row.definitions;

import io.vertigo.core.node.definition.AbstractDefinition;
import io.vertigo.core.node.definition.DefinitionPrefix;
import io.vertigo.datamodel.structure.definitions.DtDefinition;

/**
 * Définition de l'item d'un dataset.
 *
 * Une Row est constitué de deux types d'objets.
 * - Un objet (les champs indexés)
 * - Un keyConcept représentant le concept métier réprésenté par cet item.
 */

@DefinitionPrefix(RowDefinition.PREFIX)
public final class RowDefinition extends AbstractDefinition {

	public static final String PREFIX = "DsR";
	
	private final DtDefinition rowDtDefinition;
    final String datasetLoaderId;
    private final DtDefinition keyConceptDtDefinition;

    /**
     * Constructor
     * @param name
     * @param itemDtDefinition
     * @param streamed
     * @param datasetLoaderId
     */
    public RowDefinition(
        final String name,
        final DtDefinition rowDtDefinition,
        final String datasetLoaderId,
        final DtDefinition keyConceptDtDefinition
        ){
        super(name);
        
        this.rowDtDefinition = rowDtDefinition;
        this.datasetLoaderId = datasetLoaderId;
        this.keyConceptDtDefinition = keyConceptDtDefinition;
        
    }

    /**
     * @return Définition de l'objet représentant le contenu de l'item.
     */
    public DtDefinition getRowDtDefinition() {
		return rowDtDefinition;
	}

    /**
	 * @return Nom du composant de chargement de l'item.
     */
    public String getDatasetLoaderId() {
        return datasetLoaderId;
    }

	/**
	 * @return Définition du keyConcept de l'item.
	 */
    public DtDefinition getKeyConceptDtDefinition() {
		return keyConceptDtDefinition;
	}
}
