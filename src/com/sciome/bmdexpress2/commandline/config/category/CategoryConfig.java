package com.sciome.bmdexpress2.commandline.config.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = DefinedConfig.class, name = "defined"),
		@Type(value = GOConfig.class, name = "go"), @Type(value = PathwayConfig.class, name = "pathway"),
		@Type(value = GeneLevelConfig.class, name = "gene") })
public abstract class CategoryConfig
{
	// name of bmdanalysis to cateogorize
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Boolean	removePromiscuousProbes;
	private Boolean	removeBMDGreaterHighDose;

	private Double	bmdPValueCutoff;
	private Double	bmdBMDLRatioMin;
	private Double	bmduBMDRatioMin;
	private Double	bmduBMDLRatioMin;
	private Double	nFoldBelowLowestDose;
	private Double	maxFoldChange;
	private Double	prefilterPValueMin;
	private Double	prefilterAdjustedPValueMin;

	private Boolean	identifyConflictingProbeSets;
	private Double	correlationCutoffForConflictingProbeSets;

	private Boolean	deduplicateGeneSets	= false;

	public String getInputName()
	{
		return inputName;
	}

	public void setInputName(String inputName)
	{
		this.inputName = inputName;
	}

	public String getOutputName()
	{
		return outputName;
	}

	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	public Boolean getRemovePromiscuousProbes()
	{
		return removePromiscuousProbes;
	}

	public void setRemovePromiscuousProbes(Boolean removePromiscuousProbes)
	{
		this.removePromiscuousProbes = removePromiscuousProbes;
	}

	public Boolean getRemoveBMDGreaterHighDose()
	{
		return removeBMDGreaterHighDose;
	}

	public void setRemoveBMDGreaterHighDose(Boolean removeBMDGreaterHighDose)
	{
		this.removeBMDGreaterHighDose = removeBMDGreaterHighDose;
	}

	public Double getBmdPValueCutoff()
	{
		return bmdPValueCutoff;
	}

	public void setBmdPValueCutoff(Double bmdPValueCutoff)
	{
		this.bmdPValueCutoff = bmdPValueCutoff;
	}

	public Double getBmdBMDLRatioMin()
	{
		return bmdBMDLRatioMin;
	}

	public void setBmdBMDLRatioMin(Double bmdBMDLRatioMin)
	{
		this.bmdBMDLRatioMin = bmdBMDLRatioMin;
	}

	public Double getBmduBMDRatioMin()
	{
		return bmduBMDRatioMin;
	}

	public void setBmduBMDRatioMin(Double bmduBMDRatioMin)
	{
		this.bmduBMDRatioMin = bmduBMDRatioMin;
	}

	public Double getBmduBMDLRatioMin()
	{
		return bmduBMDLRatioMin;
	}

	public void setBmduBMDLRatioMin(Double bmduBMDLRatioMin)
	{
		this.bmduBMDLRatioMin = bmduBMDLRatioMin;
	}

	public Double getnFoldBelowLowestDose()
	{
		return nFoldBelowLowestDose;
	}

	public void setnFoldBelowLowestDose(Double nFoldBelowLowestDose)
	{
		this.nFoldBelowLowestDose = nFoldBelowLowestDose;
	}

	public Double getMaxFoldChange()
	{
		return maxFoldChange;
	}

	public void setMaxFoldChange(Double maxFoldChange)
	{
		this.maxFoldChange = maxFoldChange;
	}

	public Double getPrefilterPValueMin()
	{
		return prefilterPValueMin;
	}

	public void setPrefilterPValueMin(Double pValueMin)
	{
		this.prefilterPValueMin = pValueMin;
	}

	public Double getPrefilterAdjustedPValueMin()
	{
		return prefilterAdjustedPValueMin;
	}

	public void setPrefilterAdjustedPValueMin(Double adjustedPValueMin)
	{
		this.prefilterAdjustedPValueMin = adjustedPValueMin;
	}

	public Boolean getIdentifyConflictingProbeSets()
	{
		return identifyConflictingProbeSets;
	}

	public void setIdentifyConflictingProbeSets(Boolean identifyConflictingProbeSets)
	{
		this.identifyConflictingProbeSets = identifyConflictingProbeSets;
	}

	public Double getCorrelationCutoffForConflictingProbeSets()
	{
		return correlationCutoffForConflictingProbeSets;
	}

	public void setCorrelationCutoffForConflictingProbeSets(Double correlationCutoffForConflictingProbeSets)
	{
		this.correlationCutoffForConflictingProbeSets = correlationCutoffForConflictingProbeSets;
	}

	public Boolean getDeduplicateGeneSets()
	{
		return deduplicateGeneSets;
	}

	public void setDeduplicateGeneSets(Boolean deduplicateGeneSets)
	{
		this.deduplicateGeneSets = deduplicateGeneSets;
	}

}
