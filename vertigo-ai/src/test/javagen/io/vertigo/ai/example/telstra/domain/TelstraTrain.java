package io.vertigo.ai.example.telstra.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
@io.vertigo.datamodel.structure.stereotype.DataSpace("train")
public final class TelstraTrain implements Entity {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer code;
	private Integer codeCount;
	private String logFeature;
	private Integer volume;
	private Integer severityFault;
	private Integer winLocationVolumeSum;
	private Integer winLocationVolumeAvg;
	private Integer winLocationVolumeMin;
	private Integer winLocationVolumeMax;
	private Integer winLocationVolumeCount;
	private Integer countFeature204;
	private Integer countFeature205;
	private String severityType;
	private Integer sumFeature204Volume;
	private Integer sumFeature205Volume;
	private Integer avgFeature204Volume;
	private Integer avgFeature205Volume;
	private Integer minFeature204Volume;
	private Integer minFeature205Volume;
	private Integer maxFeature204Volume;
	private Integer maxFeature205Volume;
	private Integer countFeature204Volume;
	private Integer countFeature205Volume;

	/** {@inheritDoc} */
	@Override
	public UID<TelstraTrain> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long id <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getId() {
		return id;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param id Long <b>Obligatoire</b>
	 */
	public void setId(final Long id) {
		this.id = id;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Code'.
	 * @return Integer code
	 */
	@Field(smartType = "STyInteger", label = "Code")
	public Integer getCode() {
		return code;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Code'.
	 * @param code Integer
	 */
	public void setCode(final Integer code) {
		this.code = code;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'code_count'.
	 * @return Integer codeCount
	 */
	@Field(smartType = "STyInteger", label = "code_count")
	public Integer getCodeCount() {
		return codeCount;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'code_count'.
	 * @param codeCount Integer
	 */
	public void setCodeCount(final Integer codeCount) {
		this.codeCount = codeCount;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Log Feature'.
	 * @return String logFeature
	 */
	@Field(smartType = "STyLabel", label = "Log Feature")
	public String getLogFeature() {
		return logFeature;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Log Feature'.
	 * @param logFeature String
	 */
	public void setLogFeature(final String logFeature) {
		this.logFeature = logFeature;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Volume'.
	 * @return Integer volume
	 */
	@Field(smartType = "STyInteger", label = "Volume")
	public Integer getVolume() {
		return volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Volume'.
	 * @param volume Integer
	 */
	public void setVolume(final Integer volume) {
		this.volume = volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Severity Fault'.
	 * @return Integer severityFault
	 */
	@Field(smartType = "STyInteger", label = "Severity Fault")
	public Integer getSeverityFault() {
		return severityFault;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Severity Fault'.
	 * @param severityFault Integer
	 */
	public void setSeverityFault(final Integer severityFault) {
		this.severityFault = severityFault;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'win_location_volume_sum'.
	 * @return Integer winLocationVolumeSum
	 */
	@Field(smartType = "STyInteger", label = "win_location_volume_sum")
	public Integer getWinLocationVolumeSum() {
		return winLocationVolumeSum;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'win_location_volume_sum'.
	 * @param winLocationVolumeSum Integer
	 */
	public void setWinLocationVolumeSum(final Integer winLocationVolumeSum) {
		this.winLocationVolumeSum = winLocationVolumeSum;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'win_location_volume_avg'.
	 * @return Integer winLocationVolumeAvg
	 */
	@Field(smartType = "STyInteger", label = "win_location_volume_avg")
	public Integer getWinLocationVolumeAvg() {
		return winLocationVolumeAvg;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'win_location_volume_avg'.
	 * @param winLocationVolumeAvg Integer
	 */
	public void setWinLocationVolumeAvg(final Integer winLocationVolumeAvg) {
		this.winLocationVolumeAvg = winLocationVolumeAvg;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'win_location_volume_min'.
	 * @return Integer winLocationVolumeMin
	 */
	@Field(smartType = "STyInteger", label = "win_location_volume_min")
	public Integer getWinLocationVolumeMin() {
		return winLocationVolumeMin;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'win_location_volume_min'.
	 * @param winLocationVolumeMin Integer
	 */
	public void setWinLocationVolumeMin(final Integer winLocationVolumeMin) {
		this.winLocationVolumeMin = winLocationVolumeMin;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'win_location_volume_max'.
	 * @return Integer winLocationVolumeMax
	 */
	@Field(smartType = "STyInteger", label = "win_location_volume_max")
	public Integer getWinLocationVolumeMax() {
		return winLocationVolumeMax;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'win_location_volume_max'.
	 * @param winLocationVolumeMax Integer
	 */
	public void setWinLocationVolumeMax(final Integer winLocationVolumeMax) {
		this.winLocationVolumeMax = winLocationVolumeMax;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'win_location_volume_count'.
	 * @return Integer winLocationVolumeCount
	 */
	@Field(smartType = "STyInteger", label = "win_location_volume_count")
	public Integer getWinLocationVolumeCount() {
		return winLocationVolumeCount;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'win_location_volume_count'.
	 * @param winLocationVolumeCount Integer
	 */
	public void setWinLocationVolumeCount(final Integer winLocationVolumeCount) {
		this.winLocationVolumeCount = winLocationVolumeCount;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'count_feature_204'.
	 * @return Integer countFeature204
	 */
	@Field(smartType = "STyInteger", label = "count_feature_204")
	public Integer getCountFeature204() {
		return countFeature204;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'count_feature_204'.
	 * @param countFeature204 Integer
	 */
	public void setCountFeature204(final Integer countFeature204) {
		this.countFeature204 = countFeature204;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'count_feature_205'.
	 * @return Integer countFeature205
	 */
	@Field(smartType = "STyInteger", label = "count_feature_205")
	public Integer getCountFeature205() {
		return countFeature205;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'count_feature_205'.
	 * @param countFeature205 Integer
	 */
	public void setCountFeature205(final Integer countFeature205) {
		this.countFeature205 = countFeature205;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'severity_type'.
	 * @return String severityType
	 */
	@Field(smartType = "STyLabel", label = "severity_type")
	public String getSeverityType() {
		return severityType;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'severity_type'.
	 * @param severityType String
	 */
	public void setSeverityType(final String severityType) {
		this.severityType = severityType;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'sum_feature_204_volume'.
	 * @return Integer sumFeature204Volume
	 */
	@Field(smartType = "STyInteger", label = "sum_feature_204_volume")
	public Integer getSumFeature204Volume() {
		return sumFeature204Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'sum_feature_204_volume'.
	 * @param sumFeature204Volume Integer
	 */
	public void setSumFeature204Volume(final Integer sumFeature204Volume) {
		this.sumFeature204Volume = sumFeature204Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'sum_feature_205_volume'.
	 * @return Integer sumFeature205Volume
	 */
	@Field(smartType = "STyInteger", label = "sum_feature_205_volume")
	public Integer getSumFeature205Volume() {
		return sumFeature205Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'sum_feature_205_volume'.
	 * @param sumFeature205Volume Integer
	 */
	public void setSumFeature205Volume(final Integer sumFeature205Volume) {
		this.sumFeature205Volume = sumFeature205Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'avg_feature_204_volume'.
	 * @return Integer avgFeature204Volume
	 */
	@Field(smartType = "STyInteger", label = "avg_feature_204_volume")
	public Integer getAvgFeature204Volume() {
		return avgFeature204Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'avg_feature_204_volume'.
	 * @param avgFeature204Volume Integer
	 */
	public void setAvgFeature204Volume(final Integer avgFeature204Volume) {
		this.avgFeature204Volume = avgFeature204Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'avg_feature_205_volume'.
	 * @return Integer avgFeature205Volume
	 */
	@Field(smartType = "STyInteger", label = "avg_feature_205_volume")
	public Integer getAvgFeature205Volume() {
		return avgFeature205Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'avg_feature_205_volume'.
	 * @param avgFeature205Volume Integer
	 */
	public void setAvgFeature205Volume(final Integer avgFeature205Volume) {
		this.avgFeature205Volume = avgFeature205Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'min_feature_204_volume'.
	 * @return Integer minFeature204Volume
	 */
	@Field(smartType = "STyInteger", label = "min_feature_204_volume")
	public Integer getMinFeature204Volume() {
		return minFeature204Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'min_feature_204_volume'.
	 * @param minFeature204Volume Integer
	 */
	public void setMinFeature204Volume(final Integer minFeature204Volume) {
		this.minFeature204Volume = minFeature204Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'min_feature_205_volume'.
	 * @return Integer minFeature205Volume
	 */
	@Field(smartType = "STyInteger", label = "min_feature_205_volume")
	public Integer getMinFeature205Volume() {
		return minFeature205Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'min_feature_205_volume'.
	 * @param minFeature205Volume Integer
	 */
	public void setMinFeature205Volume(final Integer minFeature205Volume) {
		this.minFeature205Volume = minFeature205Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'max_feature_204_volume'.
	 * @return Integer maxFeature204Volume
	 */
	@Field(smartType = "STyInteger", label = "max_feature_204_volume")
	public Integer getMaxFeature204Volume() {
		return maxFeature204Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'max_feature_204_volume'.
	 * @param maxFeature204Volume Integer
	 */
	public void setMaxFeature204Volume(final Integer maxFeature204Volume) {
		this.maxFeature204Volume = maxFeature204Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'max_feature_205_volume'.
	 * @return Integer maxFeature205Volume
	 */
	@Field(smartType = "STyInteger", label = "max_feature_205_volume")
	public Integer getMaxFeature205Volume() {
		return maxFeature205Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'max_feature_205_volume'.
	 * @param maxFeature205Volume Integer
	 */
	public void setMaxFeature205Volume(final Integer maxFeature205Volume) {
		this.maxFeature205Volume = maxFeature205Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'count_feature_204_volume'.
	 * @return Integer countFeature204Volume
	 */
	@Field(smartType = "STyInteger", label = "count_feature_204_volume")
	public Integer getCountFeature204Volume() {
		return countFeature204Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'count_feature_204_volume'.
	 * @param countFeature204Volume Integer
	 */
	public void setCountFeature204Volume(final Integer countFeature204Volume) {
		this.countFeature204Volume = countFeature204Volume;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'count_feature_205_volume'.
	 * @return Integer countFeature205Volume
	 */
	@Field(smartType = "STyInteger", label = "count_feature_205_volume")
	public Integer getCountFeature205Volume() {
		return countFeature205Volume;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'count_feature_205_volume'.
	 * @param countFeature205Volume Integer
	 */
	public void setCountFeature205Volume(final Integer countFeature205Volume) {
		this.countFeature205Volume = countFeature205Volume;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
