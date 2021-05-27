package io.vertigo.ai.example.data.models;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Train implements DtObject {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String location;
	private String logFeature;
	private String severityType;
	private Double volume;
	private Double volumeMean;
	private Double volumeSum;
	private Double volumeMin;
	private Double volumeMax;
	private Double volumeStd;
	private long resourceTypeCount;
	private Integer severityFault;
	private long featureCount;
	private String resourceType;
	private long eventPerId;
	private String eventType;
	private long locationCount;
	
	
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Integer id <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInteger", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Integer getId() {
		return id;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'ID'.
	 * @param id Integer <b>Obligatoire</b>
	 */
	public void setId(final Integer id) {
		this.id = id;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Event Type'.
	 * @return String eventType <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Event Type")
	public String getEventType() {
		return eventType;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Event Type'.
	 * @param eventType String <b>Obligatoire</b>
	 */
	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLogFeature() {
		return logFeature;
	}

	public void setLogFeature(String logFeature) {
		this.logFeature = logFeature;
	}

	public String getSeverityType() {
		return severityType;
	}

	public void setSeverityType(String severityType) {
		this.severityType = severityType;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getVolumeMean() {
		return volumeMean;
	}

	public void setVolumeMean(Double volumeMean) {
		this.volumeMean = volumeMean;
	}

	public Double getVolumeSum() {
		return volumeSum;
	}

	public void setVolumeSum(Double volumeSum) {
		this.volumeSum = volumeSum;
	}

	public Double getVolumeMin() {
		return volumeMin;
	}

	public void setVolumeMin(Double volumeMin) {
		this.volumeMin = volumeMin;
	}

	public Double getVolumeMax() {
		return volumeMax;
	}

	public void setVolumeMax(Double volumeMax) {
		this.volumeMax = volumeMax;
	}

	public Double getVolumeStd() {
		return volumeStd;
	}

	public void setVolumeStd(Double volumeStd) {
		this.volumeStd = volumeStd;
	}

	public long getResourceTypeCount() {
		return resourceTypeCount;
	}

	public void setResourceTypeCount(long resourceTypeCount) {
		this.resourceTypeCount = resourceTypeCount;
	}

	public Integer getSeverityFault() {
		return severityFault;
	}

	public void setSeverityFault(Integer severityFault) {
		this.severityFault = severityFault;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public long getFeatureCount() {
		return featureCount;
	}

	public void setFeatureCount(long featureCount) {
		this.featureCount = featureCount;
	}

	public long getEventPerId() {
		return eventPerId;
	}

	public void setEventPerId(long eventPerId) {
		this.eventPerId = eventPerId;
	}

	public long getLocationCount() {
		return locationCount;
	}

	public void setLocationCount(long locationCount) {
		this.locationCount = locationCount;
	}
}
